// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.crypto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.exceptions.InvalidJwtTokenException;
import org.sdo.rendezvous.utils.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** The factory for TO0 JWToken. */
@Component
@NoArgsConstructor
public class TO0JWTokenFactory {

  private static final String NONCE_KEY = "n3";

  @Autowired private RendezvousConfig rendezvousConfig;

  /**
   * Builds the JWT and serializes it to a compact.
   *
   * @param nonce the value to set for the specified Claims (as a key "n3")
   * @param key the algorithm-specific signing key to use to digitally sign the JWT
   * @return a JWT string
   */
  public String buildToken(byte[] nonce, String key) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(NONCE_KEY, nonce);

    return Jwts.builder()
        .setClaims(claims)
        .setExpiration(
            new Date(TimestampUtils.getCurrent() + rendezvousConfig.getToTokenExpirationTime()))
        .signWith(SignatureAlgorithm.HS256, key)
        .compact();
  }

  /**
   * Returns nonce retrieved from JWT.
   *
   * @param jwt the compact serialized JWT to parse
   * @param key the algorithm-specific signing key to use to digitally sign the JWT
   * @return nonce as an array of bytes
   * @throws InvalidJwtTokenException if jwt is not valid
   */
  public byte[] getNonceFromToken(String jwt, String key) throws InvalidJwtTokenException {
    try {
      Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody();
      return Base64.getDecoder().decode((String) claims.get(NONCE_KEY));
    } catch (ExpiredJwtException e) {
      throw new InvalidJwtTokenException(InvalidJwtTokenException.JWT_EXPIRED);
    } catch (JwtException | NullPointerException e) {
      throw new InvalidJwtTokenException(InvalidJwtTokenException.JWT_INVALID);
    }
  }
}
