// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.logging.converters;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.Optional;
import org.sdo.rendezvous.enums.AttributeName;
import org.sdo.rendezvous.utils.SessionAttributeHolder;
import org.springframework.web.context.request.RequestContextHolder;

public class SessionIdConverter extends ClassicConverter {

  private static final String NO_SESSION_ID = "NO SESSION ID";

  @Override
  public String convert(ILoggingEvent event) {
    if (RequestContextHolder.getRequestAttributes() == null) {
      return NO_SESSION_ID;
    }

    return Optional.ofNullable(SessionAttributeHolder.getAttributeValue(AttributeName.SESSION_ID))
        .orElse(NO_SESSION_ID);
  }
}
