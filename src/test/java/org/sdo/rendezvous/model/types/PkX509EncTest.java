// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import java.security.interfaces.RSAPublicKey;
import javax.xml.bind.DatatypeConverter;
import org.bouncycastle.util.encoders.Base64;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PkX509EncTest {

  // ENCODED_PUBLIC_KEY_X509 it is public key in PEM format (without BEGIN and END section),
  // extracted from /Validation/TestData/OwnershipVoucher/p1.cert.pem
  // instruction how to extract public key you can find here
  // /Validation/TestData/OwnershipVoucher/openssl_instructions.md
  private static final byte[] ENCODED_PUBLIC_KEY_X509 =
      Base64.decode(
      "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlusIYmcFRPn95HyOT3ZR3Fi93QQVW4nIXK"
      + "rnUnE4+r0yMcPgc2F1yttPrm7YkrLorxrHzuSDjVzkFt0zBDsxM2EpSCePIWwRBLApJgSL/64Usu8v"
      + "WkcSrS2LGCalnYT3APBWgYZQYQ/U8xsOMX4o8MiMpNQPoonRPS0ty5IXsUHj3DQLI3cj5XMSRS5I+m"
      + "dTc7emv+9xZ8G6KcmcBqEjmf5msmBHAmI2TfdK0V3jSzSxR8xvrHyrzJp0a6A37xRa93I3MZhHnJyU"
      + "4WDQbLLHX+Ys6vHOwSBcgkgUCz8Tj+WrSjNLyXOlAeaCGmPN5eWHm6FpCOe9zBsebPKvq0rbYQIDAQAB");

  private PkX509Enc pkx509Enc;

  @Test
  public void testCreationPublicKeyFromEncodingPublicKeyX509() throws Exception {
    // given
    byte[] expectedModulus =
        DatatypeConverter.parseHexBinary(
          "0096eb0862670544f9fde47c8e4f7651dc58bddd04155b89c85caae7527138fabd3231c3e0736175cadb4"
            + "fae6ed892b2e8af1ac7cee4838d5ce416dd33043b3133612948278f216c1104b02926048bffae14b2"
            + "ef2f5a4712ad2d8b1826a59d84f700f056818650610fd4f31b0e317e28f0c88ca4d40fa289d13d2d2"
            + "dcb9217b141e3dc340b237723e57312452e48fa675373b7a6bfef7167c1ba29c99c06a12399fe66b2"
            + "60470262364df74ad15de34b34b147cc6fac7cabcc9a746ba037ef145af772373198479c9c94e160d"
            + "06cb2c75fe62ceaf1cec1205c8248140b3f138fe5ab4a334bc973a501e6821a63cde5e5879ba16908"
            + "e7bdcc1b1e6cf2afab4adb61");

    byte[] expectedExponent = DatatypeConverter.parseHexBinary("010001");

    pkx509Enc = new PkX509Enc(PublicKeyType.RSA2048RESTR, ENCODED_PUBLIC_KEY_X509);

    // when
    RSAPublicKey publicKey = (RSAPublicKey) pkx509Enc.asJavaPublicKey();

    // then
    Assert.assertEquals(publicKey.getModulus().toByteArray(), expectedModulus);
    Assert.assertEquals(publicKey.getPublicExponent().toByteArray(), expectedExponent);
    Assert.assertEquals(publicKey.getModulus().bitLength(), 2048);
    Assert.assertEquals(publicKey.getFormat(), "X.509");
  }
}
