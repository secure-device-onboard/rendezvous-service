// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.sdo.rendezvous.enums.ErrorCodes;
import org.sdo.rendezvous.model.responses.ErrorResponse;

@SuppressWarnings("serial")
@EqualsAndHashCode(callSuper = false)
public class SdoException extends Exception {

  @Getter private ErrorCodes errorCode;

  public SdoException(String msg) {
    super(msg);
  }

  public SdoException(String msg, ErrorCodes errorCode) {
    super(msg);
    this.errorCode = errorCode;
  }

  public SdoException(String s, ErrorCodes errorCode, Throwable throwable) {
    super(s, throwable);
    this.errorCode = errorCode;
  }

  @Override
  public String toString() {
    return "["
        + "ec(error code)="
        + errorCode
        + ", emsg(id of previous message)="
        + ErrorResponse.RESUME_CODE
        + ", em(error message)= \""
        + getMessage()
        + "\"]";
  }
}
