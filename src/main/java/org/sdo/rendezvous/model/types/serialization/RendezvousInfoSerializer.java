// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.sdo.rendezvous.model.types.RendezvousInfo;

public class RendezvousInfoSerializer extends JsonSerializer<RendezvousInfo> {

  @Override
  public void serialize(
      RendezvousInfo value, JsonGenerator jsonGenerator, SerializerProvider serializers)
      throws IOException {
    jsonGenerator.writeStartArray();
    jsonGenerator.writeNumber(value.getLength());
    for (int i = 0; i < value.getRendezvousInstructions().size(); i++) {
      jsonGenerator.writeObject(value.getRendezvousInstructions().get(i));
    }
    jsonGenerator.writeEndArray();
  }
}
