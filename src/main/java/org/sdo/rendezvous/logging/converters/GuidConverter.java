// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.logging.converters;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.Optional;
import org.sdo.rendezvous.enums.AttributeName;
import org.sdo.rendezvous.utils.SessionAttributeHolder;
import org.springframework.web.context.request.RequestContextHolder;

public class GuidConverter extends ClassicConverter {

  private static final String NO_GUID = "NO GUID";

  @Override
  public String convert(ILoggingEvent event) {
    if (RequestContextHolder.getRequestAttributes() == null) {
      return NO_GUID;
    }

    return Optional.ofNullable(SessionAttributeHolder.getAttributeValue(AttributeName.GUID))
        .orElse(NO_GUID);
  }
}
