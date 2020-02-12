// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

public class JWTokenTestUtils {
  private static final int JWT_HEADER_INDEX = 0;
  private static final int JWT_BODY_INDEX = 1;

  private static final int TO1_PAYLOAD_OFFSET = 0;
  private static final int TO1_EXPIRATION_TIMESTAMP_OFFSET = 87;

  public static String getHeader(String jwt) {
    String[] segments = jwt.split("\\.");
    return segments[JWT_HEADER_INDEX];
  }

  @SuppressWarnings("unchecked")
  public static HashMap<String, Object> getClaims(String jwt) throws IOException {
    String[] segments = jwt.split("\\.");
    String decodedBodyAsJson = new String(Base64.getDecoder().decode(segments[JWT_BODY_INDEX]));
    return new ObjectMapper().readValue(decodedBodyAsJson, HashMap.class);
  }

  public static String getTO1BodyNoTimestamps(String jwt) {
    String[] segments = jwt.split("\\.");
    return segments[JWT_BODY_INDEX].substring(TO1_PAYLOAD_OFFSET, TO1_EXPIRATION_TIMESTAMP_OFFSET);
  }
}
