// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.sdo.rendezvous.model.types.Hmac;

public class HmacSerializer extends JsonSerializer<Hmac> {

  @Override
  public void serialize(Hmac value, JsonGenerator jsonGenerator, SerializerProvider serializers)
      throws IOException {
    jsonGenerator.writeStartArray();
    jsonGenerator.writeNumber(value.getHmac().length);
    jsonGenerator.writeNumber(value.getHashType().getIndex());
    jsonGenerator.writeBinary(value.getHmac());
    jsonGenerator.writeEndArray();
  }
}
