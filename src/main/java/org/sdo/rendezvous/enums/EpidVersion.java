// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.enums;

public enum EpidVersion {
  EPID11(4),
  EPID10(4),
  EPID20(16);

  private int groupIdLength;
  private static final String EPID_PROTOCOL_VERSION = "v2/";

  EpidVersion(int groupIdLength) {
    this.groupIdLength = groupIdLength;
  }

  public int getGroupIdLength() {
    return groupIdLength;
  }

  public String getEpidVersion() {
    return EPID_PROTOCOL_VERSION + toLowerCaseString();
  }

  private String toLowerCaseString() {
    return name().toLowerCase();
  }
}
