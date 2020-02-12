// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ByteConversionUtils {

  private static final int INTEGER_SIZE = 4;
  private static final int SHORT_SIZE = 2;

  /**
   * Converts unsigned int value to an array of bytes of a length 4.
   *
   * @param unsignedInteger the value to be converted
   * @return the new created an array of bytes filled with unsignedInteger
   */
  public static byte[] unsignedIntegerToByteArray(int unsignedInteger) {
    if (unsignedInteger < 0) {
      throw new IllegalArgumentException(
          "Unsigned integer underflow! Value: " + unsignedInteger + " is negative.");
    }
    return ByteBuffer.allocate(INTEGER_SIZE)
        .order(ByteOrder.BIG_ENDIAN)
        .putInt(unsignedInteger)
        .array();
  }

  /**
   * Converts usigned short value to an array of bytes of a length 2.
   *
   * @param unsignedShort the value to be converted
   * @return the new created an array of bytes filled with unsignedShort
   */
  public static byte[] unsignedShortToByteArray(short unsignedShort) {
    if (unsignedShort < 0) {
      throw new IllegalArgumentException(
          "Unsigned short underflow! Value: " + unsignedShort + " is negative.");
    }
    return ByteBuffer.allocate(SHORT_SIZE)
        .order(ByteOrder.BIG_ENDIAN)
        .putShort(unsignedShort)
        .array();
  }

  /**
   * Converts single byte to an array of bytes.
   *
   * @param myByte the value to be converted
   * @return the new created an array of bytes
   */
  public static byte[] byteToByteArray(byte myByte) {
    return new byte[] {myByte};
  }

  /**
   * Converts arrays of bytes to a single array of bytes.
   *
   * @param data the list of an array of bytes
   * @return the new created an array of bytes
   * @throws IOException if an I/O error occurs
   */
  public static byte[] createLVs(List<byte[]> data) throws IOException {
    ArrayByteBuilder builder = new ArrayByteBuilder();

    for (byte[] blob : data) {
      builder.append((short) blob.length).append(blob);
    }

    return builder.build();
  }
}
