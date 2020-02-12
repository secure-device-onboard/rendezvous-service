// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.sdo.rendezvous.model.types.AppId;

public class AppIdSerializer extends JsonSerializer<AppId> {

  @Override
  public void serialize(AppId value, JsonGenerator jsonGenerator, SerializerProvider serializers)
      throws IOException {
    jsonGenerator.writeStartArray();
    jsonGenerator.writeNumber(value.getAppIdBytes().length);
    jsonGenerator.writeNumber(value.getType());
    jsonGenerator.writeBinary(value.getAppIdBytes());
    jsonGenerator.writeEndArray();
  }
}
