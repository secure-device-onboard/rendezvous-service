// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services.signature;

import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.SecureRandom;
import javax.xml.bind.DatatypeConverter;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sdo.rendezvous.authenticators.AuthenticatorEcdsaWithSha256;
import org.sdo.rendezvous.authenticators.AuthenticatorEcdsaWithSha384;
import org.sdo.rendezvous.authenticators.AuthenticatorFactory;
import org.sdo.rendezvous.exceptions.InvalidPublicKeyTypeException;
import org.sdo.rendezvous.model.types.AppId;
import org.sdo.rendezvous.model.types.PKECDSAEnc;
import org.sdo.rendezvous.model.types.PKEPIDEnc;
import org.sdo.rendezvous.model.types.PKNull;
import org.sdo.rendezvous.model.types.ProveToSdoBody;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EcdsaSignatureVerifierTest {

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
  private static final PKECDSAEnc PUBLIC_KEY_ECDSA =
      new PKECDSAEnc(PublicKeyType.ECDSA_P_256, ECDSA_KEY_BINARY);

  private static PKECDSAEnc ecdsa256PubKey;
  private static PKECDSAEnc ecdsa384PubKey;

  @Mock private AuthenticatorFactory authenticatorFactory;

  @Mock private AuthenticatorEcdsaWithSha256 authenticatorEcdsaWithSha256;

  @Mock private AuthenticatorEcdsaWithSha384 authenticatorEcdsaWithSha384;

  private EcdsaSignatureVerifier verifier;

  @BeforeMethod
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    verifier = new EcdsaSignatureVerifier(authenticatorFactory);
    ecdsa256PubKey = new PKECDSAEnc(PublicKeyType.NONE, generateEcPubKey("secp256r1").getEncoded());
    ecdsa384PubKey = new PKECDSAEnc(PublicKeyType.NONE, generateEcPubKey("secp384r1").getEncoded());

    Mockito.when(authenticatorFactory.getAuthenticator(PublicKeyType.ECDSA_P_256))
        .thenReturn(authenticatorEcdsaWithSha256);
    Mockito.when(authenticatorFactory.getAuthenticator(PublicKeyType.ECDSA_P_384))
        .thenReturn(authenticatorEcdsaWithSha384);
    Mockito.doThrow(InvalidPublicKeyTypeException.class)
        .when(authenticatorFactory)
        .getAuthenticator(PublicKeyType.NONE);
  }

  @Test
  public void testVerifySignatureEcdsa256() throws Exception {
    verifier.verify(PROVE_BODY, ecdsa256PubKey, SIGNATURE_BINARY);
  }

  @Test
  public void testVerifySignatureEcdsa384() throws Exception {
    verifier.verify(PROVE_BODY, ecdsa384PubKey, SIGNATURE_BINARY);
  }

  @Test(expectedExceptions = InvalidPublicKeyTypeException.class)
  public void testVerifySignatureInvalidPublicKeyType() throws Exception {
    verifier.verify(PROVE_BODY, new PKNull(), SIGNATURE_BINARY);
  }

  @Test(expectedExceptions = InvalidPublicKeyTypeException.class)
  public void testVerifySignatureEcdsa512() throws Exception {
    PKECDSAEnc ecdsa512PubKey =
        new PKECDSAEnc(PublicKeyType.NONE, generateEcPubKey("secp224k1").getEncoded());
    verifier.verify(PROVE_BODY, ecdsa512PubKey, SIGNATURE_BINARY);
  }

  private PublicKey generateEcPubKey(String curve) throws Exception {
    ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec(curve);
    KeyPairGenerator g = KeyPairGenerator.getInstance("EC", new BouncyCastleProvider());
    g.initialize(ecSpec, new SecureRandom());
    return g.generateKeyPair().getPublic();
  }
}
