// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorMessageRequest {

  @JsonProperty("ec")
  @Min(0)
  @Max(65535)
  private int errorCode;

  @JsonProperty("emsg")
  @Min(0)
  @Max(255)
  private short resumeCode;

  @JsonProperty("em")
  private String errorMessage;
}
