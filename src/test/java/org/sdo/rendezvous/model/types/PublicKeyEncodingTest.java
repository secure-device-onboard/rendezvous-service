// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PublicKeyEncodingTest {

  @Test
  public void testValueOfNonEpositive() throws Exception {
    int index = 0;
    PublicKeyEncoding publicKeyEncoding = PublicKeyEncoding.valueOf(index);
    Assert.assertEquals(publicKeyEncoding, PublicKeyEncoding.NONE);
  }

  @Test
  public void testValueOfEpidpositive() throws Exception {
    int index = 4;
    PublicKeyEncoding publicKeyEncoding = PublicKeyEncoding.valueOf(index);
    Assert.assertEquals(publicKeyEncoding, PublicKeyEncoding.EPID);
  }

  @Test
  public void testValueOfOnDieEcdsapositive() throws Exception {
    int index = 5;
    PublicKeyEncoding publicKeyEncoding = PublicKeyEncoding.valueOf(index);
    Assert.assertEquals(publicKeyEncoding, PublicKeyEncoding.ONDIE_ECDSA);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testValueOfNegative() throws Exception {
    int index = -1;
    PublicKeyEncoding publicKeyEncoding = PublicKeyEncoding.valueOf(index);
  }
}
