// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import org.sdo.rendezvous.model.types.serialization.PkNullDeserializer;
import org.sdo.rendezvous.model.types.serialization.PkNullSerializer;

@JsonSerialize(using = PkNullSerializer.class)
@JsonDeserialize(using = PkNullDeserializer.class)
public class PkNull extends PubKey {

  private static final String DISPLAY_NAME = "PKNull[0]";

  public PkNull() {
    this.pkType = PublicKeyType.NONE;
    this.pkEnc = PublicKeyEncoding.NONE;
  }

  @Override
  public PublicKey asJavaPublicKey() throws NoSuchAlgorithmException {
    throw new NoSuchAlgorithmException("Null public keys cannot be converted to Java public keys.");
  }

  @Override
  protected String pubkeyToString() {
    return DISPLAY_NAME;
  }
}
