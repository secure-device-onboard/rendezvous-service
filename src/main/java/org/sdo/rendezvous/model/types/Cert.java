// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.sdo.rendezvous.logging.utils.LoggingUtils;

@AllArgsConstructor
@Data
public class Cert {

  private int length;

  private byte[] certBytes;

  @Override
  public String toString() {
    return LoggingUtils.getEdgeBytesOfArray(certBytes);
  }
}
