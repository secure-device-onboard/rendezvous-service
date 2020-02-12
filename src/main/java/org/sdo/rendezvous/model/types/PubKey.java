// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import lombok.Getter;
import org.sdo.rendezvous.exceptions.InternalCryptoException;
import org.sdo.rendezvous.exceptions.ResourceNotFoundException;
import org.sdo.rendezvous.model.types.serialization.PubKeyDeserializer;

@JsonDeserialize(using = PubKeyDeserializer.class)
@Getter
public abstract class PubKey {

  protected PublicKeyType pkType;

  protected PublicKeyEncoding pkEnc;

  public abstract PublicKey asJavaPublicKey()
      throws NoSuchAlgorithmException, InternalCryptoException, ResourceNotFoundException;

  protected abstract String pubkeyToString();

  @Override
  public String toString() {
    return "[pkType=" + pkType + ", pkEnc=" + pkEnc + ", " + pubkeyToString() + "]";
  }
}
