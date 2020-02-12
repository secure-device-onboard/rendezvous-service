// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class OwnershipVoucherEntryBody {

  @JsonProperty("hp")
  private Hash previousEntryHash;

  @JsonProperty("hc")
  private Hash guidDeviceInfoHash;

  @JsonProperty("pk")
  private PubKey publicKey;

  @Override
  public String toString() {
    return "["
        + "hp(previous entry hash)="
        + previousEntryHash
        + ", hc(guid device info hash)="
        + guidDeviceInfoHash
        + ", pk(public key (signed)))="
        + publicKey
        + ']';
  }
}
