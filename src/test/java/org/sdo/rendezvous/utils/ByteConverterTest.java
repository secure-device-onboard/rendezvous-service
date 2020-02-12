// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils;

import javax.xml.bind.DatatypeConverter;
import org.sdo.rendezvous.exceptions.InvalidGuidException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ByteConverterTest {

  @Test
  public void testGetGuidFromByteArrayPositive() throws InvalidGuidException {
    byte[] guid = DatatypeConverter.parseHexBinary("21EC20203AEA4069A2DD08002B30309D");
    Assert.assertEquals(
        "21ec2020-3aea-4069-a2dd-08002b30309d", ByteConverter.getGuidFromByteArray(guid));
  }

  @Test(expectedExceptions = InvalidGuidException.class)
  public void testGetGuidFromByteArrayTooShortArray() throws InvalidGuidException {
    byte[] guid = DatatypeConverter.parseHexBinary("21EC2020");
    ByteConverter.getGuidFromByteArray(guid);
  }

  @Test
  public void testGetGuidFromByteArrayTooLongArray() throws InvalidGuidException {
    byte[] guid = DatatypeConverter.parseHexBinary("21EC20203AEA4069A2DD08002B30309D00000000");
    Assert.assertEquals(
        "21ec2020-3aea-4069-a2dd-08002b30309d", ByteConverter.getGuidFromByteArray(guid));
  }
}
