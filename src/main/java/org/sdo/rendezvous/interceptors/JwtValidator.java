// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.interceptors;

import io.jsonwebtoken.MalformedJwtException;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.exceptions.InvalidJwtTokenException;
import org.sdo.rendezvous.model.MpConstants;
import org.sdo.rendezvous.services.TokenParserService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class JwtValidator {

  private final TokenParserService tokenParserService;
  private final RendezvousConfig rendezvousConfig;

  public String validateAndGetToken(HttpServletRequest request) throws InvalidJwtTokenException {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authHeader == null) {
      throw new InvalidJwtTokenException();
    }

    // Decode jwt payload to extract the expiry timestamp
    Base64.Decoder decoder = Base64.getDecoder();
    String[] jwtElements = authHeader.split("\\.");
    String payload = new String(decoder.decode(jwtElements[1]));
    String expTime = payload.substring(payload.lastIndexOf(":") + 1, payload.length() - 1);
    Date time = new Date(Long.parseLong(expTime) * 1000);

    // Validate the timestamp is not older than TO Token expiration time
    int diffMinutes =
        (int) TimeUnit.MILLISECONDS.toMinutes(time.getTime() - System.currentTimeMillis());

    if (diffMinutes
        > TimeUnit.MILLISECONDS.toMinutes(rendezvousConfig.getToTokenExpirationTime())) {
      throw new MalformedJwtException(
          "Expiry time set of the JWT is more than TO Token expiration time");
    }

    tokenParserService.validateTokenPrefix(authHeader);

    return authHeader.substring(MpConstants.BEARER_PREFIX.length());
  }
}
