// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.exceptions;

import org.sdo.rendezvous.enums.ErrorCodes;

@SuppressWarnings("serial")
public class InvalidJwtTokenException extends SdoException {

  private static final ErrorCodes ERROR_CODE = ErrorCodes.INVALID_JWT_TOKEN;
  public static final String DEFAULT_MSG =
      "JWT token is missing or authorization header value does not start with 'Bearer'.";
  public static final String JWT_EXPIRED = "JWT token has expired.";
  public static final String JWT_INVALID = "JWT token is invalid.";

  public InvalidJwtTokenException(String msg) {
    super(msg, ERROR_CODE);
  }

  public InvalidJwtTokenException() {
    super(DEFAULT_MSG, ERROR_CODE);
  }

  public InvalidJwtTokenException(String s, Throwable throwable) {
    super(s, ERROR_CODE, throwable);
  }
}
