// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.exceptions.InvalidCertException;
import org.sdo.rendezvous.model.types.Cert;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CertUtils {

  /**
   * Returns a public key in format X509 as an array of bytes from the specified cert.
   *
   * @param cert the instance of Cert
   * @return the encoded form of public key
   * @throws InvalidCertException if a class initialization or certificate parsing error occurs, it
   *     is wrapper for CertificateException
   */
  public byte[] getPubKeyBytesFromCert(Cert cert) throws InvalidCertException {
    try {
      InputStream certByteStream = new ByteArrayInputStream(cert.getCertBytes());
      CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
      X509Certificate x509Cert = (X509Certificate) certFactory.generateCertificate(certByteStream);

      return x509Cert.getPublicKey().getEncoded();

    } catch (CertificateException e) {
      log.error(String.format("Problem retrieving certificate fields. Error: %s", e.getMessage()));
      throw new InvalidCertException("Problem retrieving certificate fields.");
    }
  }
}
