// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.sdo.rendezvous.model.types.SigInfo;

public class SigInfoSerializer extends JsonSerializer<SigInfo> {
  @Override
  public void serialize(
      SigInfo value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
      throws IOException {
    jsonGenerator.writeStartArray();
    jsonGenerator.writeNumber(value.getSigInfoType().getIndex());
    jsonGenerator.writeNumber(value.getBytes().length);
    jsonGenerator.writeBinary(value.getBytes());
    jsonGenerator.writeEndArray();
  }
}
