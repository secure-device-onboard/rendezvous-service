// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.sdo.rendezvous.model.types.Signature;

public class SignatureDeserializer extends JsonDeserializer<Signature> {

  private static final int LENGTH_INDEX = 0;
  private static final int SIGNATURE_INDEX = 1;

  @Override
  public Signature deserialize(JsonParser jsonParser, DeserializationContext ctxt)
      throws IOException {

    ObjectCodec objectCodec = jsonParser.getCodec();
    JsonNode jsonNode = objectCodec.readTree(jsonParser);

    jsonNode.get(LENGTH_INDEX).asInt();
    byte[] signature = jsonNode.get(SIGNATURE_INDEX).binaryValue();
    return new Signature(signature);
  }
}
