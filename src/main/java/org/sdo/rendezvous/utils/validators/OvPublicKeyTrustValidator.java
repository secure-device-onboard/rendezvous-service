// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils.validators;

import javax.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.exceptions.InvalidOwnershipVoucherException;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
@RequiredArgsConstructor
@Slf4j
public class OvPublicKeyTrustValidator {

  private static final String WHITELIST_HASHSET_NAME = "OP_KEYS_WHITELIST";
  private static final String BLACKLIST_HASHSET_NAME = "OP_KEYS_BLACKLIST";

  private final JedisPool jedisPool;

  /**
   * Verifies whether the specified keyHash from Ownership Voucher is not on the blacklist or
   * whitelist.
   *
   * @param keyHash the key hash
   * @return true if key hash is on the whitelist
   * @throws InvalidOwnershipVoucherException if the key hash is on the blacklist
   */
  public boolean verify(byte[] keyHash) throws InvalidOwnershipVoucherException {

    String hexHash = DatatypeConverter.printHexBinary(keyHash);
    try (Jedis jedis = jedisPool.getResource()) {

      if (jedis.hexists(BLACKLIST_HASHSET_NAME, hexHash)) {
        log.info("Key hash {} from Ownership Voucher is blacklisted.", hexHash);
        throw new InvalidOwnershipVoucherException(
            "One of keys in Ownership voucher is blacklisted.");
      }

      boolean result = jedis.hexists(WHITELIST_HASHSET_NAME, hexHash);

      if (result) {
        log.info("Hash {} found on the whitelist", hexHash);
      } else {
        log.info("Hash {} not found on the whitelist.", hexHash);
      }

      return result;
    }
  }
}
