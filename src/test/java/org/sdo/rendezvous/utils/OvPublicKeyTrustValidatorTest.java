// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils;

import javax.xml.bind.DatatypeConverter;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.testng.PowerMockTestCase;
import org.sdo.rendezvous.exceptions.InvalidOwnershipVoucherException;
import org.sdo.rendezvous.utils.validators.OvPublicKeyTrustValidator;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class OvPublicKeyTrustValidatorTest extends PowerMockTestCase {

  @Mock private JedisPool jedisPool;

  @Mock private Jedis jedis;

  private OvPublicKeyTrustValidator ovPublicKeyTrustValidator;

  private String allowlistHashsetName = "OP_KEYS_ALLOWLIST";
  private String denylistHashsetName = "OP_KEYS_DENYLIST";
  private String keyHash =
      "30820122300D06092A864886F70D01010105000382010F003082010A028201010096EB0862670544F9F"
          + "DE47C8E4F7651DC58BDDD04155B89C85CAAE7527138FABD3231C3E0736175CADB4FAE6ED892B2E8"
          + "AF1AC7CEE4838D5CE416DD33043B3133612948278F216C1104B02926048BFFAE14B2EF2F5A4712A"
          + "D2D8B1826A59D84F700F056818650610FD4F31B0E317E28F0C88CA4D40FA289D13D2D2DCB9217B1"
          + "41E3DC340B237723E57312452E48FA675373B7A6BFEF7167C1BA29C99C06A12399FE66B26047026"
          + "2364DF74AD15DE34B34B147CC6FAC7CABCC9A746BA037EF145AF772373198479C9C94E160D06CB2"
          + "C75FE62CEAF1CEC1205C8248140B3F138FE5AB4A334BC973A501E6821A63CDE5E5879BA16908E7B"
          + "DCC1B1E6CF2AFAB4ADB610203010001";

  /**
   * Variable initialization.
   */
  @BeforeMethod
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    ovPublicKeyTrustValidator = new OvPublicKeyTrustValidator(jedisPool);
    Mockito.when(jedisPool.getResource()).thenReturn(jedis);
    Mockito.when(jedis.hexists(denylistHashsetName, keyHash)).thenReturn(false);
  }

  @Test
  public void testVerifyAllowlistPositive() throws InvalidOwnershipVoucherException {
    Mockito.when(jedis.hexists(allowlistHashsetName, keyHash)).thenReturn(true);
    boolean result = ovPublicKeyTrustValidator.verify(DatatypeConverter.parseHexBinary(keyHash));
    Assert.assertTrue(result);
  }

  @Test
  public void testVerifyAllowlistNegative() throws InvalidOwnershipVoucherException {
    Mockito.when(jedis.hexists(allowlistHashsetName, keyHash)).thenReturn(false);
    boolean result = ovPublicKeyTrustValidator.verify(DatatypeConverter.parseHexBinary(keyHash));
    Assert.assertFalse(result);
  }

  @Test(expectedExceptions = InvalidOwnershipVoucherException.class)
  public void testVerifyDenylistShouldThrow() throws InvalidOwnershipVoucherException {
    Mockito.when(jedis.hexists(denylistHashsetName, keyHash)).thenReturn(true);
    Mockito.when(jedis.hexists(allowlistHashsetName, keyHash)).thenReturn(false);
    ovPublicKeyTrustValidator.verify(DatatypeConverter.parseHexBinary(keyHash));
  }
}
