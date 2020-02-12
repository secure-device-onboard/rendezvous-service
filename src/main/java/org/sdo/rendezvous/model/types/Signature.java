// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sdo.rendezvous.logging.utils.LoggingUtils;
import org.sdo.rendezvous.model.types.serialization.SignatureDeserializer;
import org.sdo.rendezvous.model.types.serialization.SignatureSerializer;

@JsonSerialize(using = SignatureSerializer.class)
@JsonDeserialize(using = SignatureDeserializer.class)
@AllArgsConstructor
@Getter
public class Signature {

  private byte[] bytes;

  @Override
  public String toString() {
    return LoggingUtils.getEdgeBytesOfArray(bytes);
  }
}
