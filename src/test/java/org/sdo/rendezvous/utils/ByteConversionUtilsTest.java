// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ByteConversionUtilsTest {

  @Test
  public void testByteToByteArrayPositive() {
    byte oneByte = 0x11;
    byte[] expectedResult = new byte[] {oneByte};
    byte[] result = ByteConversionUtils.byteToByteArray(oneByte);
    Assert.assertEquals(result, expectedResult);
  }

  @Test
  public void testUnsignedShortToByteArrayPositive() {
    short shortValue = 13107;
    byte[] expectedResult = new byte[] {0x33, 0x33};
    byte[] result = ByteConversionUtils.unsignedShortToByteArray(shortValue);
    Assert.assertEquals(result, expectedResult);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testUnsignedShortToByteArrayMinNegativeValue() {
    short shortValue = Short.MIN_VALUE;
    ByteConversionUtils.unsignedShortToByteArray(shortValue);
  }

  @Test
  public void testUnsignedShortToByteArrayMaxPositiveValue() {
    short shortValue = Short.MAX_VALUE;
    byte[] expectedResult = DatatypeConverter.parseHexBinary("7fff");
    byte[] result = ByteConversionUtils.unsignedShortToByteArray(shortValue);
    Assert.assertEquals(result, expectedResult);
  }

  @Test
  public void testUnsignedIntegerToByteArrayPositive() {
    int intValue = 1145324612;
    byte[] expectedResult = new byte[] {0x44, 0x44, 0x44, 0x44};
    byte[] result = ByteConversionUtils.unsignedIntegerToByteArray(intValue);
    Assert.assertEquals(result, expectedResult);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testUnsignedIntegerToByteArrayMinNegativeValue() {
    int intValue = Integer.MIN_VALUE;
    ByteConversionUtils.unsignedIntegerToByteArray(intValue);
  }

  @Test
  public void testUnsignedIntegerToByteArrayMaxPositiveValue() {
    int intValue = Integer.MAX_VALUE;
    byte[] expectedResult = DatatypeConverter.parseHexBinary("7fffffff");
    byte[] result = ByteConversionUtils.unsignedIntegerToByteArray(intValue);
    Assert.assertEquals(result, expectedResult);
  }

  @Test
  public void testCreateLVsPositive() throws IOException {
    // given
    byte[] blobA = new byte[] {(byte) 0xa1, (byte) 0xa2, (byte) 0xa3};
    byte[] blobB = new byte[] {(byte) 0xb1, (byte) 0xb2, (byte) 0xb3, (byte) 0xb4, (byte) 0xb5};
    byte[] blobC = new byte[] {(byte) 0xc1, (byte) 0xc2};

    List<byte[]> sigInfoBytes = new ArrayList<>();
    sigInfoBytes.add(blobA);
    sigInfoBytes.add(blobB);
    sigInfoBytes.add(blobC);

    byte[] expectedOutput =
        new byte[] {
          (byte) 0x00,
          (byte) 0x03,
          (byte) 0xa1,
          (byte) 0xa2,
          (byte) 0xa3,
          (byte) 0x00,
          (byte) 0x05,
          (byte) 0xb1,
          (byte) 0xb2,
          (byte) 0xb3,
          (byte) 0xb4,
          (byte) 0xb5,
          (byte) 0x00,
          (byte) 0x02,
          (byte) 0xc1,
          (byte) 0xc2
        };

    // when
    byte[] output = ByteConversionUtils.createLVs(sigInfoBytes);

    // then
    Assert.assertEquals(output, expectedOutput);
  }
}
