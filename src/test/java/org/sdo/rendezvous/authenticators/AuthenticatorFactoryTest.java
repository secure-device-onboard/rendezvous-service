// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.authenticators;

import org.sdo.rendezvous.exceptions.InvalidPublicKeyTypeException;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AuthenticatorFactoryTest {

  private AuthenticatorFactory authenticatorFactory;

  @BeforeMethod
  public void setUp() {
    authenticatorFactory = new AuthenticatorFactory();
  }

  @Test
  public void testGetAuthenticatorSha256() throws Exception {
    Authenticator authenticator = authenticatorFactory.getAuthenticator(PublicKeyType.ECDSA_P_256);
    Assert.assertTrue(authenticator instanceof AuthenticatorEcdsaWithSha256);
  }

  @Test
  public void testGetAuthenticatorSha384() throws Exception {
    Authenticator authenticator = authenticatorFactory.getAuthenticator(PublicKeyType.ECDSA_P_384);
    Assert.assertTrue(authenticator instanceof AuthenticatorEcdsaWithSha384);

    authenticator = authenticatorFactory.getAuthenticator(PublicKeyType.ONDIE_ECDSA_384);
    Assert.assertTrue(authenticator instanceof AuthenticatorOnDieEcdsaWithSha384);
  }

  @Test
  public void testGetAuthenticatorRsa() throws Exception {
    Authenticator authenticator = authenticatorFactory.getAuthenticator(PublicKeyType.RSA2048RESTR);
    Assert.assertTrue(authenticator instanceof AuthenticatorRsaWithSha256);
  }

  @Test(expectedExceptions = InvalidPublicKeyTypeException.class)
  public void testGetAuthenticatorInvalidHashType() throws Exception {
    authenticatorFactory.getAuthenticator(PublicKeyType.NONE);
  }
}
