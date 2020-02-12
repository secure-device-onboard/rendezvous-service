// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.crypto;

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
import org.sdo.rendezvous.model.types.Device;
import org.sdo.rendezvous.utils.JWTokenTestUtils;
import org.sdo.rendezvous.utils.TimestampUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@PrepareForTest(TimestampUtils.class)
@PowerMockIgnore({"javax.*", "org.xml.sax.*"})
public class TO1JWTokenFactoryTest extends PowerMockTestCase {

  private static final String HMAC_KEY =
      "efe08a5df188dac44a4534c9f8ca0aed1b7ef6003622a07bf8a331134b3c1af4"
          + "27a6d7ee298f25d7f911ae495a8356e4636c19ada91643bd0d8b4f7b2bb97156";
  private static final byte[] GUID =
      DatatypeConverter.parseHexBinary("21EC20203AEA4069A2DD08002B30309D");
  private static final byte[] NONCE =
      DatatypeConverter.parseHexBinary("00000000000000000000000000000000");

  private static final int EXPIRATION_TIME = 900000; // 15 minutes in miliseconds
  private static final long EXPIRED_TIMESTAMP = 1476723958960L;

  private static final String EXPECTED_TOKEN_HEADER = "eyJhbGciOiJIUzI1NiJ9";
  private static final String EXPECTED_TOKEN_BODY_NO_TIMESTAMP =
      "eyJuNCI6IkFBQUFBQUFBQUFBQUFBQUFBQUFBQUE9PSIsImcyIjoiSWV3Z0lEcnFRR21pM1FnQUt6QXduUT09Iiw";

  @InjectMocks private TO1JWTokenFactory tokenFactory;

  @Mock private RendezvousConfig rendezvousConfig;

  private String builtToken;

  @BeforeMethod
  public void initBeans() {
    Mockito.when(rendezvousConfig.getToTokenExpirationTime()).thenReturn(EXPIRATION_TIME);
    builtToken = tokenFactory.buildToken(GUID, NONCE, HMAC_KEY);
  }

  @Test
  void testBuildTokenPositive() {
    Assert.assertEquals(JWTokenTestUtils.getHeader(builtToken), EXPECTED_TOKEN_HEADER);
    Assert.assertEquals(
        JWTokenTestUtils.getTO1BodyNoTimestamps(builtToken), EXPECTED_TOKEN_BODY_NO_TIMESTAMP);
  }

  @Test
  void testParseTokenPositive() throws InvalidJwtTokenException {
    Device device = tokenFactory.parseToken(builtToken, HMAC_KEY);
    Assert.assertEquals(device.getGuid(), GUID);
    Assert.assertEquals(device.getNonce(), NONCE);
  }

  @Test(expectedExceptions = InvalidJwtTokenException.class)
  public void parseTokenExpiredTimestamp() throws InvalidJwtTokenException {
    PowerMockito.mockStatic(TimestampUtils.class);
    PowerMockito.when(TimestampUtils.getCurrent()).thenReturn(EXPIRED_TIMESTAMP);
    String expiredToken = tokenFactory.buildToken(GUID, NONCE, HMAC_KEY);
    tokenFactory.parseToken(expiredToken, HMAC_KEY);
  }

  @Test(expectedExceptions = InvalidJwtTokenException.class)
  public void parseTokenInvalidSignatureTest() throws InvalidJwtTokenException {
    tokenFactory.parseToken(builtToken.substring(0, builtToken.length() - 1), HMAC_KEY);
  }

  @Test(expectedExceptions = InvalidJwtTokenException.class)
  public void parseTokenInvalidHeaderTest() throws InvalidJwtTokenException {
    tokenFactory.parseToken(builtToken.substring(1, builtToken.length()), HMAC_KEY);
  }
}
