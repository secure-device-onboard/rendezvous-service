// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.exceptions;

import org.sdo.rendezvous.enums.ErrorCodes;

@SuppressWarnings("serial")
public class InvalidCertException extends SdoException {

  private static final ErrorCodes ERROR_CODE = ErrorCodes.INVALID_OWNERSHIP_VOUCHER;

  public InvalidCertException(String msg) {
    super(msg, ERROR_CODE);
  }
}
