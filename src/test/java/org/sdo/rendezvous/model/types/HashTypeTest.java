// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import org.testng.Assert;
import org.testng.annotations.Test;

public class HashTypeTest {

  @Test
  public void testValueOfNonePositive() throws Exception {
    int index = 0;
    HashType hashType = HashType.valueOf(index);
    Assert.assertEquals(hashType, HashType.NONE);
  }

  @Test
  public void testValueOfSha512Positive() throws Exception {
    int index = 10;
    HashType hashType = HashType.valueOf(index);
    Assert.assertEquals(hashType, HashType.SHA512);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testValueOfNegative() throws Exception {
    int index = -1;
    HashType hashType = HashType.valueOf(index);
  }
}
