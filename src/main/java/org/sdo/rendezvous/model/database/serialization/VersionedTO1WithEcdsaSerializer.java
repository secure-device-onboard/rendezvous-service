// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.database.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.sdo.rendezvous.model.database.VersionedTO1Data;
import org.sdo.rendezvous.model.types.OwnerSignTO1Data;

public class VersionedTO1WithEcdsaSerializer extends JsonSerializer<VersionedTO1Data> {

  @Override
  public void serialize(
      VersionedTO1Data value, JsonGenerator jsonGenerator, SerializerProvider serializers)
      throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeStringField(VersionedTO1Data.MODEL_VERSION_TAG, value.getModelVersion());
    jsonGenerator.writeObjectField(
        OwnerSignTO1Data.TO1_DATA_BODY_TAG, value.getTo1Data().getBody());
    jsonGenerator.writeObjectField(OwnerSignTO1Data.PUBKEY_TAG, value.getTo1Data().getPubKey());
    jsonGenerator.writeObjectField(
        OwnerSignTO1Data.SIGNATURE_TAG, value.getTo1Data().getSignature());
    if (value.getEcdsaPublicKey() != null) {
      jsonGenerator.writeBinaryField(
          VersionedTO1Data.ECDSA_PUBLICKEY_TAG, value.getEcdsaPublicKey());
    }
    jsonGenerator.writeEndObject();
  }
}
