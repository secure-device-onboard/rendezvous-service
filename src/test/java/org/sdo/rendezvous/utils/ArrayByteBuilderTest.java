// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils;

import java.io.IOException;
import javax.xml.bind.DatatypeConverter;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

@PrepareForTest({ByteConversionUtils.class})
public class ArrayByteBuilderTest extends PowerMockTestCase {

  @Test
  public void testBuildArrayPositive() throws IOException {
    PowerMockito.mockStatic(ByteConversionUtils.class);

    byte[] byteArray = {0x11, 0x11, 0x11};
    byte oneByte = 0x22;
    short shortValue = 13107;
    int intValue = 1145324612;

    PowerMockito.when(ByteConversionUtils.byteToByteArray(oneByte))
        .thenReturn(new byte[] {oneByte});
    PowerMockito.when(ByteConversionUtils.unsignedIntegerToByteArray(intValue))
        .thenReturn(new byte[] {0x44, 0x44, 0x44, 0x44});
    PowerMockito.when(ByteConversionUtils.unsignedShortToByteArray(shortValue))
        .thenReturn(new byte[] {0x33, 0x33});

    byte[] expectedResult = DatatypeConverter.parseHexBinary("11111122333344444444");
    ArrayByteBuilder arrayByteBuilder = new ArrayByteBuilder();
    byte[] result =
        arrayByteBuilder
            .append(byteArray)
            .append(oneByte)
            .append(shortValue)
            .append(intValue)
            .build();
    Assert.assertEquals(result, expectedResult);
  }
}
