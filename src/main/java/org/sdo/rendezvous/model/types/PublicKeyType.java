// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Public Key Type. */
@AllArgsConstructor
@Getter
public enum PublicKeyType {
  NONE(0),
  RSA2048RESTR(1),
  DH(2),
  DSA(3),
  RSA_UR(4),
  ECDSA_P_256(13),
  ECDSA_P_384(14),
  EPID_1_0(90),
  EPID_1_1(91),
  EPID_2_0(92),
  ECC_P_256(113),
  ECC_P_384(114);

  private int index;

  /**
   * Returns the enum constant of public key type specified by index.
   *
   * @param index the public key number
   * @return the enum constant with the specified index
   * @throws IllegalArgumentException if this enum type has no constant with the specified index
   */
  public static PublicKeyType valueOf(int index) {
    for (PublicKeyType publicKeyType : PublicKeyType.values()) {
      if (publicKeyType.index == index) {
        return publicKeyType;
      }
    }
    throw new IllegalArgumentException("Can't find PublicKeyType enum for index: " + index);
  }

  public boolean isNone() {
    return this == NONE;
  }

  public boolean isEcdsa() {
    return this == ECDSA_P_256 || this == ECDSA_P_384;
  }

  public boolean isEpid() {
    return this == EPID_1_0 || this == EPID_1_1 || this == EPID_2_0;
  }

  @Override
  public String toString() {
    return index + "(" + name() + ")";
  }
}
