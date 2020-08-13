// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.sdo.rendezvous.exceptions.InternalCryptoException;
import org.sdo.rendezvous.exceptions.ResourceNotFoundException;
import org.sdo.rendezvous.model.types.PubKey;
import org.sdo.rendezvous.model.types.PublicKeyEncoding;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.sdo.rendezvous.model.types.serialization.PkOnDieEcdsaNullDeserializer;
import org.sdo.rendezvous.model.types.serialization.PkOnDieEcdsaNullSerializer;

@Slf4j
@JsonSerialize(using = PkOnDieEcdsaNullSerializer.class)
@JsonDeserialize(using = PkOnDieEcdsaNullDeserializer.class)
public class PkOnDieEcdsaNull extends PubKey {

  private static final String DISPLAY_NAME = "PKNull[0]";

  public PkOnDieEcdsaNull() {
    this.pkType = PublicKeyType.ONDIE_ECDSA_384;
    this.pkEnc = PublicKeyEncoding.ONDIE_ECDSA;
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
