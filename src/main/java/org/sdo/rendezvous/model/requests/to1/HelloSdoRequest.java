// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.requests.to1;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sdo.rendezvous.model.types.SigInfo;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HelloSdoRequest {

  @JsonProperty("g2")
  @Size(min = 16, max = 16)
  private byte[] guid;

  @JsonProperty("eA")
  private SigInfo sigInfo;

  @Override
  public String toString() {
    return "[" + "g2(guid)=" + printHexBinary(guid) + ", eA(signature info)=" + sigInfo + "]";
  }
}
