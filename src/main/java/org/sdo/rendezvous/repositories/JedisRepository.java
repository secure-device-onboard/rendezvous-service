// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.exceptions.ResourceNotFoundException;
import org.sdo.rendezvous.model.database.VersionedTO1Data;
import org.sdo.rendezvous.model.log.to1.TO1TransactionInfo;
import org.sdo.rendezvous.utils.JsonUtils;
import org.sdo.rendezvous.utils.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Repository
@Slf4j
public class JedisRepository {

  private static final String HASH_NAME = "TO1";
  private static final int RANDOM_STRING_SIZE = 10;

  @Autowired private JedisPool jedisPool;

  /**
   * Stores the serialized to1data in the database.
   *
   * @param guid the guid used as a key in the database
   * @param to1Data the to1data json data used as a value in the database
   * @param expirationTime the expiration time in seconds
   */
  public void setVersionedTO1Data(String guid, String to1Data, int expirationTime) {
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.setex(guid, expirationTime, to1Data);
    }
  }

  /**
   * Returns the deserialized VersionedTO1Data object pulled out from the database.
   *
   * @param guid the guid used as a key in the database
   * @return return instance of VersionedTO1Data retrived from database
   * @throws ResourceNotFoundException if the record for specified guid doesn't exist in the
   *     database
   * @throws IOException if a low-level I/O problem occurs
   */
  public VersionedTO1Data getVersionedTO1Data(String guid)
      throws ResourceNotFoundException, IOException {
    try (Jedis jedis = jedisPool.getResource()) {
      String versionedTO1WithEcdsaSerialized = jedis.get(guid);
      if (versionedTO1WithEcdsaSerialized == null) {
        throw new ResourceNotFoundException(
            "Not found owner connection info for GUID: " + guid + ".");
      }
      // Deserialization
      return JsonUtils.mapJsonToObject(versionedTO1WithEcdsaSerialized, VersionedTO1Data.class);
    }
  }

  /**
   * Deletes the record from database specified by guid.
   *
   * @param guid the key in the database
   */
  public void deleteVersionedTO1Data(String guid) {
    try (Jedis jedis = jedisPool.getResource()) {
      String versionedTO1WithEcdsaSerialized = jedis.get(guid);
      if (versionedTO1WithEcdsaSerialized != null) {
        jedis.del(guid);
      }
    }
  }

  /**
   * Stores in the database the transaction info for TO1 protocol - logging purpose.
   *
   * @param transactionInfo the to1 transaction info
   * @throws JsonProcessingException if a JSON processing problem occurs
   */
  public void setTransactionInfo(TO1TransactionInfo transactionInfo)
      throws JsonProcessingException {
    try (Jedis jedis = jedisPool.getResource()) {
      String transactionKey =
          transactionInfo.getCurrentTimestamp()
              + ":"
              + RandomUtils.generateRandomChars(RANDOM_STRING_SIZE);
      jedis.hset(HASH_NAME, transactionKey, JsonUtils.mapObjectToJson(transactionInfo));
    }
  }
}
