// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.sdo.rendezvous.model.types.HashType;
import org.sdo.rendezvous.model.types.Hmac;

public class HmacDeserializer extends JsonDeserializer<Hmac> {

  private static final int LENGTH_INDEX = 0;
  private static final int HASH_TYPE_INDEX = 1;
  private static final int HMAC_INDEX = 2;

  @Override
  public Hmac deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

    ObjectCodec objectCodec = jsonParser.getCodec();
    JsonNode jsonNode = objectCodec.readTree(jsonParser);

    jsonNode.get(LENGTH_INDEX).asInt();
    HashType hashType = HashType.valueOf(jsonNode.get(HASH_TYPE_INDEX).asInt());
    byte[] hmac = jsonNode.get(HMAC_INDEX).binaryValue();
    return new Hmac(hashType, hmac);
  }
}
