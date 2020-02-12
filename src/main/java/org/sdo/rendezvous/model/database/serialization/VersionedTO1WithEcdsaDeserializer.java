// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.database.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.Optional;
import org.sdo.rendezvous.model.database.VersionedTO1Data;
import org.sdo.rendezvous.model.types.OwnerSignTO1Data;
import org.sdo.rendezvous.model.types.OwnerSignTO1DataBody;
import org.sdo.rendezvous.model.types.PKNull;
import org.sdo.rendezvous.model.types.Signature;
import org.sdo.rendezvous.utils.JsonUtils;

public class VersionedTO1WithEcdsaDeserializer extends JsonDeserializer<VersionedTO1Data> {

  private static final String BACKWARD_MODEL_VERSION = "1";

  @Override
  public VersionedTO1Data deserialize(JsonParser jsonParser, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    ObjectCodec objectCodec = jsonParser.getCodec();
    JsonNode jsonNode = objectCodec.readTree(jsonParser);

    OwnerSignTO1Data ownerSignTO1Data = getOwnerSignTO1Data(jsonNode);
    VersionedTO1Data versionedTO1Data = new VersionedTO1Data(ownerSignTO1Data);

    Optional<JsonNode> modelVersion =
        Optional.ofNullable(jsonNode.get(VersionedTO1Data.MODEL_VERSION_TAG));
    if (modelVersion.isPresent()) {
      versionedTO1Data.setModelVersion(modelVersion.get().asText());
    } else {
      versionedTO1Data.setModelVersion(BACKWARD_MODEL_VERSION);
    }

    Optional<JsonNode> ecdsaPublicKey =
        Optional.ofNullable(jsonNode.get(VersionedTO1Data.ECDSA_PUBLICKEY_TAG));
    if (ecdsaPublicKey.isPresent()) {
      versionedTO1Data.setEcdsaPublicKey(ecdsaPublicKey.get().binaryValue());
    }

    return versionedTO1Data;
  }

  private OwnerSignTO1Data getOwnerSignTO1Data(JsonNode jsonNode) throws IOException {
    return new OwnerSignTO1Data(
        JsonUtils.mapJsonToObject(
            jsonNode.get(OwnerSignTO1Data.TO1_DATA_BODY_TAG).toString(),
            OwnerSignTO1DataBody.class),
        JsonUtils.mapJsonToObject(
            jsonNode.get(OwnerSignTO1Data.PUBKEY_TAG).toString(), PKNull.class),
        JsonUtils.mapJsonToObject(
            jsonNode.get(OwnerSignTO1Data.SIGNATURE_TAG).toString(), Signature.class));
  }
}
