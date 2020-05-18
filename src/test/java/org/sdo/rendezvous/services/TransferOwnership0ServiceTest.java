// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sdo.rendezvous.OwnerSignRequestFactory;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.crypto.HashGenerator;
import org.sdo.rendezvous.model.requests.to0.OwnerSignRequest;
import org.sdo.rendezvous.repositories.JedisRepository;
import org.sdo.rendezvous.repositories.TransferOwnership0Repository;
import org.sdo.rendezvous.utils.CertUtils;
import org.sdo.rendezvous.utils.validators.WaitSecondsValidator;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TransferOwnership0ServiceTest {

  private static final byte[] NONCE = OwnerSignRequestFactory.NONCE;

  @Mock private TransferOwnership0Repository transferOwnership0Repository;

  @Mock private CertUtils certUtils;

  @Mock private WaitSecondsValidator wsValidator;

  @Mock private JedisRepository jedisRepository;

  @Mock private OwnerSignRequestVerifier ownerSignRequestVerifier;

  @Mock private RendezvousConfig rendezvousConfig;

  @Mock private TransferOwnership0Service transferOwnership0Service;

  private HashGenerator hashGenerator;
  private OwnerSignRequest ownerSignRequest;


  /**
   * Variable initialization.
   * @throws Exception for any unhandled exception
   */
  @BeforeMethod
  public void initBeans() throws Exception {
    MockitoAnnotations.initMocks(this);
    hashGenerator = new HashGenerator();
    transferOwnership0Service =
        new TransferOwnership0Service(
            transferOwnership0Repository,
            certUtils,
            wsValidator,
            jedisRepository,
            ownerSignRequestVerifier,
            rendezvousConfig,
            hashGenerator);
    ownerSignRequest = OwnerSignRequestFactory.createOwnerSignRequest();
  }

  @Test
  public void testGetWsSecondsPositive() throws Exception {
    int expectedWs = ownerSignRequest.getTo0Data().getWaitSeconds();
    int actualWs = transferOwnership0Service.getWaitSeconds(ownerSignRequest, NONCE);
    Assert.assertEquals(actualWs, expectedWs);
  }

  @Test
  public void testGetWsSecondsPositiveWs0() throws Exception {
    int expectedWs = ownerSignRequest.getTo0Data().getWaitSeconds();
    Mockito.when(wsValidator.isRemovalRequired(expectedWs)).thenReturn(true);
    int actualWs = transferOwnership0Service.getWaitSeconds(ownerSignRequest, NONCE);
    Assert.assertEquals(actualWs, expectedWs);
  }

  @Test
  public void testGetWsSecondsPositiveWsAboveLimit() throws Exception {
    int expectedWs = 10;
    Mockito.when(wsValidator.isAboveLimit(ownerSignRequest.getTo0Data().getWaitSeconds()))
        .thenReturn(true);
    Mockito.when(rendezvousConfig.getWaitSecondsLimit()).thenReturn(expectedWs);
    int actualWs = transferOwnership0Service.getWaitSeconds(ownerSignRequest, NONCE);
    Assert.assertEquals(actualWs, expectedWs);
  }
}
