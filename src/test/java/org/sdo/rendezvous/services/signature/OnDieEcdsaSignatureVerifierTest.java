// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services.signature;

import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Base64;
import javax.xml.bind.DatatypeConverter;
import org.sdo.rendezvous.authenticators.AuthenticatorFactory;
import org.sdo.rendezvous.exceptions.InvalidSignatureException;
import org.sdo.rendezvous.model.types.AppId;
import org.sdo.rendezvous.model.types.PkOnDieEcdsaEnc;
import org.sdo.rendezvous.model.types.ProveToSdoBody;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class OnDieEcdsaSignatureVerifierTest {

  private static final int AI_TYPE = 2;
  private static final byte[] AI_BYTES = DatatypeConverter.parseHexBinary("64616c74657374");
  private static final byte[] NONCE = DatatypeConverter.parseHexBinary("1234");
  private static final byte[] GUID =
      DatatypeConverter.parseHexBinary("00000000000000000000000000000001");
  private static final String SIGNATURE_BINARY =
      "ARDiywa9EaMjQZ0dNWO4CbxGEL0vujai1k2rk5D/baL+8xwBsQ4ZF/eL0V/yxtaafl11BJZ7rjnesm"
          + "/H8i6Hq3r8DeObqqGDo88mVnibvb9z3zlYlLahzLkwkhxsoTRRzXIQ6km2Dm6hQX5zmRkUDiFtzadw"
          + "MDfh+dPVQMlf/vNG1j5K";
  private static final ProveToSdoBody PROVE_BODY =
      new ProveToSdoBody(new AppId(AI_TYPE, AI_BYTES), NONCE, GUID);
  private static PkOnDieEcdsaEnc ecdsa384PubKey;
  String b64DeviceCert =
      "MIIBszCCATqgAwIBAgIQcYhLQDPbPylyGiZ0lFRLwzAKBggqhkjOPQQDAzAeMRwwGgYDVQQDDBNDU0"
          + "1FIFRHTCBEQUxfSTAxU0RFMB4XDTE5MDEwMTAwMDAwMFoXDTQ5MTIzMTIzNTk1OVowFzEVMBMGA1UE"
          + "AwwMREFMIEludGVsIFRBMHYwEAYHKoZIzj0CAQYFK4EEACIDYgAE044GJ2MiK44UHXubptTvkGefiy"
          + "rKO9ofn5v1yBVJcwpbYYTBjop/W01f7Gv7se7sMin8D1zfoEIQuahlijcsVWlG0CcB6LodLkxQi+IS"
          + "D8MNbObYIt8EGIacVVOgPdSho0QwQjAfBgNVHSMEGDAWgBSuPjAqQWKsFmeOf7U8OWyMbE+tfTAPBg"
          + "NVHRMBAf8EBTADAQEAMA4GA1UdDwEB/wQEAwIDyDAKBggqhkjOPQQDAwNnADBkAjAdss2kczBguN6s"
          + "iidupV+ipN8bCVAYe3eZV7c3i9rhTpHipVdII1/ppdswzl2IXQ0CMHNeOFuvHe64S9m2JRbBXUSdJ7"
          + "iNQwp/4+OdQUmWYs2mB7KqZpmDPGQkq5mDuygBaA==";

  private AuthenticatorFactory authenticatorFactory = new AuthenticatorFactory();
  private OnDieEcdsaSignatureVerifier verifier;

  PublicKey getPublicKey() throws CertificateException {
    byte[] certBytes = Base64.getDecoder().decode(b64DeviceCert);
    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
    Certificate cert = certificateFactory.generateCertificate(new ByteArrayInputStream(certBytes));
    return cert.getPublicKey();
  }

  /**
   * Variable initialization.
   *
   * @throws Exception for InvalidPublicKeyTypeException and any other unhandled exceptions
   */
  @BeforeMethod
  public void setUp() throws Exception {
    verifier = new OnDieEcdsaSignatureVerifier(authenticatorFactory);
    ecdsa384PubKey =
        new PkOnDieEcdsaEnc(PublicKeyType.ONDIE_ECDSA_384, getPublicKey().getEncoded());
  }

  @Test(expectedExceptions = InvalidSignatureException.class)
  public void testNegativeVerifySignatureOnDieEcdsa384() throws Exception {
    verifier.verify(PROVE_BODY, ecdsa384PubKey, SIGNATURE_BINARY.substring(1).getBytes());
  }
}
