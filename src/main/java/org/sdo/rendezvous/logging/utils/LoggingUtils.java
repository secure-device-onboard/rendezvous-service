// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.logging.utils;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class LoggingUtils {

  public static final Marker CUSTOMER = MarkerFactory.getMarker("_CUSTOMER");
  private static final Pattern PROTOCOL_VERSION_PATTERN = Pattern.compile("\\/mp\\/([0-9]{3})\\/");

  private static final int NUMBER_OF_BYTES = 8;

  /**
   * Converts an array of bytes into a string. Only first 8 and last 8 bytes are significant.
   *
   * @param array an array of bytes
   * @return a string containing a lexical representation of xsd:hexBinary (first 8 and last 8
   *     bytes)
   */
  public static String getEdgeBytesOfArray(byte[] array) {
    if (array.length == 0) {
      return "()";
    } else if (array.length > 2 * NUMBER_OF_BYTES) {
      return String.format(
          "(%s...%s, length=%d)",
          printHexBinary(Arrays.copyOfRange(array, 0, NUMBER_OF_BYTES)),
          printHexBinary(Arrays.copyOfRange(array, array.length - NUMBER_OF_BYTES, array.length)),
          array.length);
    }

    return printHexBinary(array);
  }

  /**
   * Returns protocol version husked from url.
   *
   * @return a version of protocol as a String
   */
  public static String getProtocolVersionFromUrl() {
    HttpServletRequest request =
        (((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());
    String uri = request.getRequestURI();
    Matcher matcher = PROTOCOL_VERSION_PATTERN.matcher(uri);
    if (matcher.find()) {
      return matcher.group(1);
    }

    return uri;
  }
}
