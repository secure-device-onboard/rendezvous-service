// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@Import(RedisConfig.class)
class JedisConnectionConfig {

  @Autowired private RedisConfig redisConfig;

  @Bean(destroyMethod = "destroy")
  JedisPool jedisPool() {
    return new JedisPool(
        new JedisPoolConfig(), redisConfig.getHost(), redisConfig.getPort(), redisConfig.isSsl());
  }
}
