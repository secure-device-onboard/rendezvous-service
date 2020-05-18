// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.crypto;

import static org.mockito.ArgumentMatchers.anyString;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.bouncycastle.util.encoders.Base64;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.sdo.rendezvous.model.types.HashType;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*"})
@PrepareForTest({HashGenerator.class, MessageDigest.class})
public class HashGeneratorTest extends PowerMockTestCase {
  private static HashType ALGHORITHM_TYPE_SHA_256 = HashType.SHA256;
  private static HashType ALGHORITHM_TYPE_SHA_384 = HashType.SHA384;
  private static HashType INVALID_ALGHORITHM_TYPE = HashType.NONE;
  private static byte[] VALID_BYTES = new byte[] {0x11, 0x22, 0x33};
  private static byte[] INPUT_DATA = "DATA".getBytes();

  private HashGenerator hashGenerator;

  @BeforeMethod
  public void setUp() {
    hashGenerator = new HashGenerator();
  }

  @Test
  public void testGenerate() throws NoSuchAlgorithmException {
    PowerMockito.mockStatic(MessageDigest.class);
    MessageDigest md = PowerMockito.mock(MessageDigest.class);
    PowerMockito.when(md.digest()).thenReturn(VALID_BYTES);
    PowerMockito.when(MessageDigest.getInstance(ALGHORITHM_TYPE_SHA_384.getAlgorithmName()))
        .thenReturn(md);

    byte[] hash = hashGenerator.generate(INPUT_DATA, ALGHORITHM_TYPE_SHA_384);
    Assert.assertEquals(hash, VALID_BYTES);
  }

  @Test
  public void testHashSha256() throws NoSuchAlgorithmException {
    PowerMockito.mockStatic(MessageDigest.class);
    MessageDigest md = PowerMockito.mock(MessageDigest.class);
    PowerMockito.when(MessageDigest.getInstance(ALGHORITHM_TYPE_SHA_256.getAlgorithmName()))
        .thenReturn(md);
    PowerMockito.when(md.digest()).thenReturn(VALID_BYTES);

    byte[] hash = hashGenerator.hashSha256(INPUT_DATA);
    Assert.assertEquals(hash, VALID_BYTES);
  }

  @SuppressWarnings("unchecked")
  @Test(expectedExceptions = NoSuchAlgorithmException.class)
  public void testGenerateWithInvalidMessageDigestInstance() throws NoSuchAlgorithmException {
    PowerMockito.mockStatic(MessageDigest.class);
    PowerMockito.when(MessageDigest.getInstance(anyString()))
        .thenThrow(NoSuchAlgorithmException.class);

    hashGenerator.generate(INPUT_DATA, INVALID_ALGHORITHM_TYPE);
  }

  @SuppressWarnings("unchecked")
  @Test(expectedExceptions = NoSuchAlgorithmException.class)
  public void testHashSha256WithInvalidMessageDigestInstance() throws NoSuchAlgorithmException {
    PowerMockito.mockStatic(MessageDigest.class);
    PowerMockito.when(MessageDigest.getInstance(anyString()))
        .thenThrow(NoSuchAlgorithmException.class);

    hashGenerator.hashSha256(INPUT_DATA);
  }

  @Test
  public void testHashOpPublicKeyFromModulusExponent()
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    // this test was created to generate fingerprint for encoding type RSAMODEEXP with key (RSA2048)

    // given
    byte[] modulusBytes =
        DatatypeConverter.parseHexBinary(
        "0096eb0862670544f9fde47c8e4f7651dc58bddd04155b89c85caae7527138fabd3231c3"
        + "e0736175cadb4fae6ed892b2e8af1ac7cee4838d5ce416dd33043b3133612948278f216c1104b02926048bf"
        + "fae14b2ef2f5a4712ad2d8b1826a59d84f700f056818650610fd4f31b0e317e28f0c88ca4d40fa289d13d2d"
        + "2dcb9217b141e3dc340b237723e57312452e48fa675373b7a6bfef7167c1ba29c99c06a12399fe66b260470"
        + "262364df74ad15de34b34b147cc6fac7cabcc9a746ba037ef145af772373198479c9c94e160d06cb2c75fe6"
        + "2ceaf1cec1205c8248140b3f138fe5ab4a334bc973a501e6821a63cde5e5879ba16908e7bdcc1b1e6cf2afa"
        + "b4adb61");
    byte[] exponentBytes = DatatypeConverter.parseHexBinary("010001");

    byte[] expectedFingerprint =
        DatatypeConverter.parseHexBinary(
            "0734FAC43DBE455D531930B6A8E024043356541BFFCC7A250E417EC38E217725");
    BigInteger modulus = new BigInteger(modulusBytes);
    BigInteger exponent = new BigInteger(exponentBytes);
    RSAPublicKey rsaPublicKey =
        (RSAPublicKey)
            KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, exponent));

    // when
    byte[] actualFingerprint = hashGenerator.hashSha256(rsaPublicKey.getEncoded());

    // then
    Assert.assertEquals(actualFingerprint, expectedFingerprint);
  }

  @Test
  public void testHashOpPublicKeyFromEncodedPublicKeyX509()
      throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
    // this test was created to generate fingerprint for encoding type X509 with key (RSA2048)

    // given
    byte[] encodedPublicKeyX509 =
      Base64.decode(
          "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlusIYmcFRPn95HyOT3ZR3Fi93QQVW4nIXKrnUnE"
        + "4+r0yMcPgc2F1yttPrm7YkrLorxrHzuSDjVzkFt0zBDsxM2EpSCePIWwRBLApJgSL/64Usu8vWkcSrS2LGC"
        + "alnYT3APBWgYZQYQ/U8xsOMX4o8MiMpNQPoonRPS0ty5IXsUHj3DQLI3cj5XMSRS5I+mdTc7emv+9xZ8G6K"
        + "cmcBqEjmf5msmBHAmI2TfdK0V3jSzSxR8xvrHyrzJp0a6A37xRa93I3MZhHnJyU4WDQbLLHX+Ys6vHOwSBc"
        + "gkgUCz8Tj+WrSjNLyXOlAeaCGmPN5eWHm6FpCOe9zBsebPKvq0rbYQIDAQAB");

    RSAPublicKey rsaPublicKey =
        (RSAPublicKey)
            KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(encodedPublicKeyX509));
    byte[] expectedFingerprint =
        DatatypeConverter.parseHexBinary(
            "0734FAC43DBE455D531930B6A8E024043356541BFFCC7A250E417EC38E217725");

    // when
    byte[] actualFingerprint = hashGenerator.hashSha256(rsaPublicKey.getEncoded());

    // then
    Assert.assertEquals(actualFingerprint, expectedFingerprint);
  }
}
