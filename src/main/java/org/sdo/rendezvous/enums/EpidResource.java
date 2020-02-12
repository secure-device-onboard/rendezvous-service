// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.enums;

public enum EpidResource {
  GROUPCERTSIGMA10("PUBKEY.CRT.BIN"),
  GROUPCERTSIGMA11("PUBKEY.CRT"),
  SIGRL("SIGRL"),
  PUBKEY("PUBKEY");

  private final String epidResourceName;

  EpidResource(String epidResourceName) {
    this.epidResourceName = epidResourceName;
  }

  public String toLowerCaseString() {
    return epidResourceName.toLowerCase();
  }
}
