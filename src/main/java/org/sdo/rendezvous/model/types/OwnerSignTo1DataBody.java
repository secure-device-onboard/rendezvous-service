// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OwnerSignTo1DataBody {

  @JsonProperty("i1")
  private IpAddress ipAddress;

  @JsonProperty("dns1")
  private String dns;

  @JsonProperty("port1")
  private int port;

  @JsonProperty("to0dh")
  private Hash to0DataHash;

  @Override
  public String toString() {
    return "["
        + "i1(ip address)="
        + ipAddress
        + ", dns1(dns name)="
        + dns
        + ", port1(port)="
        + port
        + ", to0dh(to0 data hash)="
        + to0DataHash
        + "]";
  }
}
