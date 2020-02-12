// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils;

import java.util.Random;

public class RandomUtils {

  private static final int ALPHABET_SIZE = 'z' - 'a' + 1;

  /**
   * Generates the random characters.
   *
   * @param length the number of characters in the generated sequence
   * @return generated sequence of characters as a String
   */
  public static String generateRandomChars(int length) {
    if (length < 0) {
      return null;
    }
    Random generator = new Random();
    StringBuilder randomSequence = new StringBuilder();
    for (int i = 0; i < length; i++) {
      randomSequence.append((char) (generator.nextInt(ALPHABET_SIZE) + 'a'));
    }
    return randomSequence.toString();
  }
}
