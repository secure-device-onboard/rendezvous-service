// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

@Slf4j
@Component
@SuppressWarnings("try")
public class RedisConnectionChecker {

  @Autowired private JedisPool jedisPool;

  private static final String REDIS_RESPONSE = "PONG";

  /**
   * Checks connection to the Redis database.
   *
   * @return true if the connection established
   */
  public boolean isPoolConnected() {
    log.info("Getting resources from jedisPool.");
    try (Jedis jedis = jedisPool.getResource()) {
      log.info("Checking connection with jedis.");
      return isConnectionWithRedis(jedis);
    } catch (JedisException e) {
      log.error("Problem with getting resources for redis: " + e.getMessage());
      return false;
    }
  }

  private boolean isConnectionWithRedis(Jedis jedis) {
    try {
      return jedis.ping().equalsIgnoreCase(REDIS_RESPONSE);
    } catch (JedisConnectionException e) {
      log.error("Problem with connection to redis: " + e.getMessage());
      return false;
    }
  }
}
