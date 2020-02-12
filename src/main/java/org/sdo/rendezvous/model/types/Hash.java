// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sdo.rendezvous.logging.utils.LoggingUtils;
import org.sdo.rendezvous.model.types.serialization.HashDeserializer;
import org.sdo.rendezvous.model.types.serialization.HashSerializer;

@JsonDeserialize(using = HashDeserializer.class)
@JsonSerialize(using = HashSerializer.class)
@AllArgsConstructor
@Getter
public class Hash {

  private HashType hashType;

  private byte[] hash;

  @Override
  public String toString() {
    return LoggingUtils.getEdgeBytesOfArray(hash);
  }
}
