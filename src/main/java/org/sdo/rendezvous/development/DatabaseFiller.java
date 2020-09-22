// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.development;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

@Slf4j
@Service
@Profile("development")
public class DatabaseFiller {

  private static final String ALLOWLIST_HASHSET_NAME = "OP_KEYS_ALLOWLIST";
  private static final String DENYLIST_HASHSET_NAME = "OP_KEYS_DENYLIST";
  private static final String GUID_DENYLIST_NAME = "GUIDS_DENYLIST";

  // this key hash has been calculated from public key of
  // /Validation/TestData/OwnershipVoucher/p1.cert.pem
  // instruction how it was calculated can be found
  // /Validation/TestData/OwnershipVoucher/openssl_instructions.md
  // raw data for ecdsa keys are available in file TO0TestData.scala
  private static final String[] ALLOWLIST_KEYS_HASHES_ARRAY = {
    "0734FAC43DBE455D531930B6A8E024043356541BFFCC7A250E417EC38E217725", // rsa pubkey 1
    "3055924C4AF1A77FD365C380F9B3CFC40C5F8C79B1EC6492F0D15648E9792CA2", // rsa pubkey 7
    "8EA241130B68234932411A2B9615B939C70619E4169AFB02D913729456A15C94", // ecdsa384
    "0F600E1E444664FFDEA57B3A83D0CFEB29B406B898142404725874A0CB419AE1" // ecdsa256
  };

  // this key hash has been calculated from public key of
  // /Validation/TestData/OwnershipVoucher/p6_DENYLIST.cert.pem
  // instruction how it was calculated can be found
  // /Validation/TestData/OwnershipVoucher/openssl_instructions.md
  private static final String[] DENYLIST_KEYS_HASHES_ARRAY = {
    "C449FF026A15C42090A90A4E8CAEB64AAA9B5CA0FC5C1747F4E8045678968969",
    "7BFB39F80CDD1BDC12B06A8D2DB94AD91702FA2E8626E740B0B127B0708618E3" // ecdsa256
  };

  private static final String[] DENYLIST_GUIDS_ARRAY = {"0000000000000000000000000000FFFF"};

  private static final List<String> ALLOWLIST_KEYS_HASHES =
      new ArrayList<>(Arrays.asList(ALLOWLIST_KEYS_HASHES_ARRAY));
  private static final List<String> DENYLIST_KEYS_HASHES =
      new ArrayList<>(Arrays.asList(DENYLIST_KEYS_HASHES_ARRAY));
  private static final List<String> DENYLIST_GUIDS =
      new ArrayList<>(Arrays.asList(DENYLIST_GUIDS_ARRAY));

  private final JedisPool jedisPool;

  @Autowired
  public DatabaseFiller(JedisPool jedisPool) {
    this.jedisPool = jedisPool;
  }

  @PostConstruct
  private void fill() throws IOException {
    assignHashesToKey(ALLOWLIST_HASHSET_NAME, ALLOWLIST_KEYS_HASHES);
    assignHashesToKey(DENYLIST_HASHSET_NAME, DENYLIST_KEYS_HASHES);
    assignHashesToKey(GUID_DENYLIST_NAME, DENYLIST_GUIDS);
  }

  private void assignHashesToKey(String keyName, List<String> values) {
    try (Jedis jedis = jedisPool.getResource()) {

      jedis.del(keyName);

      for (String hash : values) {
        jedis.hset(keyName, hash, "1");
      }
    } catch (JedisConnectionException e) {
      throw new RuntimeException(
          String.format("'DatabaseFiller' Internal error: %s", e.getMessage()));
    }
  }
}
