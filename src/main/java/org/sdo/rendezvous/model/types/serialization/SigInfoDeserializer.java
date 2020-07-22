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
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.sdo.rendezvous.model.types.SigInfo;

@Slf4j
public class SigInfoDeserializer extends JsonDeserializer<SigInfo> {

  private static final int PK_TYPE_INDEX = 0;
  private static final int LENGTH_INDEX = 1;
  private static final int SIGINFO_INDEX = 2;
  private static final Set<PublicKeyType> ALLOWED_TYPES =
      ImmutableSet.of(
          PublicKeyType.EPID_1_0,
          PublicKeyType.EPID_1_1,
          PublicKeyType.EPID_2_0,
          PublicKeyType.ECDSA_P_256,
          PublicKeyType.ECDSA_P_384,
          PublicKeyType.ONDIE_ECDSA_384);

  @Override
  public SigInfo deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException {
    ObjectCodec objectCodec = jsonParser.getCodec();
    JsonNode jsonNode = objectCodec.readTree(jsonParser);

    PublicKeyType pkType = PublicKeyType.valueOf(jsonNode.get(PK_TYPE_INDEX).asInt());
    if (!ALLOWED_TYPES.contains(pkType)) {
      log.error("Invalid EPID public key type. Public key type: {}.", pkType.name());
      throw new JsonParseException(jsonParser, "SigInfo is invalid.");
    }
    jsonNode.get(LENGTH_INDEX).asInt();
    byte[] sigInfo = jsonNode.get(SIGINFO_INDEX).binaryValue();
    return new SigInfo(pkType, sigInfo);
  }
}
