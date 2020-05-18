// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import java.security.interfaces.RSAPublicKey;
import javax.xml.bind.DatatypeConverter;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PkRmeEncTest {

  // modulus and exponent have been extracted from
  // /Validation/TestData/OwnershipVoucher/p1.cert.pem
  // instructions how to do it can be found
  // /Validation/TestData/OwnershipVoucher/openssl_instructions.md

  private static final byte[] MODULUS =
      DatatypeConverter.parseHexBinary(
      "0096eb0862670544f9fde47c8e4f7651dc58bddd04155b89c85caae7527138fabd3231c"
      + "3e0736175cadb4fae6ed892b2e8af1ac7cee4838d5ce416dd33043b3133612948278f216c1104b02926048b"
      + "ffae14b2ef2f5a4712ad2d8b1826a59d84f700f056818650610fd4f31b0e317e28f0c88ca4d40fa289d13d2"
      + "d2dcb9217b141e3dc340b237723e57312452e48fa675373b7a6bfef7167c1ba29c99c06a12399fe66b26047"
      + "0262364df74ad15de34b34b147cc6fac7cabcc9a746ba037ef145af772373198479c9c94e160d06cb2c75fe"
      + "62ceaf1cec1205c8248140b3f138fe5ab4a334bc973a501e6821a63cde5e5879ba16908e7bdcc1b1e6cf2af"
      + "ab4adb61");

  private static final byte[] EXPONENT = DatatypeConverter.parseHexBinary("010001");

  private PkRmeEnc pkrmeEnc;

  @Test
  public void testCreationPublicKeyFromModulusAndExponent() throws Exception {
    // given
    pkrmeEnc = new PkRmeEnc(PublicKeyType.RSA2048RESTR, MODULUS, EXPONENT);
    byte[] expectedExponent = new byte[] {0x01, 0x00, 0x01};

    // when
    RSAPublicKey publicKey = (RSAPublicKey) pkrmeEnc.asJavaPublicKey();

    // then
    Assert.assertEquals(publicKey.getAlgorithm(), "RSA");
    Assert.assertEquals(publicKey.getModulus().bitLength(), 2048);
    Assert.assertEquals(publicKey.getFormat(), "X.509");
    Assert.assertEquals(publicKey.getPublicExponent().toByteArray(), expectedExponent);
  }
}
