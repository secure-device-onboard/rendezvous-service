// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.sdo.rendezvous.model.types.RendezvousInfo;
import org.sdo.rendezvous.model.types.RendezvousInstr;

public class RendezvousInfoDeserializer extends JsonDeserializer<RendezvousInfo> {

  private static final int RENDEZVOUS_INSTR_VALUES_INDEX = 1;

  @Override
  public RendezvousInfo deserialize(JsonParser jsonParser, DeserializationContext ctxt)
      throws IOException {

    ObjectCodec objectCodec = jsonParser.getCodec();
    JsonNode jsonNode = objectCodec.readTree(jsonParser);
    RendezvousInfo rendezvousInfo = new RendezvousInfo();
    for (int i = RENDEZVOUS_INSTR_VALUES_INDEX; i < jsonNode.size(); i++) {
      RendezvousInstr rendezvousInstr =
          objectCodec.treeToValue(jsonNode.get(i), RendezvousInstr.class);
      rendezvousInfo.add(rendezvousInstr);
    }
    return rendezvousInfo;
  }
}
