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
import org.sdo.rendezvous.model.database.VersionedTo1Data;
import org.sdo.rendezvous.model.types.OwnerSignTo1Data;
import org.sdo.rendezvous.model.types.OwnerSignTo1DataBody;
import org.sdo.rendezvous.model.types.PkNull;
import org.sdo.rendezvous.model.types.Signature;
import org.sdo.rendezvous.utils.JsonUtils;

public class VersionedTo1WithEcdsaDeserializer extends JsonDeserializer<VersionedTo1Data> {

  private static final String BACKWARD_MODEL_VERSION = "1";

  @Override
  public VersionedTo1Data deserialize(JsonParser jsonParser, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    ObjectCodec objectCodec = jsonParser.getCodec();
    JsonNode jsonNode = objectCodec.readTree(jsonParser);

    OwnerSignTo1Data ownerSignTO1Data = getOwnerSignTO1Data(jsonNode);
    VersionedTo1Data versionedTO1Data = new VersionedTo1Data(ownerSignTO1Data);

    Optional<JsonNode> modelVersion =
        Optional.ofNullable(jsonNode.get(VersionedTo1Data.MODEL_VERSION_TAG));
    if (modelVersion.isPresent()) {
      versionedTO1Data.setModelVersion(modelVersion.get().asText());
    } else {
      versionedTO1Data.setModelVersion(BACKWARD_MODEL_VERSION);
    }

    Optional<JsonNode> ecdsaPublicKey =
        Optional.ofNullable(jsonNode.get(VersionedTo1Data.ECDSA_PUBLICKEY_TAG));
    if (ecdsaPublicKey.isPresent()) {
      versionedTO1Data.setEcdsaPublicKey(ecdsaPublicKey.get().binaryValue());
    }

    return versionedTO1Data;
  }

  private OwnerSignTo1Data getOwnerSignTO1Data(JsonNode jsonNode) throws IOException {
    return new OwnerSignTo1Data(
        JsonUtils.mapJsonToObject(
            jsonNode.get(OwnerSignTo1Data.TO1_DATA_BODY_TAG).toString(),
            OwnerSignTo1DataBody.class),
        JsonUtils.mapJsonToObject(
            jsonNode.get(OwnerSignTo1Data.PUBKEY_TAG).toString(), PkNull.class),
        JsonUtils.mapJsonToObject(
            jsonNode.get(OwnerSignTo1Data.SIGNATURE_TAG).toString(), Signature.class));
  }
}
