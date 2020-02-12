// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.responses.to0;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sdo.rendezvous.enums.AttributeName;
import org.sdo.rendezvous.utils.SessionAttributeHolder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class HelloAckResponse {

  public static final int NONCE_SIZE = 16;

  @Getter
  @Setter
  @JsonProperty("n3")
  private byte[] nonce;

  /**
   * Generates nonce needed for tracking requests - TO0.HelloAck.
   *
   * @return the HelloAckResponse with generated nonce
   */
  public static HelloAckResponse generateInstance() {
    byte[] nonce =
        parseHexBinary(SessionAttributeHolder.getAttributeValue(AttributeName.SESSION_ID));
    return new HelloAckResponse(nonce);
  }

  @Override
  public String toString() {
    return "[" + "n3(nonce)=" + printHexBinary(nonce) + "]";
  }
}
