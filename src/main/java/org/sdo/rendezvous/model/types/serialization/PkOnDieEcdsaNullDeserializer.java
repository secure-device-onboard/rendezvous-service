// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.model.types.PkOnDieEcdsaNull;
import org.sdo.rendezvous.model.types.PublicKeyEncoding;
import org.sdo.rendezvous.model.types.PublicKeyType;


@Slf4j
public class PkOnDieEcdsaNullDeserializer extends JsonDeserializer<PkOnDieEcdsaNull> {

  private static final int PK_ON_DIE_ECDSA_TYPE_INDEX = 0;
  private static final int PK_ON_DIE_ECDSA_ENCODING_INDEX = 1;
  private static final int PK_ON_DIE_ECDSA_NULL_OBJECT_INDEX = 2;
  private static final int PK_ON_DIE_ECDSA_NULL_VALUE_INDEX = 0;

  @Override
  public PkOnDieEcdsaNull deserialize(JsonParser jsonParser, DeserializationContext ctxt)
      throws IOException {
    ObjectCodec objectCodec = jsonParser.getCodec();
    JsonNode jsonNode = objectCodec.readTree(jsonParser);

    PublicKeyType pkType = PublicKeyType.valueOf(jsonNode.get(PK_ON_DIE_ECDSA_TYPE_INDEX).asInt());
    if (pkType != PublicKeyType.ONDIE_ECDSA_384) {
      throw new JsonParseException(
          jsonParser, "PKOnDieEcdsaNull must have pkType set to OnDieEcdsa.");
    }

    int pkEncoding = jsonNode.get(PK_ON_DIE_ECDSA_ENCODING_INDEX).asInt();
    if (pkEncoding != PublicKeyEncoding.ONDIE_ECDSA.getIndex()) {
      throw new JsonParseException(jsonParser, "PKOnDieEcdsaNull encoding is invalid.");
    }

    JsonNode pkNullObect = jsonNode.get(PK_ON_DIE_ECDSA_NULL_OBJECT_INDEX);
    if (!pkNullObect.isArray()) {
      throw new JsonParseException(jsonParser, "PKNull (jsonNode) is not an array.");
    }

    int pkValue = pkNullObect.get(PK_ON_DIE_ECDSA_NULL_VALUE_INDEX).asInt();
    if (pkValue != PublicKeyEncoding.NONE.getIndex()) {
      throw new JsonParseException(jsonParser, "PKOnDieEcdsaNull value is invalid.");
    }

    return new PkOnDieEcdsaNull();
  }
}
