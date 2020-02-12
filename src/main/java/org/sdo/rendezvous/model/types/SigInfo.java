// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.sdo.rendezvous.logging.utils.LoggingUtils;
import org.sdo.rendezvous.model.types.serialization.SigInfoDeserializer;
import org.sdo.rendezvous.model.types.serialization.SigInfoSerializer;

@JsonDeserialize(using = SigInfoDeserializer.class)
@JsonSerialize(using = SigInfoSerializer.class)
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class SigInfo {

  private PublicKeyType sigInfoType;

  private byte[] bytes;

  @Override
  public String toString() {
    String epidData =
        sigInfoType.isEpid() ? ", (bytes)=" + LoggingUtils.getEdgeBytesOfArray(bytes) : "";
    return "[(signature info type)=" + sigInfoType + epidData + "]";
  }
}
