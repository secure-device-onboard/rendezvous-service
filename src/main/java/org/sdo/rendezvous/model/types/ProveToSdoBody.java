// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProveToSdoBody {

  @JsonProperty("ai")
  private AppId appId;

  @JsonProperty("n4")
  @Size(min = 16, max = 16)
  private byte[] nonce;

  @JsonProperty("g2")
  @Size(min = 16, max = 16)
  private byte[] guid;

  @Override
  public String toString() {
    return "["
        + "ai(app id)="
        + appId
        + ", n4(nonce)="
        + printHexBinary(nonce)
        + ", g2(guid)="
        + printHexBinary(guid)
        + "]";
  }
}
