// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services.signature;

import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import javax.xml.bind.DatatypeConverter;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sdo.rendezvous.exceptions.InvalidEpidSignatureException;
import org.sdo.rendezvous.exceptions.InvalidPublicKeyTypeException;
import org.sdo.rendezvous.model.types.AppId;
import org.sdo.rendezvous.model.types.PkEcdsaEnc;
import org.sdo.rendezvous.model.types.PkEpidEnc;
import org.sdo.rendezvous.model.types.ProveToSdoBody;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.sdo.rendezvous.services.EpidVerificationService;
import org.springframework.web.client.HttpClientErrorException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EpidSignatureVerifierTest {

  private static final int AI_TYPE = 2;
  private static final byte[] AI_BYTES = DatatypeConverter.parseHexBinary("0000");
  private static final byte[] NONCE = DatatypeConverter.parseHexBinary("1234");
  private static final byte[] GUID =
      DatatypeConverter.parseHexBinary("00000000000000000000000000000001");
  private static final byte[] SIGNATURE_BINARY = DatatypeConverter.parseHexBinary("0000");
  private static final ProveToSdoBody PROVE_BODY =
      new ProveToSdoBody(new AppId(AI_TYPE, AI_BYTES), NONCE, GUID);
  private static final byte[] EPID_KEY_BINARY =
      DatatypeConverter.parseHexBinary("00000005AF2121201321100000000005");
  private static final byte[] ECDSA_KEY_BINARY =
      DatatypeConverter.parseHexBinary("212dA2121F212121C21C5FA2121C121B2121212E121FF2");
  private static final PkEpidEnc PUBLIC_KEY_EPID_11 =
      new PkEpidEnc(PublicKeyType.EPID_1_1, EPID_KEY_BINARY);
  private static final PkEpidEnc PUBLIC_KEY_EPID_20 =
      new PkEpidEnc(PublicKeyType.EPID_1_1, EPID_KEY_BINARY);
  private static final PkEcdsaEnc PUBLIC_KEY_ECDSA =
      new PkEcdsaEnc(PublicKeyType.ECDSA_P_256, ECDSA_KEY_BINARY);

  @Mock private EpidVerificationService epidVerificationService;

  private EpidSignatureVerifier epidSignatureVerifier;

  @BeforeMethod
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    epidSignatureVerifier = new EpidSignatureVerifier(epidVerificationService);
  }

  @Test
  public void testVerifySignatureEpid11() throws Exception {
    epidSignatureVerifier.verify(PROVE_BODY, PUBLIC_KEY_EPID_11, SIGNATURE_BINARY);
  }

  @Test
  public void testVerifySignatureEpid20() throws Exception {
    epidSignatureVerifier.verify(PROVE_BODY, PUBLIC_KEY_EPID_20, SIGNATURE_BINARY);
  }

  @Test(expectedExceptions = InvalidPublicKeyTypeException.class)
  public void testVerifySignatureInvalidPublicKeyType() throws Exception {
    epidSignatureVerifier.verify(PROVE_BODY, PUBLIC_KEY_ECDSA, SIGNATURE_BINARY);
  }

  @Test(expectedExceptions = InvalidEpidSignatureException.class)
  public void testVerifySignatureVerificationServiceThrowHttpException() throws Exception {
    Mockito.doThrow(HttpClientErrorException.class)
        .when(epidVerificationService)
        .verifySignature(any(), any(), any(), any());
    epidSignatureVerifier.verify(PROVE_BODY, PUBLIC_KEY_EPID_20, SIGNATURE_BINARY);
  }

  @Test(expectedExceptions = InvalidEpidSignatureException.class)
  public void testVerifySignatureVerificationServiceThrowIoException() throws Exception {
    Mockito.doThrow(IOException.class)
        .when(epidVerificationService)
        .verifySignature(any(), any(), any(), any());
    epidSignatureVerifier.verify(PROVE_BODY, PUBLIC_KEY_EPID_20, SIGNATURE_BINARY);
  }
}
