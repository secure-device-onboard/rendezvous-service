// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.enums;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ErrorCodesTest {
  private static final short IP_ADDRESS_ID = 4;
  private static final short UNKNOWN_ID = 222;
  private static final short NEGATIVE_ID = -1;
  private static final String UNKNOWN_ERROR_MESSAGE = "Unknown error code";

  @Test
  public void testGetDescriptionByIdPositive() {
    Assert.assertEquals(
        ErrorCodes.getDescriptionById(IP_ADDRESS_ID), ErrorCodes.INVALID_IP_ADDRESS.name());
  }

  @Test
  public void testGetDescriptionByIdPositiveWithUnknownId() {
    Assert.assertEquals(ErrorCodes.getDescriptionById(UNKNOWN_ID), UNKNOWN_ERROR_MESSAGE);
  }

  @Test
  public void testGetDescriptionByIdPositiveWithNegativeId() {
    Assert.assertEquals(ErrorCodes.getDescriptionById(NEGATIVE_ID), UNKNOWN_ERROR_MESSAGE);
  }
}
