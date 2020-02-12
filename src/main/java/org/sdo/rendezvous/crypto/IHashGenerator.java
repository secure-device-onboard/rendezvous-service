// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.crypto;

import java.security.NoSuchAlgorithmException;
import org.sdo.rendezvous.model.types.HashType;
import org.springframework.stereotype.Component;

/** The interface for hash generators. */
@Component
public interface IHashGenerator {

  /**
   * Returns the hash computation.
   *
   * @param data the input to be updated before the digest is completed
   * @param algorithm the instance of HashType which represents the requested algorithm
   * @return the array of bytes for the resulting hash value
   * @throws NoSuchAlgorithmException if no Provider supports a MessageDigestSpi implementation for
   *     the specified algorithm
   */
  byte[] generate(byte[] data, HashType algorithm) throws NoSuchAlgorithmException;

  /**
   * Returns the hash computation based on SHA-256.
   *
   * @param data the input to be updated before the digest is completed
   * @return the array of bytes for the resulting hash value
   * @throws NoSuchAlgorithmException if no Provider supports a MessageDigestSpi implementation for
   *     the specified algorithm
   */
  byte[] hashSha256(byte[] data) throws NoSuchAlgorithmException;
}
