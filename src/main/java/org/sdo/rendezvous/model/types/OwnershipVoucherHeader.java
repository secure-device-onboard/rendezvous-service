// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OwnershipVoucherHeader {

  @JsonProperty("pv")
  private int protocolVersion;

  @JsonProperty("pe")
  private short keyEncoding;

  @JsonProperty("r")
  private RendezvousInfo rendezvousInfo;

  @JsonProperty("g")
  private byte[] guid;

  @JsonProperty("d")
  private String deviceInfo;

  @JsonProperty("pk")
  private PubKey manufacturerPublicKey;

  @JsonProperty("hdc")
  private Hash deviceCertChainHash;

  @Override
  public String toString() {
    return "["
        + "pv(protocol version)="
        + protocolVersion
        + ", pe(key encoding)="
        + keyEncoding
        + ", r(rendezvous info)="
        + rendezvousInfo
        + ", g(guid)="
        + printHexBinary(guid)
        + ", d(device info)="
        + deviceInfo
        + ", pk(manufacturer public key)="
        + manufacturerPublicKey
        + ", hdc(device cert chain hash)="
        + deviceCertChainHash
        + ']';
  }
}
