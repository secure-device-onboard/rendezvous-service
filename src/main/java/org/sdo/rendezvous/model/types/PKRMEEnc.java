// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.exceptions.InternalCryptoException;
import org.sdo.rendezvous.logging.utils.LoggingUtils;
import org.sdo.rendezvous.model.types.serialization.PKRMEEncDeserializer;
import org.sdo.rendezvous.model.types.serialization.PKRMEEncSerializer;

@JsonSerialize(using = PKRMEEncSerializer.class)
@JsonDeserialize(using = PKRMEEncDeserializer.class)
@Getter
@Slf4j
public class PKRMEEnc extends PubKey {

  private byte[] modba;

  private byte[] expba;

  /**
   * Creates an instance of PKRMEEnc specified by publicKeyType, modulus and exponent.
   *
   * @param publicKeyType he type of public key
   * @param modba the RSA modulus as an array of bytes
   * @param expba the RSA public exponent as an array of bytes
   */
  public PKRMEEnc(PublicKeyType publicKeyType, byte[] modba, byte[] expba) {
    this.pkType = publicKeyType;
    this.pkEnc = PublicKeyEncoding.RSAMODEXP;
    this.modba = modba;
    this.expba = expba;
  }

  @Override
  public PublicKey asJavaPublicKey() throws NoSuchAlgorithmException, InternalCryptoException {
    BigInteger modulus = new BigInteger(modba);
    BigInteger exp = new BigInteger(expba);
    try {
      return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, exp));
    } catch (InvalidKeySpecException e) {
      log.debug(e.getMessage());
      throw new InternalCryptoException("Could not construct a valid public key.");
    }
  }

  @Override
  public String pubkeyToString() {
    return "PKRMEEnc[modulus="
        + LoggingUtils.getEdgeBytesOfArray(modba)
        + ", exponent="
        + printHexBinary(expba)
        + "]";
  }
}
