// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.sdo.rendezvous.model.log.to0.TO0TransactionInfo;
import org.sdo.rendezvous.utils.JsonUtils;
import org.sdo.rendezvous.utils.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Repository
public class TransferOwnership0Repository {

  @Autowired private JedisPool jedisPool;

  private static final String HASH_NAME = "TO0";
  private static final int RANDOM_STRING_SIZE = 10;

  /**
   * Stores in the database the transaction info for TO1 protocol - logging purpose.
   *
   * @param transactionInfo the to0 transaction info
   * @throws JsonProcessingException if a JSON processing problem occurs
   */
  public void setTransactionInfo(TO0TransactionInfo transactionInfo)
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
