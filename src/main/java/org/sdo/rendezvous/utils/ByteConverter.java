// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.UUID;
import org.sdo.rendezvous.exceptions.InvalidGuidException;

public class ByteConverter {

  /**
   * Converts an array of bytes to a guid as a String.
   *
   * @param guid the guid as an array of bytes
   * @return the converted guid to a String
   * @throws InvalidGuidException if an array of bytes with guid inside is invalid, it is wrapper
   *     for BufferUnderflowException
   */
  public static String getGuidFromByteArray(byte[] guid) throws InvalidGuidException {
    try {
      ByteBuffer byteBuffer = ByteBuffer.wrap(guid);
      long high = byteBuffer.getLong();
      long low = byteBuffer.getLong();
      UUID uuid = new UUID(high, low);
      return uuid.toString();
    } catch (BufferUnderflowException e) {
      throw new InvalidGuidException("GUID is invalid.");
    }
  }
}
