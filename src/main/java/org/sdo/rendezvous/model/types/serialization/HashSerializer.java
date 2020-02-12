// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.sdo.rendezvous.model.types.Hash;

public class HashSerializer extends JsonSerializer<Hash> {

  @Override
  public void serialize(Hash value, JsonGenerator jsonGenerator, SerializerProvider serializers)
      throws IOException {
    jsonGenerator.writeStartArray();
    jsonGenerator.writeNumber(value.getHash().length);
    jsonGenerator.writeNumber(value.getHashType().getIndex());
    jsonGenerator.writeBinary(value.getHash());
    jsonGenerator.writeEndArray();
  }
}
