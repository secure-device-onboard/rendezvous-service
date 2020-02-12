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
public class OwnershipVoucherEntry {

  @JsonProperty("bo")
  private OwnershipVoucherEntryBody ownershipVoucherEntryBody;

  @JsonProperty("pk")
  private PubKey pubKey;

  @JsonProperty("sg")
  private Signature signature;

  @Override
  public String toString() {
    return "["
        + "bo(ownership voucher entry body)="
        + ownershipVoucherEntryBody
        + ", pk(public key (signer))="
        + pubKey
        + ", sg(signature)="
        + signature
        + ']';
  }
}
