// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.log.to1;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Getter;

@Getter
public class TO1TransactionInfo {

  @JsonProperty("guid")
  String guid;

  @JsonProperty("ct")
  String currentTimestamp;

  public TO1TransactionInfo(String guid) {
    this.guid = guid;
    this.currentTimestamp = new Date().toString();
  }
}
