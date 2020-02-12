// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.repositories;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.sdo.rendezvous.model.log.to0.TO0TransactionInfo;
import org.sdo.rendezvous.utils.JsonUtils;
import org.sdo.rendezvous.utils.RandomUtils;
import org.testng.annotations.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

@PrepareForTest({JsonUtils.class, RandomUtils.class})
public class TransferOwnership0RepositoryTest extends PowerMockTestCase {

  private static final String MOCKED_TIMESTAMP = "Tue May 09 10:33:39 CEST 2017";
  private static final String MOCKED_RANDOM_SEQUENCE = "aaaaaaaaaa";
  private static final String MOCKED_LOG_RECORD =
      "{\"guid\":\"00000000-0000-0000-0000-000000000005\",\"ip\":\"127.0.0.1\","
          + "\"dns\":\"DNSNAME\",\"ct\":\"Tue May 09 10:33:39 CEST 2017\",\"pkHashes\":[]}";
  private static final String HASH_NAME = "TO0";
  private static final int RANDOM_STRING_SIZE = 10;
  @Mock private JedisPool jedisPool;

  @InjectMocks
  private TransferOwnership0Repository transferOwnership0Repository =
      new TransferOwnership0Repository();

  @Test
  public void testSetTransactionInfo() throws JsonProcessingException {
    TO0TransactionInfo transactionInfo = Mockito.mock(TO0TransactionInfo.class);
    PowerMockito.mockStatic(JsonUtils.class);
    PowerMockito.mockStatic(RandomUtils.class);
    Jedis jedis = Mockito.mock(Jedis.class);

    Mockito.when(transactionInfo.getCurrentTimestamp()).thenReturn(MOCKED_TIMESTAMP);
    Mockito.when(jedisPool.getResource()).thenReturn(jedis);
    PowerMockito.when(RandomUtils.generateRandomChars(RANDOM_STRING_SIZE))
        .thenReturn(MOCKED_RANDOM_SEQUENCE);
    PowerMockito.when(JsonUtils.mapObjectToJson(any())).thenReturn(MOCKED_LOG_RECORD);

    transferOwnership0Repository.setTransactionInfo(transactionInfo);

    Mockito.verify(jedis)
        .hset(HASH_NAME, MOCKED_TIMESTAMP + ":" + MOCKED_RANDOM_SEQUENCE, MOCKED_LOG_RECORD);
  }

  @SuppressWarnings("unchecked")
  @Test(expectedExceptions = JedisConnectionException.class)
  public void testSetTransactionInfoBrokenConnection() throws JsonProcessingException {
    Jedis jedis = Mockito.mock(Jedis.class);
    Mockito.when(jedisPool.getResource()).thenThrow(JedisConnectionException.class);
    transferOwnership0Repository.setTransactionInfo(null);
    Mockito.verify(jedis, never()).hset(anyString(), anyString(), anyString());
  }

  @SuppressWarnings("unchecked")
  @Test(expectedExceptions = JedisException.class)
  public void testSetTransactionInfoFailedToSetLogRecord() throws JsonProcessingException {
    TO0TransactionInfo transactionInfo = Mockito.mock(TO0TransactionInfo.class);
    PowerMockito.mockStatic(JsonUtils.class);
    PowerMockito.mockStatic(RandomUtils.class);
    Jedis jedis = Mockito.mock(Jedis.class);

    Mockito.when(transactionInfo.getCurrentTimestamp()).thenReturn(MOCKED_TIMESTAMP);
    Mockito.when(jedisPool.getResource()).thenReturn(jedis);
    PowerMockito.when(RandomUtils.generateRandomChars(RANDOM_STRING_SIZE))
        .thenReturn(MOCKED_RANDOM_SEQUENCE);
    PowerMockito.when(JsonUtils.mapObjectToJson(any())).thenReturn(MOCKED_LOG_RECORD);
    Mockito.when(jedis.hset(anyString(), anyString(), anyString())).thenThrow(JedisException.class);

    transferOwnership0Repository.setTransactionInfo(transactionInfo);

    Mockito.verify(jedis)
        .hset(HASH_NAME, MOCKED_TIMESTAMP + ":" + MOCKED_RANDOM_SEQUENCE, MOCKED_LOG_RECORD);
  }

  @SuppressWarnings("unchecked")
  @Test(expectedExceptions = JsonProcessingException.class)
  public void testSetTransactionInfoFailedToMapToJsonObject() throws JsonProcessingException {
    TO0TransactionInfo transactionInfo = Mockito.mock(TO0TransactionInfo.class);
    PowerMockito.mockStatic(JsonUtils.class);
    PowerMockito.mockStatic(RandomUtils.class);
    Jedis jedis = Mockito.mock(Jedis.class);

    Mockito.when(transactionInfo.getCurrentTimestamp()).thenReturn(MOCKED_TIMESTAMP);
    Mockito.when(jedisPool.getResource()).thenReturn(jedis);
    PowerMockito.when(RandomUtils.generateRandomChars(RANDOM_STRING_SIZE))
        .thenReturn(MOCKED_RANDOM_SEQUENCE);
    PowerMockito.when(JsonUtils.mapObjectToJson(any())).thenThrow(JsonProcessingException.class);

    transferOwnership0Repository.setTransactionInfo(transactionInfo);

    Mockito.verify(jedis, never())
        .hset(HASH_NAME, MOCKED_TIMESTAMP + ":" + MOCKED_RANDOM_SEQUENCE, MOCKED_LOG_RECORD);
  }
}
