// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import org.sdo.rendezvous.model.types.PkEpidEnc;
import org.sdo.rendezvous.model.types.PublicKeyType;

public class PkEpidEncDeserializer extends JsonDeserializer<PkEpidEnc> {

  private static final int PK_TYPE_INDEX = 0;
  private static final int PKEPIDENC_OBJECT_INDEX = 2;
  private static final int PK_EPID_GROUP_NO_BYTES_INDEX = 0;
  private static final int PK_EPID_GROUP_NO_INDEX = 1;
  private static final ImmutableSet<PublicKeyType> ALLOWED_TYPES =
      ImmutableSet.of(PublicKeyType.EPID_1_0, PublicKeyType.EPID_1_1, PublicKeyType.EPID_2_0);

  @Override
  public PkEpidEnc deserialize(JsonParser jsonParser, DeserializationContext ctxt)
      throws IOException {

    ObjectCodec objectCodec = jsonParser.getCodec();
    JsonNode jsonNode = objectCodec.readTree(jsonParser);

    PublicKeyType pkType = PublicKeyType.valueOf(jsonNode.get(PK_TYPE_INDEX).asInt());
    if (!ALLOWED_TYPES.contains(pkType)) {
      throw new JsonParseException(jsonParser, "PKEPIDEnc has wrong type.");
    }

    JsonNode pkEpidEncObject = jsonNode.get(PKEPIDENC_OBJECT_INDEX);
    if (!pkEpidEncObject.isArray()) {
      throw new JsonParseException(jsonParser, "PKEPIDEnc (jsonNode) is not a array.");
    }

    pkEpidEncObject.get(PK_EPID_GROUP_NO_BYTES_INDEX).asInt();
    byte[] epidGroupNo = pkEpidEncObject.get(PK_EPID_GROUP_NO_INDEX).binaryValue();

    return new PkEpidEnc(pkType, epidGroupNo);
  }
}
