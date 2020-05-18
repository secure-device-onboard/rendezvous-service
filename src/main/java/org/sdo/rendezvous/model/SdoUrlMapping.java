// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SdoUrlMapping {
  private static final String MP = "/mp/";
  private static final String HEALTH_ENDPOINT = "health";
  private static final String FULL_HEALTH_ENDPOINT = "/full";
  private static final String MSG = "msg/";
  public static final String PROTOCOL_VERSION_110 = "110/";
  private static final String PROTOCOL_VERSION_112 = "112/";
  private static final String PROTOCOL_VERSION_113 = "113/";

  public static final String TO_MSG_110 = MP + PROTOCOL_VERSION_110 + MSG;
  public static final String TO_MSG_112 = MP + PROTOCOL_VERSION_112 + MSG;
  public static final String TO_MSG_113 = MP + PROTOCOL_VERSION_113 + MSG;

  public static final String HEALTH_110 = MP + PROTOCOL_VERSION_110 + HEALTH_ENDPOINT;
  public static final String HEALTH_112 = MP + PROTOCOL_VERSION_112 + HEALTH_ENDPOINT;
  public static final String HEALTH_113 = MP + PROTOCOL_VERSION_113 + HEALTH_ENDPOINT;

  public static final String FULL_HEALTH_110 =
      MP + PROTOCOL_VERSION_110 + HEALTH_ENDPOINT + FULL_HEALTH_ENDPOINT;
  public static final String FULL_HEALTH_112 =
      MP + PROTOCOL_VERSION_112 + HEALTH_ENDPOINT + FULL_HEALTH_ENDPOINT;
  public static final String FULL_HEALTH_113 =
      MP + PROTOCOL_VERSION_113 + HEALTH_ENDPOINT + FULL_HEALTH_ENDPOINT;

  public static final String TO0_HELLO_ENDPOINT = "20";
  public static final String TO0_110_HELLO = TO_MSG_110 + TO0_HELLO_ENDPOINT;
  public static final String TO0_112_HELLO = TO_MSG_112 + TO0_HELLO_ENDPOINT;
  public static final String TO0_113_HELLO = TO_MSG_113 + TO0_HELLO_ENDPOINT;

  public static final String TO0_OWNER_SIGN_ENDPOINT = "22";
  public static final String TO0_110_OWNER_SIGN = TO_MSG_110 + TO0_OWNER_SIGN_ENDPOINT;
  public static final String TO0_112_OWNER_SIGN = TO_MSG_112 + TO0_OWNER_SIGN_ENDPOINT;
  public static final String TO0_113_OWNER_SIGN = TO_MSG_113 + TO0_OWNER_SIGN_ENDPOINT;

  public static final String TO1_HELLO_SDO_ENDPOINT = "30";
  public static final String TO1_110_HELLO_SDO = TO_MSG_110 + TO1_HELLO_SDO_ENDPOINT;
  public static final String TO1_112_HELLO_SDO = TO_MSG_112 + TO1_HELLO_SDO_ENDPOINT;
  public static final String TO1_113_HELLO_SDO = TO_MSG_113 + TO1_HELLO_SDO_ENDPOINT;

  public static final String TO1_PROVE_TO_SDO_ENDPOINT = "32";
  public static final String TO1_110_PROVE_TO_SDO = TO_MSG_110 + TO1_PROVE_TO_SDO_ENDPOINT;
  public static final String TO1_112_PROVE_TO_SDO = TO_MSG_112 + TO1_PROVE_TO_SDO_ENDPOINT;
  public static final String TO1_113_PROVE_TO_SDO = TO_MSG_113 + TO1_PROVE_TO_SDO_ENDPOINT;

  public static final String ERROR_ENDPOINT = "255";
  public static final String ERROR_ENDPOINT_110 = TO_MSG_110 + ERROR_ENDPOINT;
  public static final String ERROR_ENDPOINT_112 = TO_MSG_112 + ERROR_ENDPOINT;
  public static final String ERROR_ENDPOINT_113 = TO_MSG_113 + ERROR_ENDPOINT;
}
