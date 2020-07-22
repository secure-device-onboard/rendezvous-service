// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import java.io.ByteArrayOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.sdo.rendezvous.exceptions.InternalCryptoException;
import org.sdo.rendezvous.exceptions.ResourceNotFoundException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PkOnDieEcdsaEncTest {

  private KeyPair validKeyPair;

  @BeforeMethod
  public void setUp() throws Exception {
    validKeyPair = generateEcKeyPair();
  }

  @Test
  public void testGetPublicKey() throws Exception {
    KeyPair keyPair = generateEcKeyPair();
    PublicKey publicKey = keyPair.getPublic();
    PkOnDieEcdsaEnc pkOnDieEcdsaEnc =
        new PkOnDieEcdsaEnc(PublicKeyType.ONDIE_ECDSA_384, publicKey.getEncoded());
    Assert.assertEquals(publicKey.getEncoded(), pkOnDieEcdsaEnc.asJavaPublicKey().getEncoded());
  }

  @Test(expectedExceptions = InternalCryptoException.class)
  public void testGetPublicKeyByteArrayTooLong() throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    outputStream.write((byte) 0);
    outputStream.write(validKeyPair.getPublic().getEncoded());

    PkOnDieEcdsaEnc pkOnDieEcdsaEnc =
        new PkOnDieEcdsaEnc(PublicKeyType.ONDIE_ECDSA_384, outputStream.toByteArray());
    pkOnDieEcdsaEnc.asJavaPublicKey();
  }

  @Test(expectedExceptions = InternalCryptoException.class)
  public void testGetPublicKeyByteArrayTooShort() throws Exception {
    byte[] tooShortPublicKey =
        Arrays.copyOfRange(
            validKeyPair.getPublic().getEncoded(),
            0,
            validKeyPair.getPublic().getEncoded().length - 1);
    PkOnDieEcdsaEnc pkOnDieEcdsaEnc =
        new PkOnDieEcdsaEnc(PublicKeyType.ONDIE_ECDSA_384, tooShortPublicKey);
    pkOnDieEcdsaEnc.asJavaPublicKey();
  }

  @Test(expectedExceptions = InternalCryptoException.class)
  public void testGetPublicKeyInvalidBytes() throws Exception {
    byte[] invalidPublicKey = validKeyPair.getPublic().getEncoded();
    invalidPublicKey[9] = (byte) (invalidPublicKey[9] + 1);
    PkOnDieEcdsaEnc pkOnDieEcdsaEnc =
        new PkOnDieEcdsaEnc(PublicKeyType.ONDIE_ECDSA_384, invalidPublicKey);
    pkOnDieEcdsaEnc.asJavaPublicKey();
  }

  @Test(expectedExceptions = ResourceNotFoundException.class)
  public void testGetPublicKeyByteArrayNull() throws Exception {
    PkOnDieEcdsaEnc pkOnDieEcdsaEnc = new PkOnDieEcdsaEnc(PublicKeyType.ONDIE_ECDSA_384, null);
    pkOnDieEcdsaEnc.asJavaPublicKey();
  }

  @Test(expectedExceptions = InternalCryptoException.class)
  public void testGetPublicKeyByteArrayEmpty() throws Exception {
    PkOnDieEcdsaEnc pkOnDieEcdsaEnc =
        new PkOnDieEcdsaEnc(PublicKeyType.ONDIE_ECDSA_384, new byte[] {});
    pkOnDieEcdsaEnc.asJavaPublicKey();
  }

  private KeyPair generateEcKeyPair() throws Exception {
    ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp384r1");
    KeyPairGenerator g = KeyPairGenerator.getInstance("EC", new BouncyCastleProvider());
    g.initialize(ecSpec, new SecureRandom());
    return g.generateKeyPair();
  }
}
