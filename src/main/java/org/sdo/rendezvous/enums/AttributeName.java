// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AttributeName {
  GUID(0),
  SESSION_ID(1);

  private int attributeId;
}
