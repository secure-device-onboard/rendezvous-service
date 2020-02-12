// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sdo.rendezvous.model.types.serialization.HmacDeserializer;
import org.sdo.rendezvous.model.types.serialization.HmacSerializer;

@JsonDeserialize(using = HmacDeserializer.class)
@JsonSerialize(using = HmacSerializer.class)
@Getter
@AllArgsConstructor
public class Hmac {

  private HashType hashType;

  private byte[] hmac;

  @Override
  public String toString() {
    return printHexBinary(hmac);
  }
}
