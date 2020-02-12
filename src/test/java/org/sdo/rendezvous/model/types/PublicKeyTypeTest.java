// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PublicKeyTypeTest {

  @Test
  public void testValueOfNonePositive() throws Exception {
    int index = 0;
    PublicKeyType publicKeyType = PublicKeyType.valueOf(index);
    Assert.assertEquals(publicKeyType, PublicKeyType.NONE);
  }

  @Test
  public void testValueOfEpid11Positive() throws Exception {
    int index = 91;
    PublicKeyType publicKeyType = PublicKeyType.valueOf(index);
    Assert.assertEquals(publicKeyType, PublicKeyType.EPID_1_1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testValueOfNegative() throws Exception {
    int index = -1;
    PublicKeyType publicKeyType = PublicKeyType.valueOf(index);
  }
}
