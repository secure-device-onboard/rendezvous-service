// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.requests.to0;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sdo.rendezvous.model.types.IpAddress;
import org.sdo.rendezvous.model.types.OwnerSignTO0Data;
import org.sdo.rendezvous.model.types.OwnerSignTO1Data;

@NoArgsConstructor
@Data
public class OwnerSignRequest {

  @JsonProperty("to0d")
  private OwnerSignTO0Data to0Data;

  @JsonProperty("to1d")
  private OwnerSignTO1Data to1Data;

  @JsonIgnore
  public IpAddress getIpAddress() {
    return to1Data.getBody().getIpAddress();
  }

  @JsonIgnore
  public String getDns() {
    return to1Data.getBody().getDns();
  }

  @JsonIgnore
  public int getPort() {
    return to1Data.getBody().getPort();
  }

  @JsonIgnore
  public byte[] getGuid() {
    return to0Data.getOwnershipVoucher().getOwnershipVoucherHeader().getGuid();
  }

  @Override
  public String toString() {
    return "[" + "to0d(to0 data)=" + to0Data + ", to1d(to1 data)=" + to1Data + "]";
  }
}
