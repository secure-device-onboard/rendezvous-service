// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services;

import static org.mockito.ArgumentMatchers.anyString;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.model.types.ComponentStatus;
import org.sdo.rendezvous.utils.validators.RedisConnectionChecker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class HealthServiceTest {

  private static final String VERIFICATION_SERVICE_URL = "https://verification.com";
  private static final String VERIFICATION_SERVICE_VERSION = "1.2.3.4";
  private static final ResponseEntity<String> VERIFICATION_RESPONSE_ENTITY_OK =
      new ResponseEntity<>(
          "{\"version\": \"" + VERIFICATION_SERVICE_VERSION + "\"}", HttpStatus.OK);
  private static final ResponseEntity<String> VERIFICATION_RESPONSE_ENTITY_500_ERROR =
      new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
  private static final ResponseEntity<String> VERIFICATION_RESPONSE_ENTITY_OK_MALFORMED_RESPONSE =
      new ResponseEntity<>("{\"status\": \"OK\"}", HttpStatus.OK);

  @Mock private RedisConnectionChecker redisConnectionChecker;

  @Mock private RendezvousConfig rendezvousConfig;

  @Mock private EpidVerificationService epidVerificationService;

  private HealthService healthService;

  @BeforeMethod
  public void beforeMethod() {
    MockitoAnnotations.initMocks(this);
    healthService =
        new HealthService(redisConnectionChecker, rendezvousConfig, epidVerificationService);
  }

  @Test
  public void testCheckRedisStatus_ShouldRespondOkWhenPoolIsConnected() {
    Mockito.when(redisConnectionChecker.isPoolConnected()).thenReturn(true);

    ComponentStatus result = healthService.checkRedisStatus();

    Assert.assertEquals(result.getStatus(), ComponentStatus.Status.OK);
  }

  @Test
  public void testCheckRedisStatus_ShouldRespondErrorWhenPoolIsNotConnected() {
    Mockito.when(redisConnectionChecker.isPoolConnected()).thenReturn(false);

    ComponentStatus result = healthService.checkRedisStatus();

    Assert.assertEquals(result.getStatus(), ComponentStatus.Status.ERROR);
  }

  @Test
  public void testCheckVerSerStatus_ShouldRespondOkWhenVerificationServiceIsConnected() {
    Mockito.when(rendezvousConfig.getVerificationServiceHost())
        .thenReturn(VERIFICATION_SERVICE_URL);
    Mockito.when(epidVerificationService.getEpidVerificationHealth(anyString()))
        .thenReturn(VERIFICATION_RESPONSE_ENTITY_OK);

    ComponentStatus result = healthService.checkVerificationServiceStatus();

    Assert.assertEquals(result.getStatus(), ComponentStatus.Status.OK);
  }

  @Test
  public void testCheckVerSerStatusShouldRespondErrorWhenVerSerIsConnectedAndResponseIsMalformed() {
    Mockito.when(rendezvousConfig.getVerificationServiceHost())
        .thenReturn(VERIFICATION_SERVICE_URL);
    Mockito.when(epidVerificationService.getEpidVerificationHealth(anyString()))
        .thenReturn(VERIFICATION_RESPONSE_ENTITY_OK_MALFORMED_RESPONSE);

    ComponentStatus result = healthService.checkVerificationServiceStatus();

    Assert.assertEquals(result.getStatus(), ComponentStatus.Status.ERROR);
  }

  @Test
  public void testCheckVerSerStatus_ShouldRespondErrorWhenVerificationServiceIsNotConnected() {
    Mockito.when(rendezvousConfig.getVerificationServiceHost())
        .thenReturn(VERIFICATION_SERVICE_URL);
    Mockito.when(epidVerificationService.getEpidVerificationHealth(anyString()))
        .thenReturn(VERIFICATION_RESPONSE_ENTITY_500_ERROR);

    ComponentStatus result = healthService.checkVerificationServiceStatus();

    Assert.assertEquals(result.getStatus(), ComponentStatus.Status.ERROR);
  }
}
