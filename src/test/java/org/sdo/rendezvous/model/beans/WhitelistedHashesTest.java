// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.beans;

import java.util.Arrays;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class WhitelistedHashesTest {

  private static final byte[] HASH_1 = new byte[] {(byte) 0x01};
  private static final byte[] HASH_2 = new byte[] {(byte) 0xFF, (byte) 0xAF};
  private static final byte[] HASH_3 =
      new byte[] {(byte) 0x01, (byte) 0x03, (byte) 0xCC, (byte) 0xF1};

  private WhitelistedHashes whitelistedHashes;

  @BeforeMethod
  public void setUp() {
    whitelistedHashes = new WhitelistedHashes();
  }

  @Test
  public void testAddAndGetNotEmpty() {
    whitelistedHashes.add(HASH_1);
    whitelistedHashes.add(HASH_2);
    whitelistedHashes.add(HASH_3);
    Assert.assertEquals(whitelistedHashes.getAll().size(), 3);
    Assert.assertTrue(
        whitelistedHashes.getAll().containsAll(Arrays.asList(HASH_1, HASH_2, HASH_3)));
  }

  @Test
  public void testGetEmpty() {
    Assert.assertTrue(whitelistedHashes.getAll().isEmpty());
  }
}
