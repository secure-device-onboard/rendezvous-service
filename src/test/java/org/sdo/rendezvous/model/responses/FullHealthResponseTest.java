// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.responses;

import org.sdo.rendezvous.model.types.ComponentStatus;
import org.sdo.rendezvous.model.types.ServiceStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FullHealthResponseTest {

  private static final String RV_VERSION = "1.5.333";
  private static final String VERIFICATION_SERVICE_VERSION = "1.0.123";
  private static final ServiceStatus RV_SERVICE_OK =
      new ServiceStatus(ComponentStatus.Status.OK, RV_VERSION);
  private static final ServiceStatus VERIFICATION_SERVICE_OK =
      new ServiceStatus(ComponentStatus.Status.OK, VERIFICATION_SERVICE_VERSION);
  private static final ServiceStatus VERIFICATION_SERVICE_ERROR =
      new ServiceStatus(ComponentStatus.Status.ERROR, VERIFICATION_SERVICE_VERSION);
  private static final ComponentStatus DATABASE_OK = new ComponentStatus(ComponentStatus.Status.OK);

  @Test
  public void testFullHealthResponse_ShouldSetSdoStatusToOkWhenAllComponentsAreOk() {
    FullHealthResponse fullHealthResponse =
        new FullHealthResponse(RV_SERVICE_OK, VERIFICATION_SERVICE_OK, DATABASE_OK);

    Assert.assertEquals(fullHealthResponse.getSdoStatus(), ComponentStatus.Status.OK);
  }

  @Test
  public void testFullHealthResponse_ShouldSetSdoStatusToErrorWhenAtLeastOneComponentIsError() {
    FullHealthResponse fullHealthResponse =
        new FullHealthResponse(RV_SERVICE_OK, VERIFICATION_SERVICE_ERROR, DATABASE_OK);

    Assert.assertEquals(fullHealthResponse.getSdoStatus(), ComponentStatus.Status.ERROR);
  }
}
