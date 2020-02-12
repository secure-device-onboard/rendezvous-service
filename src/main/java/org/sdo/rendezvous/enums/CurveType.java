// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.enums;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CurveType {
  None(0),
  Secp256r1(256),
  Secp384r1(384);

  private int curveId;

  public static CurveType getEnum(int value) {
    return Arrays.stream(values()).filter(d -> d.curveId == value).findFirst().orElse(None);
  }
}
