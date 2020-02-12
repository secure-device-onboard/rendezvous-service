// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.model.responses.VerificationServiceResponse;
import org.sdo.rendezvous.model.types.ComponentStatus;
import org.sdo.rendezvous.model.types.ServiceStatus;
import org.sdo.rendezvous.utils.validators.RedisConnectionChecker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class HealthService {

  private static final String VERIFICATION_HEALTH_ENDPOINT = "/health";

  private final RedisConnectionChecker redisConnectionChecker;
  private final RendezvousConfig rendezvousConfig;
  private final EpidVerificationService epidVerificationService;

  /**
   * Checks the connection to a Redis database.
   *
   * @return the status of Redis database as an object of component status
   */
  public ComponentStatus checkRedisStatus() {
    if (redisConnectionChecker.isPoolConnected()) {
      return new ComponentStatus(ComponentStatus.Status.OK);
    }
    log.error("Redis connection check failed.");
    return new ComponentStatus(ComponentStatus.Status.ERROR);
  }

  /**
   * Checks the connection to the Verification Service.
   *
   * @return the status of Verification service as an object of component status
   */
  public ComponentStatus checkVerificationServiceStatus() {
    String verificationHealthUrl =
        rendezvousConfig.getVerificationServiceHost() + VERIFICATION_HEALTH_ENDPOINT;

    ResponseEntity<String> response =
        epidVerificationService.getEpidVerificationHealth(verificationHealthUrl);

    if (response.getStatusCode() != HttpStatus.OK) {
      log.error("Connection to verification service {} failed", verificationHealthUrl);
      return new ComponentStatus(ComponentStatus.Status.ERROR);
    }

    VerificationServiceResponse verificationServiceResponse;
    try {
      verificationServiceResponse = parseVerificationServiceResponse(response.getBody());
    } catch (IOException exception) {
      return new ComponentStatus(ComponentStatus.Status.ERROR);
    }

    return new ServiceStatus(ComponentStatus.Status.OK, verificationServiceResponse.getVersion());
  }

  private VerificationServiceResponse parseVerificationServiceResponse(String responseBody)
      throws IOException {

    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(responseBody, VerificationServiceResponse.class);
  }
}
