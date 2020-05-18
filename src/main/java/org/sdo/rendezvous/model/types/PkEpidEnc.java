// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import lombok.Getter;
import org.sdo.rendezvous.model.types.serialization.PkEpidEncDeserializer;
import org.sdo.rendezvous.model.types.serialization.PkEpidEncSerializer;

@JsonSerialize(using = PkEpidEncSerializer.class)
@JsonDeserialize(using = PkEpidEncDeserializer.class)
@Getter
public class PkEpidEnc extends PubKey {

  private byte[] epidGroupNo;

  /**
   * Creates an instance of PKEPIDEnc specified by publicKeyType and epidGroupNo.
   *
   * @param publicKeyType the type of public key
   * @param epidGroupNo the number of epid group as an array of bytes
   */
  public PkEpidEnc(PublicKeyType publicKeyType, byte[] epidGroupNo) {
    this.pkType = publicKeyType;
    this.pkEnc = PublicKeyEncoding.EPID;
    this.epidGroupNo = epidGroupNo;
  }

  @Override
  public PublicKey asJavaPublicKey() throws NoSuchAlgorithmException {
    throw new NoSuchAlgorithmException(
        "There is no way to create java PublicKey base on Epid key.");
  }

  @Override
  public String pubkeyToString() {
    return String.format("PKEPIDEnc[epidGroupNo=%s]", printHexBinary(epidGroupNo));
  }
}
