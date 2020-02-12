// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.sdo.rendezvous.model.types.serialization.CertChainDeserializer;
import org.sdo.rendezvous.model.types.serialization.CertChainSerializer;

@JsonDeserialize(using = CertChainDeserializer.class)
@JsonSerialize(using = CertChainSerializer.class)
@AllArgsConstructor
@Data
public class CertChain {

  private CertType type;

  private short numEntries;

  private List<Cert> certs;

  @Override
  public String toString() {
    return "[" + "type=" + type + ", numEntries=" + numEntries + ", certs=" + certs + ']';
  }
}
