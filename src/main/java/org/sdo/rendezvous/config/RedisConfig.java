// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "redis")
@Getter
@Setter
class RedisConfig {

  @Value("${redis.host}")
  private String host;

  @Value("${redis.port}")
  private Integer port;

  @Value("${redis.ssl}")
  private boolean ssl;
}
