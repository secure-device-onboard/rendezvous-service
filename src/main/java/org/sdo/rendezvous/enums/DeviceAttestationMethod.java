// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DeviceAttestationMethod {
  EPID(0),
  ECDSA(1);

  private int methodId;
}
