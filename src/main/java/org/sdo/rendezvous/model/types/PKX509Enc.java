// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.exceptions.InternalCryptoException;
import org.sdo.rendezvous.logging.utils.LoggingUtils;
import org.sdo.rendezvous.model.types.serialization.PKX509EncDeserializer;
import org.sdo.rendezvous.model.types.serialization.PKX509EncSerializer;

@JsonDeserialize(using = PKX509EncDeserializer.class)
@JsonSerialize(using = PKX509EncSerializer.class)
@Getter
@Slf4j
public class PKX509Enc extends PubKey {

  private byte[] pkX509;

  /**
   * Creates an instance of PKX509Enc specified by publicKeyType and epidGroupNo.
   *
   * @param publicKeyType the type of public key
   * @param pkX509 the public key in X.509 encoding as an array of bytes
   */
  public PKX509Enc(PublicKeyType publicKeyType, byte[] pkX509) {
    this.pkType = publicKeyType;
    this.pkEnc = PublicKeyEncoding.X509;
    this.pkX509 = pkX509;
  }

  @Override
  public PublicKey asJavaPublicKey() throws InternalCryptoException {
    try {
      if (pkType.isEcdsa()) {
        return KeyFactory.getInstance("ECDSA").generatePublic(new X509EncodedKeySpec(pkX509));
      }
      return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pkX509));
    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
      log.debug(e.getMessage());
      throw new InternalCryptoException("Could not construct a valid public key.");
    }
  }

  @Override
  protected String pubkeyToString() {
    return String.format("PK509Enc[pkx509=%s]", LoggingUtils.getEdgeBytesOfArray(pkX509));
  }
}
