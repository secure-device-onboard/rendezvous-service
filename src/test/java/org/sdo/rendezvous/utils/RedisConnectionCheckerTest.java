// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.testng.PowerMockTestCase;
import org.sdo.rendezvous.utils.validators.RedisConnectionChecker;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisConnectionCheckerTest extends PowerMockTestCase {

  @Mock private JedisPool jedisPool;

  @Mock private Jedis jedis;

  @InjectMocks private RedisConnectionChecker redisConnectionChecker = new RedisConnectionChecker();

  @BeforeMethod
  public void setUp() {
    jedis = Mockito.mock(Jedis.class);
    Mockito.when(jedisPool.getResource()).thenReturn(jedis);
  }

  @Test
  public void testIsPoolConnectedPositive() {
    Mockito.when(jedis.ping()).thenReturn("PONG");
    boolean isConnected = redisConnectionChecker.isPoolConnected();
    Assert.assertTrue(isConnected);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testIsPoolConnectedBrokenConnection() {
    Mockito.when(jedisPool.getResource()).thenThrow(JedisConnectionException.class);
    boolean isConnected = redisConnectionChecker.isPoolConnected();
    Assert.assertFalse(isConnected);
  }

  @Test
  public void testIsPoolResourcesAvailableButNoConnectionToRedis() {
    Mockito.when(jedis.ping()).thenReturn("");
    boolean isConnected = redisConnectionChecker.isPoolConnected();
    Assert.assertFalse(isConnected);
  }
}
