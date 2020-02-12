// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RandomUtilsTest {

  @Test
  public void testGenerateRandomCharsPositive() {
    int correctSize = 10;
    String randomSequence = RandomUtils.generateRandomChars(correctSize);
    String pattern = "^([a-z]{" + correctSize + "})$";
    Assert.assertTrue(randomSequence.matches(pattern));
    Assert.assertEquals(randomSequence.length(), correctSize);
  }

  @Test
  public void testGenerateRandomCharsZeroSize() {
    int zeroSize = 0;
    String randomSequence = RandomUtils.generateRandomChars(zeroSize);
    Assert.assertEquals(randomSequence.length(), zeroSize);
  }

  @Test
  public void testGenerateRandomCharsNegativeSize() {
    int negativeSize = -1;
    String randomSequence = RandomUtils.generateRandomChars(negativeSize);
    Assert.assertNull(randomSequence);
  }
}
