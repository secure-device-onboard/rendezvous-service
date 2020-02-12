// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils.validators;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.exceptions.FailedCertChainVerificationException;
import org.sdo.rendezvous.model.types.Cert;
import org.sdo.rendezvous.model.types.CertChain;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CertChainValidator {

  private static final String FAILED_CERT_CHAIN_VERIFICATION = "Certificate chain is invalid.";

  private static final String ECDSA_ALGORITHM = "EC";
  private static final String SECP384R1_STRING = "secp384r1";
  private static final String SECP256R1_STRING = "secp256r1";
  private static final String PKIX_CERT_PATH_VALIDATOR_ALGORITHM = "PKIX";
  private static final int KEY_SIZE_256 = 256;
  private static final int KEY_SIZE_384 = 384;
  private static final int KEY_USAGE_EXTENSION_INDEX = 0;

  /**
   * Validates the cerificate chain.
   *
   * @param certChain the specificated CertChain
   * @throws FailedCertChainVerificationException if the certChain is invalid
   * @throws IOException if an I/O error occurs
   */
  public void validateCertChain(CertChain certChain)
      throws FailedCertChainVerificationException, IOException {
    try {
      InputStream certByteStream;
      CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
      LinkedList<X509Certificate> x509certs = new LinkedList<>();

      for (Cert cert : certChain.getCerts()) {
        certByteStream = new ByteArrayInputStream(cert.getCertBytes());
        X509Certificate x509Cert =
            (X509Certificate) certFactory.generateCertificate(certByteStream);
        x509certs.add(x509Cert);
        certByteStream.close();
      }

      X509Certificate leafCertificate = x509certs.getFirst();
      verifyLeafPubKeyData(leafCertificate);
      verifyLeafCertPrivileges(leafCertificate);

      CertPath certPath = certFactory.generateCertPath(x509certs);
      X509Certificate rootCertificate = x509certs.getLast();
      PKIXParameters certPathParams = configureAndGetCertPathParams(rootCertificate);
      CertPathValidator certPathValidator =
          CertPathValidator.getInstance(PKIX_CERT_PATH_VALIDATOR_ALGORITHM);
      certPathValidator.validate(certPath, certPathParams);

    } catch (CertificateException
        | CertPathValidatorException
        | NoSuchAlgorithmException
        | InvalidAlgorithmParameterException e) {
      log.info(
          String.format("Certificate chain validation has failed. Error: %s.", e.getMessage()));
      throw new FailedCertChainVerificationException(FAILED_CERT_CHAIN_VERIFICATION);
    }
  }

  private PKIXParameters configureAndGetCertPathParams(X509Certificate x509Certificate)
      throws InvalidAlgorithmParameterException {
    Set<TrustAnchor> trustedCerts = new HashSet<>();
    trustedCerts.add(new TrustAnchor(x509Certificate, null));

    PKIXParameters certPathParams = new PKIXParameters(trustedCerts);
    certPathParams.setRevocationEnabled(false);
    certPathParams.setDate(new Date());
    return certPathParams;
  }

  private void verifyLeafPubKeyData(X509Certificate cert)
      throws FailedCertChainVerificationException {
    String publicKeyAlgorithm = cert.getPublicKey().getAlgorithm();
    verifyAlgorithm(publicKeyAlgorithm);

    String ecdsaCurveName = ((ECPublicKey) cert.getPublicKey()).getParams().toString();
    verifyCurveName(ecdsaCurveName);

    int pubKeySize =
        ((ECPublicKey) cert.getPublicKey()).getParams().getCurve().getField().getFieldSize();
    verifyKeySize(pubKeySize);
  }

  private void verifyLeafCertPrivileges(X509Certificate cert)
      throws FailedCertChainVerificationException {
    if (cert.getKeyUsage() != null) {
      if (!(cert.getKeyUsage()[KEY_USAGE_EXTENSION_INDEX])) {
        log.info("Digital signature is not allowed for the device certificate.");
        throw new FailedCertChainVerificationException(FAILED_CERT_CHAIN_VERIFICATION);
      }
    }
  }

  private void verifyAlgorithm(String algorithmName) throws FailedCertChainVerificationException {
    if (!(algorithmName.equals(ECDSA_ALGORITHM))) {
      log.info(
          String.format(
              "Wrong public key algorithm inside the device certificate - supported: "
                  + "ECDSA, received: %s.",
              algorithmName));
      throw new FailedCertChainVerificationException(FAILED_CERT_CHAIN_VERIFICATION);
    }
  }

  private void verifyCurveName(String ecdsaCurveName) throws FailedCertChainVerificationException {
    if (!(ecdsaCurveName.contains(SECP256R1_STRING) || ecdsaCurveName.contains(SECP384R1_STRING))) {
      log.info("Mismatch of ECDSA curve type.");
      throw new FailedCertChainVerificationException(FAILED_CERT_CHAIN_VERIFICATION);
    }
  }

  private void verifyKeySize(int pubKeySize) throws FailedCertChainVerificationException {
    if (pubKeySize != KEY_SIZE_256 && pubKeySize != KEY_SIZE_384) {
      log.info("Wrong public key size. Received public key size: {}.", pubKeySize);
      throw new FailedCertChainVerificationException(FAILED_CERT_CHAIN_VERIFICATION);
    }
  }
}
