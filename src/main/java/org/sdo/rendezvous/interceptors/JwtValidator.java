// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.interceptors;

import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.sdo.rendezvous.exceptions.InvalidJwtTokenException;
import org.sdo.rendezvous.model.MpConstants;
import org.sdo.rendezvous.services.TokenParserService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class JwtValidator {

  private final TokenParserService tokenParserService;

  public String validateAndGetToken(HttpServletRequest request) throws InvalidJwtTokenException {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authHeader == null) {
      throw new InvalidJwtTokenException();
    }

    tokenParserService.validateTokenPrefix(authHeader);

    return authHeader.substring(MpConstants.BEARER_PREFIX.length());
  }
}
