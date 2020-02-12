// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.exceptions;

import org.sdo.rendezvous.enums.ErrorCodes;

@SuppressWarnings("serial")
public class InvalidOwnershipVoucherException extends SdoException {

  private static final String DEFAULT_MSG = "Ownership voucher is invalid.";
  private static final ErrorCodes ERROR_CODE = ErrorCodes.INVALID_OWNERSHIP_VOUCHER;

  public InvalidOwnershipVoucherException() {
    super(DEFAULT_MSG, ERROR_CODE);
  }

  public InvalidOwnershipVoucherException(String msg) {
    super(msg, ERROR_CODE);
  }

  public InvalidOwnershipVoucherException(Throwable throwable) {
    super(DEFAULT_MSG, ERROR_CODE, throwable);
  }
}
