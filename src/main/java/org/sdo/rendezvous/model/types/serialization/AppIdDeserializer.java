// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.sdo.rendezvous.model.types.AppId;

public class AppIdDeserializer extends JsonDeserializer<AppId> {

  private static final int LENGTH_INDEX = 0;
  private static final int TYPE_INDEX = 1;
  private static final int APP_ID_INDEX = 2;

  @Override
  public AppId deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

    ObjectCodec objectCodec = jsonParser.getCodec();
    JsonNode jsonNode = objectCodec.readTree(jsonParser);

    jsonNode.get(LENGTH_INDEX).asInt();
    int type = jsonNode.get(TYPE_INDEX).asInt();
    byte[] appId = jsonNode.get(APP_ID_INDEX).binaryValue();
    return new AppId(type, appId);
  }
}
