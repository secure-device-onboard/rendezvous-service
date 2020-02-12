// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sdo.rendezvous.model.types.serialization.AppIdDeserializer;
import org.sdo.rendezvous.model.types.serialization.AppIdSerializer;

@JsonDeserialize(using = AppIdDeserializer.class)
@JsonSerialize(using = AppIdSerializer.class)
@AllArgsConstructor
@Getter
public class AppId {

  private int type;

  private byte[] appIdBytes;

  @Override
  public String toString() {
    return "[" + "type=" + type + ", appIdBytes=" + printHexBinary(appIdBytes) + "]";
  }
}
