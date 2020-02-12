// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.responses.to1;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sdo.rendezvous.enums.AttributeName;
import org.sdo.rendezvous.model.types.SigInfo;
import org.sdo.rendezvous.utils.SessionAttributeHolder;

@Data
@AllArgsConstructor
@NoArgsConstructor
// The name corresponds to the protocol specification
public class HelloSdoAckResponse {

  private static final Integer NONCE_SIZE = 16;

  @JsonProperty("n4")
  private byte[] nonce;

  @JsonProperty("eB")
  private SigInfo sigInfo;

  /**
   * /** Generates nonce needed for tracking requests - TO1.HelloAck.
   *
   * @param sigInfo the encoded parameters for the device attestation signature
   * @return the instance of HelloSdoAckResponse containing sigInfo and generated nonce
   */
  public static HelloSdoAckResponse generateInstance(SigInfo sigInfo) {
    byte[] nonce =
        parseHexBinary(SessionAttributeHolder.getAttributeValue(AttributeName.SESSION_ID));
    return new HelloSdoAckResponse(nonce, sigInfo);
  }

  @Override
  public String toString() {
    return "[" + "n4(nonce)=" + printHexBinary(nonce) + ", eB(sigInfo)=" + sigInfo + "]";
  }
}
