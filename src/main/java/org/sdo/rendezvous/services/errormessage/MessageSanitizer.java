// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services.errormessage;

import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class MessageSanitizer {
  private static final String OFFENDED_MESSAGE_ALPHABET = "[^a-zA-Z0-9$_.+!*(), -]";
  private static final String REPLACEMENT_CHARACTER = "#";

  /**
   * Sanitizes the error message.
   *
   * @param message the original message which will be sanitized
   * @return the sanitized message as a String
   */
  public Optional<String> sanitizeMessage(String message) {
    String sanitized = message.replaceAll(OFFENDED_MESSAGE_ALPHABET, REPLACEMENT_CHARACTER);
    if (sanitized.equals(message)) {
      return Optional.empty();
    } else {
      return Optional.of(sanitized);
    }
  }
}
