// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services;

import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sdo.rendezvous.enums.DeviceAttestationMethod;
import org.sdo.rendezvous.exceptions.InvalidAttestationMethodException;
import org.sdo.rendezvous.model.types.OwnershipVoucher;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DeviceAttestationServiceTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private OwnershipVoucher ownershipVoucher;

  private DeviceAttestationService deviceAttestationService = new DeviceAttestationService();

  @BeforeMethod
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testDeviceAttestationCheckerPositiveEpid() throws Exception {
    Mockito.when(ownershipVoucher.getCertificateChain()).thenReturn(null);
    Mockito.when(ownershipVoucher.getOwnershipVoucherHeader().getDeviceCertChainHash())
        .thenReturn(null);
    DeviceAttestationMethod method =
        deviceAttestationService.getDeviceAttestationMethod(ownershipVoucher);
    Assert.assertEquals(method, DeviceAttestationMethod.EPID);
  }

  @Test
  public void testDeviceAttestationCheckerPositiveEcdsa() throws Exception {
    DeviceAttestationMethod method =
        deviceAttestationService.getDeviceAttestationMethod(ownershipVoucher);
    Assert.assertEquals(method, DeviceAttestationMethod.ECDSA);
  }

  @Test(expectedExceptions = InvalidAttestationMethodException.class)
  public void testDeviceAttestationCheckerInvalidInput() throws Exception {
    Mockito.when(ownershipVoucher.getOwnershipVoucherHeader().getDeviceCertChainHash())
        .thenReturn(null);
    deviceAttestationService.getDeviceAttestationMethod(ownershipVoucher);
  }
}
