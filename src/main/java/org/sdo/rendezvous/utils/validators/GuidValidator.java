// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils.validators;

import javax.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.exceptions.GuidBlacklistedException;
import org.sdo.rendezvous.exceptions.InvalidGuidException;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
@Slf4j
@RequiredArgsConstructor
public class GuidValidator {

  private static final String GUID_BLACKLIST_LABEL = "GUIDS_BLACKLIST";
  private static final String GUID_BLACKLISTED_LOG_MSG = "GUID {} is blacklisted";

  private static final int VALID_GUID_SIZE = 16;

  private final JedisPool jedisPool;

  /**
   * Verifies whether the specified guid is on the blacklist in the database.
   *
   * @param guidBytes the guid as an array of bytes
   * @throws GuidBlacklistedException if the guid is blacklisted
   */
  public void verifyAgainstBlackList(byte[] guidBytes) throws GuidBlacklistedException {
    String guid = DatatypeConverter.printHexBinary(guidBytes).toUpperCase();
    try (Jedis jedis = jedisPool.getResource()) {

      if (jedis.hexists(GUID_BLACKLIST_LABEL, guid)) {
        log.info(GUID_BLACKLISTED_LOG_MSG, guid);
        throw new GuidBlacklistedException("GUID is blacklisted.");
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
