// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.crypto;

import java.util.Base64;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.exceptions.InvalidJwtTokenException;
import org.sdo.rendezvous.utils.JWTokenTestUtils;
import org.sdo.rendezvous.utils.TimestampUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@PrepareForTest(TimestampUtils.class)
@PowerMockIgnore({"javax.*", "io.*"})
public class TO0JWTokenFactoryTest extends PowerMockTestCase {

  private static final String HMAC_KEY =
      "efe08a5df188dac44a4534c9f8ca0aed1b7ef6003622a07bf8a331134b3c1af427a6"
          + "d7ee298f25d7f911ae495a8356e4636c19ada91643bd0d8b4f7b2bb97156";
  private static final byte[] NONCE =
      DatatypeConverter.parseHexBinary("00000000000000000000000000000000");
  private static final long EXPIRED_TIMESTAMP = 1476723958960L;
  private static final String NONCE_KEY = "n3";
  private static final String EXPIRATION_TIMESTAMP_KEY = "exp";
  private static final String EXPECTED_TOKEN_HEADER = "eyJhbGciOiJIUzI1NiJ9";
  private static final int EXPIRATION_TIME = 900000; // 15 minutes in miliseconds
  private String builtToken;
  @InjectMocks private TO0JWTokenFactory tokenFactory;

  @Mock private RendezvousConfig rendezvousConfig;

  @BeforeMethod
  public void setUp() throws Exception {
    Mockito.when(rendezvousConfig.getToTokenExpirationTime()).thenReturn(EXPIRATION_TIME);
    builtToken = tokenFactory.buildToken(NONCE, HMAC_KEY);
  }

  @Test
  public void buildCorrectTokenTest() throws Exception {
    Assert.assertEquals(JWTokenTestUtils.getHeader(builtToken), EXPECTED_TOKEN_HEADER);
    Map<String, Object> bodyClaims = JWTokenTestUtils.getClaims(builtToken);

    Assert.assertEquals(Base64.getDecoder().decode((String) bodyClaims.get(NONCE_KEY)), NONCE);
    Assert.assertTrue(bodyClaims.containsKey(EXPIRATION_TIMESTAMP_KEY));
  }

  @Test
  public void getNonceFromTokenPositive() throws Exception {
    byte[] nonce = tokenFactory.getNonceFromToken(builtToken, HMAC_KEY);
    Assert.assertEquals(nonce, NONCE);
  }

  @Test(expectedExceptions = InvalidJwtTokenException.class)
  public void getNonceFromTokenExpiredTimestamp() throws Exception {
    PowerMockito.mockStatic(TimestampUtils.class);
    PowerMockito.when(TimestampUtils.getCurrent()).thenReturn(EXPIRED_TIMESTAMP);

    String expiredToken = tokenFactory.buildToken(NONCE, HMAC_KEY);
    tokenFactory.getNonceFromToken(expiredToken, HMAC_KEY);
  }

  @Test(expectedExceptions = InvalidJwtTokenException.class)
  public void getNonceFromTokenInvalidSignatureTest() throws Exception {
    tokenFactory.getNonceFromToken(builtToken.substring(0, builtToken.length() - 1), HMAC_KEY);
  }

  @Test(expectedExceptions = InvalidJwtTokenException.class)
  public void getNonceFromTokenInvalidHeaderTest() throws Exception {
    tokenFactory.getNonceFromToken(builtToken.substring(1), HMAC_KEY);
  }
}
