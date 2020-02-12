// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.enums.AttributeName;
import org.sdo.rendezvous.exceptions.InvalidJwtTokenException;
import org.sdo.rendezvous.logging.utils.LoggingUtils;
import org.sdo.rendezvous.model.SdoURLMapping;
import org.sdo.rendezvous.model.beans.DeviceInfo;
import org.sdo.rendezvous.model.types.Device;
import org.sdo.rendezvous.services.TokenParserService;
import org.sdo.rendezvous.utils.SessionAttributeHolder;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
@RequiredArgsConstructor
@Slf4j
public class TO1JwtParsingInterceptor extends HandlerInterceptorAdapter {

  private final DeviceInfo deviceInfo;
  private final JwtValidator jwtValidator;
  private final TokenParserService tokenParserService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws InvalidJwtTokenException {

    try {
      String jwt = jwtValidator.validateAndGetToken(request);

      Device device = tokenParserService.getDeviceData(jwt);
      deviceInfo.setGuid(device.getGuid());
      deviceInfo.setNonce(device.getNonce());

      SessionAttributeHolder.setAttributeValue(AttributeName.SESSION_ID, device.getNonce());

    } catch (InvalidJwtTokenException e) {
      logCustomerInfo(request, e);
      throw e;
    }
    return true;
  }

  private void logCustomerInfo(HttpServletRequest request, InvalidJwtTokenException e) {

    final String endpointName =
        request.getRequestURI().contains(SdoURLMapping.ERROR_ENDPOINT) ? "Error" : "TO1.ProveToSDO";
    final String jwtErrorReason =
        e.getMessage().equals(InvalidJwtTokenException.DEFAULT_MSG) ? "missing" : "invalid";

    log.info(
        LoggingUtils.CUSTOMER,
        "{} request received. Protocol version: {}. Request data: " + "INVALID ({} JWT token).",
        endpointName,
        LoggingUtils.getProtocolVersionFromUrl(),
        jwtErrorReason);
    log.info(
        LoggingUtils.CUSTOMER,
        "{} request processing finished. Sending error response with "
            + "HTTP status code: {}. Response data: {}.",
        endpointName,
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
