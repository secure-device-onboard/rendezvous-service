// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.sdo.rendezvous.model.types.PKRMEEnc;

public class PKRMEEncSerializer extends JsonSerializer<PKRMEEnc> {

  @Override
  public void serialize(PKRMEEnc value, JsonGenerator jsonGenerator, SerializerProvider serializers)
      throws IOException {

    jsonGenerator.writeStartArray();
    jsonGenerator.writeNumber(value.getPkType().getIndex());
    jsonGenerator.writeNumber(value.getPkEnc().getIndex());
    jsonGenerator.writeStartArray();
    jsonGenerator.writeNumber(value.getModba().length);
    jsonGenerator.writeBinary(value.getModba());
    jsonGenerator.writeNumber(value.getExpba().length);
    jsonGenerator.writeBinary(value.getExpba());
    jsonGenerator.writeEndArray();
    jsonGenerator.writeEndArray();
  }
}
