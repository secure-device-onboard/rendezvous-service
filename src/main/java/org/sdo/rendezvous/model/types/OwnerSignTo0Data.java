// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OwnerSignTo0Data {

  @JsonProperty("op")
  private OwnershipVoucher ownershipVoucher;

  @JsonProperty("ws")
  private int waitSeconds;

  @JsonProperty("n3")
  @Size(min = 16, max = 16)
  private byte[] nonce;

  @Override
  public String toString() {
    return "["
        + "op(ownership voucher)="
        + ownershipVoucher
        + ", ws(wait seconds)="
        + waitSeconds
        + ", n3(nonce)="
        + printHexBinary(nonce)
        + ']';
  }
}
