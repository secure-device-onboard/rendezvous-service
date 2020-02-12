// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.sdo.rendezvous.model.types.RendezvousInstr;

public class RendezvousInstrSerializer extends JsonSerializer<RendezvousInstr> {

  @Override
  public void serialize(
      RendezvousInstr value, JsonGenerator jsonGenerator, SerializerProvider serializers)
      throws IOException {
    jsonGenerator.writeStartArray();
    jsonGenerator.writeNumber(value.getLength());
    jsonGenerator.writeObject(value.getValues());
    jsonGenerator.writeEndArray();
  }
}
