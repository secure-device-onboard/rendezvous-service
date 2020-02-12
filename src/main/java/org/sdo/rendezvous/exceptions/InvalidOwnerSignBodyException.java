// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.exceptions;

import org.sdo.rendezvous.enums.ErrorCodes;

@SuppressWarnings("serial")
public class InvalidOwnerSignBodyException extends SdoException {

  private static final String DEFAULT_MSG = "Signature of owner message is invalid.";
  private static final ErrorCodes ERROR_CODE = ErrorCodes.INVALID_OWNER_SIGN_BODY;

  public InvalidOwnerSignBodyException() {
    super(DEFAULT_MSG, ERROR_CODE);
  }

  public InvalidOwnerSignBodyException(String msg) {
    super(msg, ERROR_CODE);
  }

  public InvalidOwnerSignBodyException(Throwable throwable) {
    super(DEFAULT_MSG, ERROR_CODE, throwable);
  }
}
