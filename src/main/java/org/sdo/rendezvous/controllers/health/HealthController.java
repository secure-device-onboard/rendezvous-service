// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.controllers.health;

import lombok.AllArgsConstructor;
import org.sdo.rendezvous.config.InfoConfig;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.logging.aspects.generic.LogExecutionTime;
import org.sdo.rendezvous.model.SdoUrlMapping;
import org.sdo.rendezvous.model.responses.FullHealthResponse;
import org.sdo.rendezvous.model.responses.HealthResponse;
import org.sdo.rendezvous.model.types.ComponentStatus;
import org.sdo.rendezvous.model.types.ServiceStatus;
import org.sdo.rendezvous.services.HealthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The class exposes endpoints responsible for checking whether the RV service itself and all
 * dependencies work.
 */
@RestController
@AllArgsConstructor
public class HealthController {

  private final InfoConfig infoConfig;
  private final HealthService healthService;
  private final RendezvousConfig rendezvousConfig;

  /**
   * Exposes endpoints /mp/110/health, /mp/112/health, /mp/113/health responsibe for checking RV
   * itself.
   *
   * @return the HealthResponse with HTTP status code
   */
  @LogExecutionTime
  @GetMapping(
      value = {SdoUrlMapping.HEALTH_110, SdoUrlMapping.HEALTH_112, SdoUrlMapping.HEALTH_113})
  public ResponseEntity<HealthResponse> checkHealth() {

    HealthResponse hr = new HealthResponse(infoConfig.getVersion());

    return new ResponseEntity<>(hr, HttpStatus.OK);
  }

  /**
   * Exposes endpoints /mp/110/health/full, /mp/112/health/full, /mp/113/health/full responsibe for
   * checking all dependencies (redis and Verification service connection).
   *
   * @return the FullHealthResponse with HTTP status code
   */
  @LogExecutionTime
  @GetMapping(
      value = {
        SdoUrlMapping.FULL_HEALTH_110,
        SdoUrlMapping.FULL_HEALTH_112,
        SdoUrlMapping.FULL_HEALTH_113
      })
  public ResponseEntity<FullHealthResponse> checkFullHealth() {

    FullHealthResponse fullHealthResponse;

    if (rendezvousConfig.isSignatureVerification()) {
      fullHealthResponse =
          new FullHealthResponse(
              new ServiceStatus(ComponentStatus.Status.OK, infoConfig.getVersion()),
              healthService.checkVerificationServiceStatus(),
              healthService.checkRedisStatus());
    } else {
      fullHealthResponse =
          new FullHealthResponse(
              new ServiceStatus(ComponentStatus.Status.OK, infoConfig.getVersion()),
              healthService.checkRedisStatus());
    }
    return new ResponseEntity<>(fullHealthResponse, HttpStatus.OK);
  }
}
