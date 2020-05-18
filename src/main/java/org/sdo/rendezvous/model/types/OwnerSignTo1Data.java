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
public class OwnerSignTo1Data {

  public static final String TO1_DATA_BODY_TAG = "bo";
  public static final String PUBKEY_TAG = "pk";
  public static final String SIGNATURE_TAG = "sg";

  /**
   * Copy constructor creates an instance of OwnerSignTO1Data from different OwnerSignTO1Data.
   *
   * @param ownerSignTO1Data the instance of OwnerSignTO1Data
   */
  public OwnerSignTo1Data(OwnerSignTo1Data ownerSignTO1Data) {
    this.body = ownerSignTO1Data.getBody();
    this.pubKey = ownerSignTO1Data.getPubKey();
    this.signature = ownerSignTO1Data.getSignature();
  }

  @JsonProperty(TO1_DATA_BODY_TAG)
  private OwnerSignTo1DataBody body;

  @JsonProperty(PUBKEY_TAG)
  private PkNull pubKey;

  @JsonProperty(SIGNATURE_TAG)
  private Signature signature;

  @Override
  public String toString() {
    return "["
        + "bo(body)="
        + body
        + ", pk(public key)="
        + pubKey
        + ", sg(signature)="
        + signature
        + "]";
  }
}
