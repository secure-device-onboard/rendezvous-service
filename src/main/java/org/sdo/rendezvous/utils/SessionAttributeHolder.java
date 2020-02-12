// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils;

import java.util.Objects;
import javax.xml.bind.DatatypeConverter;
import org.sdo.rendezvous.enums.AttributeName;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class SessionAttributeHolder {

  /**
   * Put a context value as identified with the key parameter into the current thread's context map.
   *
   * @param attributeName the key parameter
   * @param attributeValue the value
   */
  public static void setAttributeValue(AttributeName attributeName, byte[] attributeValue) {
    String value = DatatypeConverter.printHexBinary(attributeValue);
    // left for tests
    Objects.requireNonNull(RequestContextHolder.getRequestAttributes())
        .setAttribute(attributeName.name(), value, RequestAttributes.SCOPE_REQUEST);
    MDC.put(attributeName.name(), value);
  }

  /**
   * Gets the context as a String identified by the key parameter.
   *
   * @param attributeName the key parameter
   * @return the context as a String
   */
  public static String getAttributeValue(AttributeName attributeName) {
    // return MDC.get(attributeName.name());
    // left for tests
    return (String)
        Objects.requireNonNull(RequestContextHolder.getRequestAttributes())
            .getAttribute(attributeName.name(), RequestAttributes.SCOPE_REQUEST);
  }

  /**
   * Removes the context identified by the key parameter.
   *
   * @param attributeName the key parameter
   */
  public static void removeAttribute(AttributeName attributeName) {
    MDC.remove(attributeName.name());
  }
}
