// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import org.sdo.rendezvous.exceptions.InvalidIpAddressException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class IpAddressTest {

  @Test
  public void createInstancePositive() throws InvalidIpAddressException {
    String ipAddressString = "127.0.0.1";
    IpAddress ipAddress = new IpAddress(ipAddressString);
    Assert.assertEquals(ipAddress.getHostAddress(), ipAddressString);
  }

  @Test(expectedExceptions = InvalidIpAddressException.class)
  public void createInstanceNegative() throws InvalidIpAddressException {
    String ipAddressString = "9999.999.99.9";
    new IpAddress(ipAddressString);
  }
}
