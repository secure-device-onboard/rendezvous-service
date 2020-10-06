// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.beans;

import java.util.Arrays;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AllowlistHashesTest {

  private static final byte[] HASH_1 = new byte[] {(byte) 0x01};
  private static final byte[] HASH_2 = new byte[] {(byte) 0xFF, (byte) 0xAF};
  private static final byte[] HASH_3 =
      new byte[] {(byte) 0x01, (byte) 0x03, (byte) 0xCC, (byte) 0xF1};

  private AllowlistHashes allowlistHashes;

  @BeforeMethod
  public void setUp() {
    allowlistHashes = new AllowlistHashes();
  }

  @Test
  public void testAddAndGetNotEmpty() {
    allowlistHashes.add(HASH_1);
    allowlistHashes.add(HASH_2);
    allowlistHashes.add(HASH_3);
    Assert.assertEquals(allowlistHashes.getAll().size(), 3);
    Assert.assertTrue(
        allowlistHashes.getAll().containsAll(Arrays.asList(HASH_1, HASH_2, HASH_3)));
  }

  @Test
  public void testGetEmpty() {
    Assert.assertTrue(allowlistHashes.getAll().isEmpty());
  }
}
