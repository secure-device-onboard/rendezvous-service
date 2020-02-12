// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import javax.xml.bind.DatatypeConverter;
import org.sdo.rendezvous.model.types.Cert;
import org.sdo.rendezvous.model.types.CertChain;

public class CertChainSerializer extends JsonSerializer<CertChain> {

  @Override
  public void serialize(
      CertChain value, JsonGenerator jsonGenerator, SerializerProvider serializers)
      throws IOException {
    jsonGenerator.writeStartArray();
    jsonGenerator.writeNumber(value.getType().getIndex());
    jsonGenerator.writeNumber(value.getNumEntries());
    jsonGenerator.writeStartArray();
    for (Cert cert : value.getCerts()) {
      jsonGenerator.writeStartArray();
      jsonGenerator.writeNumber(cert.getLength());
      jsonGenerator.writeString(DatatypeConverter.printBase64Binary(cert.getCertBytes()));
      jsonGenerator.writeEndArray();
    }
    jsonGenerator.writeEndArray();
    jsonGenerator.writeEndArray();
  }
}
