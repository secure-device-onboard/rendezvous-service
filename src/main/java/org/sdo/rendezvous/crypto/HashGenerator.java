// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.sdo.rendezvous.model.types.HashType;
import org.springframework.stereotype.Component;

/** This is the HashGenerator. */
@Component
public class HashGenerator implements IHashGenerator {

  @Override
  public byte[] generate(byte[] data, HashType algorithm) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance(algorithm.getAlgorithmName());
    md.update(data);
    return md.digest();
  }

  @Override
  public byte[] hashSha256(byte[] data) throws NoSuchAlgorithmException {
    return generate(data, HashType.SHA256);
  }
}
