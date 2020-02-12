// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.exceptions;

import org.sdo.rendezvous.enums.ErrorCodes;

@SuppressWarnings("serial")
public class InvalidSigInfoException extends SdoException {

  private static final ErrorCodes ERROR_CODE = ErrorCodes.INVALID_MESSAGE_ERROR;

  public InvalidSigInfoException(String msg) {
    super(msg, ERROR_CODE);
  }
}
