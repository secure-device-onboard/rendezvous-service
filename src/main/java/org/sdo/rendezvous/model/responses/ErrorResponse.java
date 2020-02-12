// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.sdo.rendezvous.enums.ErrorCodes;

@Getter
@Setter
public class ErrorResponse {

  public static final short RESUME_CODE = 255;
  private static final ErrorCodes GENERIC_ERROR_CODE = ErrorCodes.GENERIC_ERROR;
  private static final String GENERIC_ERROR_MESSAGE = "Generic error.";

  @JsonProperty("ec")
  private short errorCode;

  @JsonProperty("emsg")
  private short resumeCode;

  @JsonProperty("em")
  private String errorMessage;

  /**
   * Create a new instance of the ErrorResponse using defined parameters.
   *
   * @param errorCode the error code
   * @param errorMessage the message error
   */
  public ErrorResponse(ErrorCodes errorCode, String errorMessage) {
    this.errorCode = errorCode.getValue();
    this.resumeCode = RESUME_CODE;
    this.errorMessage = errorMessage;
  }

  /** Create a new instance of the ErrorResponse using default settings. */
  public ErrorResponse() {
    this.errorCode = GENERIC_ERROR_CODE.getValue();
    this.resumeCode = RESUME_CODE;
    this.errorMessage = GENERIC_ERROR_MESSAGE;
  }
}
