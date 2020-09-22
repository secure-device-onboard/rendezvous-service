// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils.validators;

import javax.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.exceptions.GuidDenylistException;
import org.sdo.rendezvous.exceptions.InvalidGuidException;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
@Slf4j
@RequiredArgsConstructor
public class GuidValidator {

  private static final String GUID_DENYLIST_LABEL = "GUIDS_DENYLIST";
  private static final String GUID_DENYLIST_LOG_MSG = "GUID {} is denylist";

  private static final int VALID_GUID_SIZE = 16;

  private final JedisPool jedisPool;

  /**
   * Verifies whether the specified guid is on the denylist in the database.
   *
   * @param guidBytes the guid as an array of bytes
   * @throws GuidDenylistException if the guid is in denylist
   */
  public void verifyAgainstDenyList(byte[] guidBytes) throws GuidDenylistException {
    String guid = DatatypeConverter.printHexBinary(guidBytes).toUpperCase();
    try (Jedis jedis = jedisPool.getResource()) {

      if (jedis.hexists(GUID_DENYLIST_LABEL, guid)) {
        log.info(GUID_DENYLIST_LOG_MSG, guid);
        throw new GuidDenylistException("GUID is in denylist.");
      }
    }
  }

  /**
   * Validates the guid length.
   *
   * @param guidBytes the guid as an array of bytes
   * @throws InvalidGuidException if the guid is invalid
   */
  public void validateGuidLength(byte[] guidBytes) throws InvalidGuidException {
    if (guidBytes.length != VALID_GUID_SIZE) {
      throw new InvalidGuidException("GUID is invalid.");
    }
  }
}
