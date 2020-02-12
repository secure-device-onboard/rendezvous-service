// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.enums;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodes {
  UNKNOWN_ERROR((short) 0),
  INVALID_JWT_TOKEN((short) 1),
  INVALID_OWNERSHIP_VOUCHER((short) 2),
  INVALID_OWNER_SIGN_BODY((short) 3),
  INVALID_IP_ADDRESS((short) 4),
  INVALID_GUID((short) 5),
  RESOURCE_NOT_FOUND((short) 6),

  MESSAGE_BODY_ERROR((short) 100),
  INVALID_MESSAGE_ERROR((short) 101),
  GENERIC_ERROR((short) 500);

  private short value;
  private static final String UNKNOWN_ERROR_MESSAGE = "Unknown error code";

  /**
   * Returns the description of error message as string.
   *
   * @param errorCodeId the id of error code
   * @return error message
   */
  public static String getDescriptionById(short errorCodeId) {
    ErrorCodes ec =
        Arrays.stream(values())
            .filter(errorType -> errorType.value == errorCodeId)
            .findFirst()
            .orElse(UNKNOWN_ERROR);
    if (ec == ErrorCodes.UNKNOWN_ERROR) {
      return UNKNOWN_ERROR_MESSAGE;
    } else {
      return ec.name();
    }
  }

  @Override
  public String toString() {
    return value + "(" + name() + ")";
  }
}
