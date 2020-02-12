// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model;

import org.testng.Assert;
import org.testng.annotations.Test;

public class MessageTypeNamesTest {
  private static final int TO1_HELLOSDO_ID = 30;
  private static final String TO1_HELLO_SDO_NAME = "TO1.HelloSDO";
  private static final int NEGATIVE_ID = -1;
  private static final int UNKNOWN_ID = 123;
  private static final String UNKNOWN_EMSG_ID = "Unknown Message ID";

  @Test
  public void testGetEmsgNameByIdPositive() {
    Assert.assertEquals(MessageTypeNames.getEmsgNameById(TO1_HELLOSDO_ID), TO1_HELLO_SDO_NAME);
  }

  @Test
  public void testGetEmsgNameByIdPositiveWithNegativeId() {
    Assert.assertEquals(MessageTypeNames.getEmsgNameById(NEGATIVE_ID), UNKNOWN_EMSG_ID);
  }

  @Test
  public void testGetEmsgNameByIdPositiveWithUnknownId() {
    Assert.assertEquals(MessageTypeNames.getEmsgNameById(UNKNOWN_ID), UNKNOWN_EMSG_ID);
  }
}
