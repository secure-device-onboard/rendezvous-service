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
import org.sdo.rendezvous.model.types.PKRMEEnc;
import org.sdo.rendezvous.model.types.PublicKeyType;

public class PKRMEEncDeserializer extends JsonDeserializer<PKRMEEnc> {

  private static final int PK_TYPE_INDEX = 0;
  private static final int PK_ENC_INDEX = 1;
  private static final int PKRMEENC_OBJECT_INDEX = 2;
  private static final int MODBYTES_INDEX = 0;
  private static final int MODBA_INDEX = 1;
  private static final int EXPBYTES_INDEX = 2;
  private static final int EXPBA_INDEX = 3;
  private static final ImmutableSet<PublicKeyType> ALLOWED_TYPES =
      ImmutableSet.of(PublicKeyType.RSA2048RESTR);

  @Override
  public PKRMEEnc deserialize(JsonParser jsonParser, DeserializationContext ctxt)
      throws IOException {

    ObjectCodec objectCodec = jsonParser.getCodec();
    JsonNode jsonNode = objectCodec.readTree(jsonParser);

    PublicKeyType pkType = PublicKeyType.valueOf(jsonNode.get(PK_TYPE_INDEX).asInt());
    if (!ALLOWED_TYPES.contains(pkType)) {
      throw new JsonParseException(jsonParser, "PKRMEEnc has wrong type.");
    }

    JsonNode pkrmeEncObject = jsonNode.get(PKRMEENC_OBJECT_INDEX);
    if (!pkrmeEncObject.isArray()) {
      throw new JsonParseException(jsonParser, "PKRMEEnc (jsonNode) is not a array.");
    }

    pkrmeEncObject.get(MODBYTES_INDEX).asInt();
    byte[] modba = pkrmeEncObject.get(MODBA_INDEX).binaryValue();
    pkrmeEncObject.get(EXPBYTES_INDEX).asInt();
    byte[] expba = pkrmeEncObject.get(EXPBA_INDEX).binaryValue();

    return new PKRMEEnc(pkType, modba, expba);
  }
}
