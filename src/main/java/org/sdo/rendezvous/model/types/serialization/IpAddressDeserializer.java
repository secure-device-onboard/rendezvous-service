// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import javax.xml.bind.DatatypeConverter;
import org.sdo.rendezvous.model.types.IpAddress;

public class IpAddressDeserializer extends JsonDeserializer<IpAddress> {

  private static final int LENGTH_INDEX = 0;
  private static final int ADDRESS_INDEX = 1;

  @Override
  public IpAddress deserialize(JsonParser jsonParser, DeserializationContext ctxt)
      throws IOException {

    ObjectCodec objectCodec = jsonParser.getCodec();
    JsonNode jsonNode = objectCodec.readTree(jsonParser);

    jsonNode.get(LENGTH_INDEX).asInt();
    byte[] ipAddress = DatatypeConverter.parseBase64Binary(jsonNode.get(ADDRESS_INDEX).asText());
    return new IpAddress(ipAddress);
  }
}
