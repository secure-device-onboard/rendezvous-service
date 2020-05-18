// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
public class RendezvousConfig {

  private static final int MILLIS_IN_MIN = 60000;

  @Value("${rendezvous.hmacSecret}")
  private String hmacSecret;

  @Value("${rendezvous.verificationServiceHost}")
  private String verificationServiceHost;

  @Value("${rendezvous.tOTokenExpirationTime}")
  private int toTokenExpirationTime;

  @Value("${rendezvous.ownershipVoucherMaxEntries}")
  private int ownershipVoucherMaxEntries;

  @Value("${rendezvous.signatureVerification}")
  private boolean signatureVerification;

  @Value("${rendezvous.opKeyVerification}")
  private boolean opKeyVerification;

  @Value("${rendezvous.waitSecondsLimit}")
  private int waitSecondsLimit;

  public int getToTokenExpirationTime() {
    return toTokenExpirationTime * MILLIS_IN_MIN;
  }
}
