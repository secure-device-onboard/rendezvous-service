// Copyright 2019 Intel Corporation
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

public class PKECDSAEncTest {

  private KeyPair validKeyPair;

  @BeforeMethod
  public void setUp() throws Exception {
    validKeyPair = generateEcKeyPair();
  }

  @Test
  public void testGetPublicKey() throws Exception {
    KeyPair keyPair = generateEcKeyPair();

    PublicKey publicKey = keyPair.getPublic();
    PKECDSAEnc pkEcdsaEnc = new PKECDSAEnc(PublicKeyType.ECDSA_P_256, publicKey.getEncoded());
    Assert.assertEquals(publicKey.getEncoded(), pkEcdsaEnc.asJavaPublicKey().getEncoded());
  }

  @Test(expectedExceptions = InternalCryptoException.class)
  public void testGetPublicKeyByteArrayTooLong() throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    outputStream.write((byte) 0);
    outputStream.write(validKeyPair.getPublic().getEncoded());

    PKECDSAEnc pkEcdsaEnc = new PKECDSAEnc(PublicKeyType.ECDSA_P_256, outputStream.toByteArray());
    pkEcdsaEnc.asJavaPublicKey();
  }

  @Test(expectedExceptions = InternalCryptoException.class)
  public void testGetPublicKeyByteArrayTooShort() throws Exception {
    byte[] tooShortPublicKey =
        Arrays.copyOfRange(
            validKeyPair.getPublic().getEncoded(),
            0,
            validKeyPair.getPublic().getEncoded().length - 1);
    PKECDSAEnc pkEcdsaEnc = new PKECDSAEnc(PublicKeyType.ECDSA_P_256, tooShortPublicKey);
    pkEcdsaEnc.asJavaPublicKey();
  }

  @Test(expectedExceptions = InternalCryptoException.class)
  public void testGetPublicKeyInvalidBytes() throws Exception {
    byte[] invalidPublicKey = validKeyPair.getPublic().getEncoded();
    invalidPublicKey[9] = (byte) (invalidPublicKey[9] + 1);
    PKECDSAEnc pkEcdsaEnc = new PKECDSAEnc(PublicKeyType.ECDSA_P_256, invalidPublicKey);
    pkEcdsaEnc.asJavaPublicKey();
  }

  @Test(expectedExceptions = ResourceNotFoundException.class)
  public void testGetPublicKeyByteArrayNull() throws Exception {
    PKECDSAEnc pkEcdsaEnc = new PKECDSAEnc(PublicKeyType.ECDSA_P_256, null);
    pkEcdsaEnc.asJavaPublicKey();
  }

  @Test(expectedExceptions = InternalCryptoException.class)
  public void testGetPublicKeyByteArrayEmpty() throws Exception {
    PKECDSAEnc pkEcdsaEnc = new PKECDSAEnc(PublicKeyType.ECDSA_P_256, new byte[] {});
    pkEcdsaEnc.asJavaPublicKey();
  }

  private KeyPair generateEcKeyPair() throws Exception {
    ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp384r1");
    KeyPairGenerator g = KeyPairGenerator.getInstance("EC", new BouncyCastleProvider());
    g.initialize(ecSpec, new SecureRandom());
    return g.generateKeyPair();
  }
}
