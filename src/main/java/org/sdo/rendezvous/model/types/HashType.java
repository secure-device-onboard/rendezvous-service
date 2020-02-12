// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum HashType {
  NONE(0, ""),
  SHA256(8, "SHA-256"),
  SHA384(14, "SHA-384"),
  SHA512(10, "SHA-512"),
  HMAC_SHA256(108, "SHA-256"),
  HMAC_SHA384(114, "SHA-384"),
  HMAC_SHA512(110, "SHA-512");

  private int index;
  private String algorithmName;

  /**
   * Returns the enum constant of HashType specified by index.
   *
   * @param index the index of enum type
   * @return the enum constant of HashType
   */
  public static HashType valueOf(int index) {

    for (HashType hashType : HashType.values()) {
      if (hashType.index == index) {
        return hashType;
      }
    }
    throw new IllegalArgumentException("Can't find HashType enum for getIndex: " + index);
  }
}
