// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ArrayByteBuilder {

  private ByteArrayOutputStream output;

  public ArrayByteBuilder() {
    this.output = new ByteArrayOutputStream();
  }

  /**
   * Append input.length bytes from the specified byte array to the output stream.
   *
   * @param input the data
   * @return a reference to this object
   * @throws IOException if an I/O error occurs
   */
  public ArrayByteBuilder append(byte[] input) throws IOException {
    output.write(input);
    return this;
  }

  /**
   * Append the specified byte to the output stream.
   *
   * @param input the data
   * @return a reference to this object
   * @throws IOException if an I/O error occurs
   */
  public ArrayByteBuilder append(byte input) throws IOException {
    output.write(ByteConversionUtils.byteToByteArray(input));
    return this;
  }

  /**
   * Append the specified short as a byte to the output stream.
   *
   * @param input the data
   * @return a reference to this object
   * @throws IOException if an I/O error occurs
   */
  public ArrayByteBuilder append(short input) throws IOException {
    output.write(ByteConversionUtils.unsignedShortToByteArray(input));
    return this;
  }

  /**
   * Append the specified int as a byte to the output stream.
   *
   * @param input the data
   * @return a reference to this object
   * @throws IOException if an I/O error occurs
   */
  public ArrayByteBuilder append(int input) throws IOException {
    output.write(ByteConversionUtils.unsignedIntegerToByteArray(input));
    return this;
  }

  /**
   * Assigns the specified byte value to each element of a created array of bytes with the specified
   * size num.
   *
   * @param input the byte value
   * @param num the size of new array of bytes
   * @return a reference to this object
   * @throws IOException if an I/O error occurs
   */
  public ArrayByteBuilder append(byte input, int num) throws IOException {
    byte[] array = new byte[num];
    Arrays.fill(array, input);
    return this.append(array);
  }

  public byte[] build() {
    return output.toByteArray();
  }
}
