// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "rendezvous")
public class RendezvousConfig {

  private static final int MILLIS_IN_MIN = 60000;

  private String hmacSecret;

  private String verificationServiceHost;

  private int toTokenExpirationTime;

  private int ownershipVoucherMaxEntries;

  private boolean signatureVerification;

  private boolean opKeyVerification;

  private int waitSecondsLimit;

  public int getToTokenExpirationTime() {
    return toTokenExpirationTime * MILLIS_IN_MIN;
  }
}
