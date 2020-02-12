// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import lombok.Getter;

@Getter
public class ServiceStatus extends ComponentStatus {

  private String version;

  public ServiceStatus(Status status, String version) {
    super(status);
    this.version = version;
  }
}
