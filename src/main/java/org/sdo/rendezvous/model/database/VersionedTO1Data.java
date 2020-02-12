// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.database;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.sdo.rendezvous.model.database.serialization.VersionedTO1WithEcdsaDeserializer;
import org.sdo.rendezvous.model.database.serialization.VersionedTO1WithEcdsaSerializer;
import org.sdo.rendezvous.model.types.OwnerSignTO1Data;

@Setter
@Getter
@JsonSerialize(using = VersionedTO1WithEcdsaSerializer.class)
@JsonDeserialize(using = VersionedTO1WithEcdsaDeserializer.class)
public class VersionedTO1Data {
  public static final String MODEL_VERSION_TAG = "modelVersion";
  public static final String ECDSA_PUBLICKEY_TAG = "ecdsaPublicKey";

  public VersionedTO1Data(OwnerSignTO1Data to1Data) {
    this.to1Data = to1Data;
  }

  @JsonProperty(MODEL_VERSION_TAG)
  private String modelVersion = "2";

  @JsonProperty("to1d")
  private OwnerSignTO1Data to1Data;

  @JsonProperty(ECDSA_PUBLICKEY_TAG)
  private byte[] ecdsaPublicKey;
}
