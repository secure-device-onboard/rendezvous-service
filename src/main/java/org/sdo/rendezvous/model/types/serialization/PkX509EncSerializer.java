// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.sdo.rendezvous.model.types.PkX509Enc;

public class PkX509EncSerializer extends JsonSerializer<PkX509Enc> {

  @Override
  public void serialize(
      PkX509Enc value, JsonGenerator jsonGenerator, SerializerProvider serializers)
      throws IOException {
    jsonGenerator.writeStartArray();
    jsonGenerator.writeNumber(value.getPkType().getIndex());
    jsonGenerator.writeNumber(value.getPkEnc().getIndex());
    jsonGenerator.writeStartArray();
    jsonGenerator.writeNumber(value.getPkX509().length);
    jsonGenerator.writeBinary(value.getPkX509());
    jsonGenerator.writeEndArray();
    jsonGenerator.writeEndArray();
  }
}
