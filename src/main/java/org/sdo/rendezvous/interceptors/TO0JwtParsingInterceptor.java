// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.crypto.TO0JWTokenFactory;
import org.sdo.rendezvous.enums.AttributeName;
import org.sdo.rendezvous.exceptions.InvalidJwtTokenException;
import org.sdo.rendezvous.logging.utils.LoggingUtils;
import org.sdo.rendezvous.model.beans.Nonce;
import org.sdo.rendezvous.utils.SessionAttributeHolder;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
@RequiredArgsConstructor
@Slf4j
public class TO0JwtParsingInterceptor extends HandlerInterceptorAdapter {

  private final Nonce nonce;
  private final JwtValidator jwtValidator;
  private final TO0JWTokenFactory to0JWTokenFactory;
  private final RendezvousConfig rendezvousConfig;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws InvalidJwtTokenException {

    try {
      String jwt = jwtValidator.validateAndGetToken(request);

      nonce.setValue(to0JWTokenFactory.getNonceFromToken(jwt, rendezvousConfig.getHmacSecret()));

      SessionAttributeHolder.setAttributeValue(AttributeName.SESSION_ID, nonce.getValue());

    } catch (InvalidJwtTokenException e) {
      logCustomerInfo(response, e);
      throw e;
    }

    return true;
  }

  private void logCustomerInfo(HttpServletResponse response, InvalidJwtTokenException e) {

    final String jwtErrorReason =
        e.getMessage().equals(InvalidJwtTokenException.DEFAULT_MSG) ? "missing" : "invalid";

    log.info(
        LoggingUtils.CUSTOMER,
        "TO0.OwnerSign request received. Protocol version:  {}. "
            + "Request data: INVALID ({} JWT token).",
        LoggingUtils.getProtocolVersionFromUrl(),
        jwtErrorReason);
    log.info(
        LoggingUtils.CUSTOMER,
        "TO0.OwnerSign request processing finished. Sending error response with "
            + "HTTP status code: {}. Response data: {}.",
        HttpStatus.BAD_REQUEST.value(),
        e.toString());
  }

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      @Nullable ModelAndView modelAndView) {
    SessionAttributeHolder.removeAttribute(AttributeName.SESSION_ID);
  }
}
