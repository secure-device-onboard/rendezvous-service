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
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.exceptions.InvalidJwtTokenException;
import org.sdo.rendezvous.model.types.Device;
import org.sdo.rendezvous.utils.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** The factory for TO0 JWToken. */
@Slf4j
@Component
public class To1JwTokenFactory {

  private static final String GUID_KEY = "g2";
  private static final String NONCE_KEY = "n4";

  @Autowired private RendezvousConfig rendezvousConfig;

  /**
   * Builds the JWT and serializes it to a compact.
   *
   * @param guid the value to set for the specified Claims (as a key "g2")
   * @param nonce the value to set for the specified Claims (as a key "n4")
   * @param key the algorithm-specific signing key to use to digitally sign the JWT
   * @return a JWT string
   */
  public String buildToken(byte[] guid, byte[] nonce, String key) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(GUID_KEY, guid);
    claims.put(NONCE_KEY, nonce);

    return Jwts.builder()
        .setClaims(claims)
        .setExpiration(
            new Date(TimestampUtils.getCurrent() + rendezvousConfig.getToTokenExpirationTime()))
        .signWith(SignatureAlgorithm.HS256, key)
        .compact();
  }

  /**
   * Returns the instance of Device.
   *
   * @param jwt the compact serialized JWT to parse
   * @param key the algorithm-specific signing key to use to digitally sign the JWT
   * @return the instance of Device
   * @throws InvalidJwtTokenException if jwt is not valid
   */
  public Device parseToken(String jwt, String key) throws InvalidJwtTokenException {
    try {
      Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody();
      Device device = new Device();
      device.setGuid(Base64.getDecoder().decode((String) claims.get(GUID_KEY)));
      device.setNonce(Base64.getDecoder().decode((String) claims.get(NONCE_KEY)));
      return device;
    } catch (ExpiredJwtException e) {
      log.debug("JWT has expired. Error: {}", e.getMessage());
      throw new InvalidJwtTokenException(InvalidJwtTokenException.JWT_EXPIRED);
    } catch (JwtException | NullPointerException e) {
      log.debug("JWT is invalid. Error: {}", e.getMessage());
      throw new InvalidJwtTokenException(InvalidJwtTokenException.JWT_INVALID);
    }
  }
}
