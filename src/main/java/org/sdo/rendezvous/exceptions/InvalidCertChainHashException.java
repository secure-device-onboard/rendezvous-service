// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.exceptions;

import org.sdo.rendezvous.enums.ErrorCodes;

@SuppressWarnings("serial")
public class InvalidCertChainHashException extends SdoException {

  private static final String defaultMsg = "Certificate chain hash verification failed.";
  private static final ErrorCodes ERROR_CODE = ErrorCodes.INVALID_OWNERSHIP_VOUCHER;

  public InvalidCertChainHashException() {
    super(defaultMsg, ERROR_CODE);
  }

  public InvalidCertChainHashException(Throwable throwable) {
    super(defaultMsg, ERROR_CODE, throwable);
  }
}
