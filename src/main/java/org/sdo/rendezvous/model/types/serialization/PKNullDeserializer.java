// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.sdo.rendezvous.model.types.PKNull;
import org.sdo.rendezvous.model.types.PublicKeyEncoding;
import org.sdo.rendezvous.model.types.PublicKeyType;

public class PKNullDeserializer extends JsonDeserializer<PKNull> {

  private static final int PK_TYPE_INDEX = 0;
  private static final int PKNULL_OBJECT_INDEX = 2;
  private static final int PKNULL_VALUE_INDEX = 0;

  @Override
  public PKNull deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
    ObjectCodec objectCodec = jsonParser.getCodec();
    JsonNode jsonNode = objectCodec.readTree(jsonParser);

    PublicKeyType pkType = PublicKeyType.valueOf(jsonNode.get(PK_TYPE_INDEX).asInt());
    if (pkType != PublicKeyType.NONE) {
      throw new JsonParseException(jsonParser, "PKNull must have pkType set to none.");
    }

    JsonNode pkNullObect = jsonNode.get(PKNULL_OBJECT_INDEX);
    if (!pkNullObect.isArray()) {
      throw new JsonParseException(jsonParser, "PKNull (jsonNode) is not an array.");
    }

    int pkEncoding = pkNullObect.get(PKNULL_VALUE_INDEX).asInt();
    if (pkEncoding != PublicKeyEncoding.NONE.getIndex()) {
      throw new JsonParseException(jsonParser, "PKNull encoding is invalid.");
    }

    return new PKNull();
  }
}
