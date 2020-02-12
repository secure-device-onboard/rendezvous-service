// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import lombok.Data;

@Data
public class OwnershipVoucher {

  @JsonProperty("sz")
  private short numberOfEntries;

  @JsonProperty("oh")
  private OwnershipVoucherHeader ownershipVoucherHeader;

  @JsonProperty("hmac")
  private Hmac hmac;

  @JsonProperty("dc")
  private CertChain certificateChain;

  @JsonProperty("en")
  private OwnershipVoucherEntry[] ownershipVoucherEntries;

  @Override
  public String toString() {
    return "["
        + "sz(number of entries)="
        + numberOfEntries
        + ", oh(ownership voucher header)="
        + ownershipVoucherHeader
        + ", hmac(hmac)="
        + hmac
        + ", dc(certificate chain)="
        + certificateChain
        + ", en(ownership voucher entries)="
        + Arrays.toString(ownershipVoucherEntries)
        + ']';
  }
}
