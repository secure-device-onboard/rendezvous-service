// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services;

import lombok.RequiredArgsConstructor;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.crypto.TO1JWTokenFactory;
import org.sdo.rendezvous.exceptions.InvalidJwtTokenException;
import org.sdo.rendezvous.model.MpConstants;
import org.sdo.rendezvous.model.types.Device;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenParserService {

  private final TO1JWTokenFactory to1JWTokenFactory;
  private final RendezvousConfig rendezvousConfig;

  /**
   * Returns the Device instance created from JWT token.
   *
   * @param jwt jwt token as a String
   * @return instance of Device
   * @throws InvalidJwtTokenException if jwt token is invalid
   */
  public Device getDeviceData(String jwt) throws InvalidJwtTokenException {
    return to1JWTokenFactory.parseToken(jwt, rendezvousConfig.getHmacSecret());
  }

  /**
   * Returns a JWT token as a String.
   *
   * @param device the instance of Device which contains nonce and guid
   * @return built a JWT token from device instance as a String
   */
  public String getToken(Device device) {
    return to1JWTokenFactory.buildToken(
        device.getGuid(), device.getNonce(), rendezvousConfig.getHmacSecret());
  }

  /**
   * Validates JWT token.
   *
   * @param jwt jwt token as a String
   * @throws InvalidJwtTokenException if jwt token invalid
   */
  public void validateTokenPrefix(String jwt) throws InvalidJwtTokenException {
    if (!jwt.startsWith(MpConstants.BEARER_PREFIX)) {
      throw new InvalidJwtTokenException();
    }
  }
}
