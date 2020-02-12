// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MPRedirectBody {

  @JsonProperty("i1")
  private IpAddress ipAddress;

  @JsonProperty("dns1")
  private String dns;

  @JsonProperty("port1")
  private int port;

  @JsonProperty("to0dh")
  private Hash to0DataHash;
}
