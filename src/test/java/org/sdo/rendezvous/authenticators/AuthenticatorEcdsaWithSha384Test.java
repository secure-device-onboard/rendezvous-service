// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.authenticators;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Arrays;
import javax.xml.bind.DatatypeConverter;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.sdo.rendezvous.exceptions.InvalidSignatureException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AuthenticatorEcdsaWithSha384Test {

  private static final byte[] PAYLOAD =
      DatatypeConverter.parseHexBinary("6d662312123456123FFFAC6fe9993c665b1ce908ddea01cb75");
  private static byte[] VALID_SIGNATURE;
  private AuthenticatorEcdsaWithSha384 authenticator;
  private KeyPair validKeyPair;

  private KeyPair generateEcKeyPair() throws Exception {
    ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp384r1");
    KeyPairGenerator g = KeyPairGenerator.getInstance("EC", new BouncyCastleProvider());
    g.initialize(ecSpec, new SecureRandom());
    return g.generateKeyPair();
  }

  @BeforeMethod
  public void before() throws Exception {
    authenticator = new AuthenticatorEcdsaWithSha384();
    validKeyPair = generateEcKeyPair();

    Signature sig = Signature.getInstance("SHA384withECDSA", new BouncyCastleProvider());
    sig.initSign(validKeyPair.getPrivate());
    sig.update(PAYLOAD);
    VALID_SIGNATURE = sig.sign();
  }

  @Test
  public void testAuthenticateValidData() throws Exception {
    authenticator.authenticate(VALID_SIGNATURE, PAYLOAD, validKeyPair.getPublic());
  }

  @Test(expectedExceptions = InvalidSignatureException.class)
  public void testAuthenticateInvalidSignature() throws Exception {
    byte[] invalidSignature = Arrays.copyOf(VALID_SIGNATURE, VALID_SIGNATURE.length);
    invalidSignature[0] = (byte) (invalidSignature[0] + 1);
    authenticator.authenticate(invalidSignature, PAYLOAD, validKeyPair.getPublic());
  }

  @Test(expectedExceptions = InvalidSignatureException.class)
  public void testAuthenticateInvalidPayload() throws Exception {
    byte[] invalidPayload = Arrays.copyOf(PAYLOAD, PAYLOAD.length);
    invalidPayload[3] = (byte) (invalidPayload[3] + 1);
    authenticator.authenticate(VALID_SIGNATURE, invalidPayload, validKeyPair.getPublic());
  }

  @Test(expectedExceptions = InvalidSignatureException.class)
  public void testAuthenticateTooShortSignature() throws Exception {
    byte[] tooShortSignature = Arrays.copyOfRange(VALID_SIGNATURE, 0, VALID_SIGNATURE.length - 1);
    authenticator.authenticate(tooShortSignature, PAYLOAD, validKeyPair.getPublic());
  }

  @Test(expectedExceptions = InvalidSignatureException.class)
  public void testAuthenticateTooShortPayload() throws Exception {
    byte[] tooShortSignature = Arrays.copyOfRange(VALID_SIGNATURE, 0, VALID_SIGNATURE.length - 1);
    authenticator.authenticate(tooShortSignature, PAYLOAD, validKeyPair.getPublic());
  }

  @Test(expectedExceptions = InvalidSignatureException.class)
  public void testAuthenticateTooLongPayload() throws Exception {
    byte[] tooShortSignature = Arrays.copyOfRange(VALID_SIGNATURE, 0, VALID_SIGNATURE.length - 1);
    authenticator.authenticate(tooShortSignature, PAYLOAD, validKeyPair.getPublic());
  }

  @Test(expectedExceptions = InvalidSignatureException.class)
  public void testInvalidPubKeyEc() throws Exception {
    KeyPair keyPair = generateEcKeyPair();
    authenticator.authenticate(VALID_SIGNATURE, PAYLOAD, keyPair.getPublic());
  }
}
