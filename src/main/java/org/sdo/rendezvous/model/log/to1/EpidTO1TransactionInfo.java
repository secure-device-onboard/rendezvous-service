// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.log.to1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class EpidTO1TransactionInfo extends TO1TransactionInfo {

  @JsonProperty("gid")
  String epidGroupId;

  public EpidTO1TransactionInfo(String guid, String epidGroupId) {
    super(guid);
    this.epidGroupId = epidGroupId;
  }
}
