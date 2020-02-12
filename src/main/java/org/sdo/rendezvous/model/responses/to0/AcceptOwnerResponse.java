// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.responses.to0;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class AcceptOwnerResponse {

  @Getter
  @Setter
  @JsonProperty("ws")
  private int waitSeconds;

  public AcceptOwnerResponse(int waitSeconds) {
    this.waitSeconds = waitSeconds;
  }

  @Override
  public String toString() {
    return "[" + "ws(wait seconds)=" + waitSeconds + "]";
  }
}
