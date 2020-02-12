// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services;

import static org.mockito.ArgumentMatchers.any;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.crypto.TO1JWTokenFactory;
import org.sdo.rendezvous.exceptions.InvalidJwtTokenException;
import org.sdo.rendezvous.model.types.Device;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TokenParserServiceTest {

  private static final String JWT_TOKEN = "Bearer 124100AF1215EB121C212A";
  private static final String JWT_TOKEN_WITHOUT_BEARER = "124100AF1215EB121C212A";
  private static final String HMAC_SECRET = "123123456102421210151212";

  @Mock private RendezvousConfig rendezvousConfig;

  @Mock private TO1JWTokenFactory to1JwTokenFactory;
  private TokenParserService tokenParserService;

  @BeforeMethod
  public void beforeMethod() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(rendezvousConfig.getHmacSecret()).thenReturn(HMAC_SECRET);
    tokenParserService = new TokenParserService(to1JwTokenFactory, rendezvousConfig);
  }

  @Test
  public void testGetDeviceFromJwt() throws InvalidJwtTokenException {
    Device device = Mockito.mock(Device.class);
    Mockito.when(to1JwTokenFactory.parseToken(any(String.class), any(String.class)))
        .thenReturn(device);
    Device result = tokenParserService.getDeviceData(JWT_TOKEN);
    Assert.assertEquals(result, device);
  }

  @Test
  public void testValidateToken() throws InvalidJwtTokenException {
    tokenParserService.validateTokenPrefix(JWT_TOKEN);
  }

  @Test(expectedExceptions = InvalidJwtTokenException.class)
  public void testValidateTokenInvalidToken() throws InvalidJwtTokenException {
    tokenParserService.validateTokenPrefix(JWT_TOKEN_WITHOUT_BEARER);
  }
}
