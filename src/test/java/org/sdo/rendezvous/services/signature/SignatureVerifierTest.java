// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services.signature;

import javax.xml.bind.DatatypeConverter;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.exceptions.InvalidPublicKeyTypeException;
import org.sdo.rendezvous.model.types.AppId;
import org.sdo.rendezvous.model.types.PKECDSAEnc;
import org.sdo.rendezvous.model.types.PKEPIDEnc;
import org.sdo.rendezvous.model.types.ProveToSdoBody;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SignatureVerifierTest {

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
  private static final PKEPIDEnc PUBLIC_KEY_EPID_11 =
      new PKEPIDEnc(PublicKeyType.EPID_1_1, EPID_KEY_BINARY);
  private static final PKEPIDEnc PUBLIC_KEY_EPID_20 =
      new PKEPIDEnc(PublicKeyType.EPID_1_1, EPID_KEY_BINARY);
  private static final PKECDSAEnc PUBLIC_KEY_ECDSA_PROVE =
      new PKECDSAEnc(PublicKeyType.NONE, ECDSA_KEY_BINARY);

  @Mock private EpidSignatureVerifier epidSignatureVerifier;

  @Mock private EcdsaSignatureVerifier ecdsaSignatureVerifier;

  @Mock private RendezvousConfig rendezvousConfig;

  private SignatureVerifier signatureVerifier;

  @BeforeMethod
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(rendezvousConfig.isSignatureVerification()).thenReturn(true);
    signatureVerifier =
        new SignatureVerifier(epidSignatureVerifier, ecdsaSignatureVerifier, rendezvousConfig);
  }

  @Test
  public void testVerifySignatureEcdsa() throws Exception {
    signatureVerifier.verify(PROVE_BODY, PUBLIC_KEY_ECDSA_PROVE, SIGNATURE_BINARY);
  }

  @Test
  public void testVerifySignatureEpid11() throws Exception {
    signatureVerifier.verify(PROVE_BODY, PUBLIC_KEY_EPID_11, SIGNATURE_BINARY);
  }

  @Test
  public void testVerifySignatureEpid20() throws Exception {
    signatureVerifier.verify(PROVE_BODY, PUBLIC_KEY_EPID_20, SIGNATURE_BINARY);
  }

  @Test(expectedExceptions = InvalidPublicKeyTypeException.class)
  public void testVerifySignatureInvalidKeyType() throws Exception {
    signatureVerifier.verify(
        PROVE_BODY, new PKECDSAEnc(PublicKeyType.RSA2048RESTR, new byte[] {}), SIGNATURE_BINARY);
  }
}
