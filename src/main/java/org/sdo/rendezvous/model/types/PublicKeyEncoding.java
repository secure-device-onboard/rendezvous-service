// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PublicKeyEncoding {
  NONE(0),
  X509(1),
  RSAMODEXP(3),
  EPID(4);

  private int index;

  /**
   * Returns the enum constant of public key encoding specified by index.
   *
   * @param index the public key encoding
   * @return the enum constant with the specified index
   */
  public static PublicKeyEncoding valueOf(int index) {

    for (PublicKeyEncoding publicKeyEncoding : PublicKeyEncoding.values()) {
      if (publicKeyEncoding.index == index) {
        return publicKeyEncoding;
      }
    }
    throw new IllegalArgumentException("Can't find PublicKeyEncoding enum for index: " + index);
  }

  @Override
  public String toString() {
    return index + "(" + name() + ")";
  }
}
