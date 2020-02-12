// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.sdo.rendezvous.exceptions.InternalCryptoException;
import org.sdo.rendezvous.exceptions.ResourceNotFoundException;
import org.sdo.rendezvous.logging.utils.LoggingUtils;

@Slf4j
public class PKECDSAEnc extends PubKey {

  private byte[] pubKeyBytes;

  /**
   * Creates an instance of PKECDSAEnc specified by publicKeyType and pubKeyBytes.
   *
   * @param publicKeyType the type of public key
   * @param pubKeyBytes the public key as an array of bytes
   */
  public PKECDSAEnc(PublicKeyType publicKeyType, byte[] pubKeyBytes) {
    this.pubKeyBytes = pubKeyBytes;
    this.pkType = publicKeyType;
    this.pkEnc = PublicKeyEncoding.NONE;
  }

  @Override
  public PublicKey asJavaPublicKey() throws InternalCryptoException, ResourceNotFoundException {
    if (pubKeyBytes == null) {
      throw new ResourceNotFoundException("Not found public key for given GUID.");
    }
    try {
      KeyFactory kf;
      kf = KeyFactory.getInstance("EC", new BouncyCastleProvider());
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubKeyBytes);
      return kf.generatePublic(keySpec);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      log.debug(
          "Could not convert ECDSA key from database to PublicKey object. Error: {}",
          e.getMessage());
      throw new InternalCryptoException("Could not construct a valid public key.");
    }
  }

  @Override
  protected String pubkeyToString() {
    return String.format("PKECDSAEnc[pkBytes=%s]", LoggingUtils.getEdgeBytesOfArray(pubKeyBytes));
  }
}
