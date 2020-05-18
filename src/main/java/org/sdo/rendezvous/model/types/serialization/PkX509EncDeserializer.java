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
import org.sdo.rendezvous.model.types.PkX509Enc;
import org.sdo.rendezvous.model.types.PublicKeyType;

public class PkX509EncDeserializer extends JsonDeserializer<PkX509Enc> {

  private static final int PK_TYPE_INDEX = 0;
  private static final int PKX509ENC_OBJECT_INDEX = 2;
  private static final int PK_BYTES_INDEX = 0;
  private static final int PKX509_INDEX = 1;
  private static final ImmutableSet<PublicKeyType> ALLOWED_TYPES =
      ImmutableSet.of(
          PublicKeyType.RSA2048RESTR, PublicKeyType.ECDSA_P_256, PublicKeyType.ECDSA_P_384);

  @Override
  public PkX509Enc deserialize(JsonParser jsonParser, DeserializationContext ctxt)
      throws IOException {

    ObjectCodec objectCodec = jsonParser.getCodec();
    JsonNode jsonNode = objectCodec.readTree(jsonParser);

    PublicKeyType pkType = PublicKeyType.valueOf(jsonNode.get(PK_TYPE_INDEX).asInt());
    if (!ALLOWED_TYPES.contains(pkType)) {
      throw new JsonParseException(jsonParser, "PKX509ENC has wrong type.");
    }

    JsonNode pkX509EncObject = jsonNode.get(PKX509ENC_OBJECT_INDEX);
    if (!pkX509EncObject.isArray()) {
      throw new JsonParseException(jsonParser, "PKX509ENC (jsonNode) is not a array.");
    }

    pkX509EncObject.get(PK_BYTES_INDEX).asInt();
    byte[] pkX509 = pkX509EncObject.get(PKX509_INDEX).binaryValue();

    return new PkX509Enc(pkType, pkX509);
  }
}
