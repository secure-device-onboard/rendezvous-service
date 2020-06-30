// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.sdo.rendezvous.model.types.PkEpidEnc;

public class PkEpidEncSerializer extends JsonSerializer<PkEpidEnc> {

  @Override
  public void serialize(
      PkEpidEnc value, JsonGenerator jsonGenerator, SerializerProvider serializers)
      throws IOException {
    jsonGenerator.writeStartArray();
    jsonGenerator.writeNumber(value.getPkType().getIndex());
    jsonGenerator.writeNumber(value.getPkEnc().getIndex());
    jsonGenerator.writeStartArray();
    jsonGenerator.writeNumber(value.getEpidGroupNo().length);
    jsonGenerator.writeBinary(value.getEpidGroupNo());
    jsonGenerator.writeEndArray();
    jsonGenerator.writeEndArray();
  }
}
