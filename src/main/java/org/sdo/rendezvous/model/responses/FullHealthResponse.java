// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.responses;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sdo.rendezvous.model.types.ComponentStatus;

@Getter
@Setter
@NoArgsConstructor
public class FullHealthResponse {

  private static final String RV_SERVICE_KEY = "rvService";
  private static final String VERIFICATION_SERVICE_KEY = "verificationService";
  private static final String DATABASE_SERVICE_KEY = "database";

  private Map<String, ComponentStatus> sdoComponents = new HashMap<>();
  ComponentStatus.Status sdoStatus;

  /**
   * Create a new instance of FullHealthResponse using specified parameters.
   *
   * @param rvService the Rendezvous service status
   * @param verificationService the Verification service status
   * @param database the database status
   */
  public FullHealthResponse(
      ComponentStatus rvService, ComponentStatus verificationService, ComponentStatus database) {
    sdoComponents.put(RV_SERVICE_KEY, rvService);
    sdoComponents.put(VERIFICATION_SERVICE_KEY, verificationService);
    sdoComponents.put(DATABASE_SERVICE_KEY, database);

    setSdoStatus();
  }

  /**
   * Create a new instance of FullHealthResponse using specified parameters.
   *
   * @param rvService the Rendezvous service status
   * @param database the database status
   */
  public FullHealthResponse(ComponentStatus rvService, ComponentStatus database) {
    sdoComponents.put(RV_SERVICE_KEY, rvService);
    sdoComponents.put(DATABASE_SERVICE_KEY, database);

    setSdoStatus();
  }

  private void setSdoStatus() {
    sdoStatus = ComponentStatus.Status.OK;
    for (ComponentStatus component : sdoComponents.values()) {
      if (component.getStatus() != ComponentStatus.Status.OK) {
        sdoStatus = ComponentStatus.Status.ERROR;
      }
    }
  }
}
