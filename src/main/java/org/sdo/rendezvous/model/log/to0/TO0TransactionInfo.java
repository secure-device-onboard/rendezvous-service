// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.log.to0;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TO0TransactionInfo {

  @JsonProperty("guid")
  private final String guid;

  @JsonProperty("ip")
  private final String ipAddress;

  @JsonProperty("dns")
  private final String dnsAddress;

  @JsonProperty("ct")
  private final String currentTimestamp = new Date().toString();

  @JsonProperty("pkHashes")
  private final List<String> publicKeysHashes;
}
