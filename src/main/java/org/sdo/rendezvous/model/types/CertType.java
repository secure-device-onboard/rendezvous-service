// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CertType {
  X509(1);

  private int index;

  /**
   * Return the enum constant of CertType specified by index.
   *
   * @param index the index of enum type
   * @return the enum constant of CertType
   */
  public static CertType valueOf(int index) {

    for (CertType certType : CertType.values()) {
      if (certType.index == index) {
        return certType;
      }
    }
    throw new IllegalArgumentException("Can't find CertType enum for index: " + index);
  }

  @Override
  public String toString() {
    return index + "(" + name() + ")";
  }
}
