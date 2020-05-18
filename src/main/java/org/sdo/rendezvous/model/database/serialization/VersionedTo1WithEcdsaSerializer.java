// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.database.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.sdo.rendezvous.model.database.VersionedTo1Data;
import org.sdo.rendezvous.model.types.OwnerSignTo1Data;

public class VersionedTo1WithEcdsaSerializer extends JsonSerializer<VersionedTo1Data> {

  @Override
  public void serialize(
      VersionedTo1Data value, JsonGenerator jsonGenerator, SerializerProvider serializers)
      throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeStringField(VersionedTo1Data.MODEL_VERSION_TAG, value.getModelVersion());
    jsonGenerator.writeObjectField(
        OwnerSignTo1Data.TO1_DATA_BODY_TAG, value.getTo1Data().getBody());
    jsonGenerator.writeObjectField(OwnerSignTo1Data.PUBKEY_TAG, value.getTo1Data().getPubKey());
    jsonGenerator.writeObjectField(
        OwnerSignTo1Data.SIGNATURE_TAG, value.getTo1Data().getSignature());
    if (value.getEcdsaPublicKey() != null) {
      jsonGenerator.writeBinaryField(
          VersionedTo1Data.ECDSA_PUBLICKEY_TAG, value.getEcdsaPublicKey());
    }
    jsonGenerator.writeEndObject();
  }
}
