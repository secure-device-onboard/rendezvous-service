// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.interceptors;

import java.security.SecureRandom;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.sdo.rendezvous.enums.AttributeName;
import org.sdo.rendezvous.model.responses.to0.HelloAckResponse;
import org.sdo.rendezvous.utils.SessionAttributeHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class SessionIdGeneratingInterceptor extends HandlerInterceptorAdapter {

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    SecureRandom secureRandom = new SecureRandom();
    byte[] nonce = new byte[HelloAckResponse.NONCE_SIZE];
    secureRandom.nextBytes(nonce);

    SessionAttributeHolder.setAttributeValue(AttributeName.SESSION_ID, nonce);

    return true;
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
