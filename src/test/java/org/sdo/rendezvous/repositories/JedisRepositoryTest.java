// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.repositories;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.sdo.rendezvous.exceptions.ResourceNotFoundException;
import org.sdo.rendezvous.model.database.VersionedTo1Data;
import org.sdo.rendezvous.model.log.to1.TO1TransactionInfo;
import org.sdo.rendezvous.model.types.OwnerSignTo1Data;
import org.sdo.rendezvous.utils.JsonUtils;
import org.sdo.rendezvous.utils.RandomUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
@PrepareForTest({JsonUtils.class, RandomUtils.class})
public class JedisRepositoryTest extends PowerMockTestCase {

  private static final String GUID = "00000000-0000-0000-0000-000000000001";
  private static final String INVALID_GUID = "00000000-0000-0000-0000-000000000002";
  private static final String DATA = "testData";
  private static final int EXPIRATION_TIME = 1000;
  private static String MOCKED_TIMESTAMP = "Tue May 09 10:33:39 CEST 2017";
  private static String MOCKED_RANDOM_SEQUENCE = "aaaaaaaaaa";
  private static String MOCKED_LOG_RECORD =
      "{\"guid\":\"00000000-0000-0000-0000-000000000003\","
          + "\"ct\":\"Tue May 09 10:33:39 CEST 2017\","
          + "\"gid\":\"00000000000000000000000000001234\"}";
  private static String HASH_NAME = "TO1";
  private static int RANDOM_STRING_SIZE = 10;
  @Mock private JedisPool jedisPool;
  @Mock private Jedis jedis;
  @InjectMocks private JedisRepository jedisRepository;
  private VersionedTo1Data versionedTO1Data = new VersionedTo1Data(new OwnerSignTo1Data());

  @BeforeMethod()
  public void setUp() {
    Mockito.when(jedisPool.getResource()).thenReturn(jedis);
    PowerMockito.mockStatic(JsonUtils.class);
  }

  @Test
  public void testSetVersionedTO1WithEcdsa() {
    // given
    PowerMockito.when(jedisPool.getResource()).thenReturn(jedis);

    // when
    jedisRepository.setVersionedTO1Data(GUID, DATA, EXPIRATION_TIME);

    // then
    Mockito.verify(jedis).setex(GUID, EXPIRATION_TIME, DATA);
  }

  @SuppressWarnings("unchecked")
  @Test(expectedExceptions = JedisConnectionException.class)
  public void testSetVersionedTO1WithEcdsaBrokenConnection() {
    // given
    Mockito.when(jedisPool.getResource()).thenThrow(JedisConnectionException.class);

    // when
    jedisRepository.setVersionedTO1Data(GUID, DATA, EXPIRATION_TIME);

    // then
  }

  @SuppressWarnings("unchecked")
  @Test(expectedExceptions = JedisException.class)
  public void testSetVersionedTO1WithEcdsaFailToSetData() {
    // given
    Mockito.when(jedis.setex(GUID, EXPIRATION_TIME, DATA)).thenThrow(JedisException.class);

    // when
    jedisRepository.setVersionedTO1Data(GUID, DATA, EXPIRATION_TIME);

    // then
    Mockito.verify(jedis).set(GUID, DATA);
    Mockito.verify(jedis, never()).expire(anyString(), anyInt());
  }

  @Test
  public void testGetVersionedTO1WithEcdsa() throws ResourceNotFoundException, IOException {
    // given
    Mockito.when(jedis.get(anyString())).thenReturn(DATA);
    PowerMockito.when(JsonUtils.mapJsonToObject(any(), any())).thenReturn(versionedTO1Data);

    // when
    jedisRepository.getVersionedTO1Data(GUID);

    // then
    Mockito.verify(jedis).get(GUID);
  }

  @Test(
      expectedExceptions = ResourceNotFoundException.class,
      expectedExceptionsMessageRegExp =
          "Not found owner connection info for GUID: 00000000-0000-0000-0000-000000000002.")
  public void testGetVersionedTO1WithEcdsaInvalidGuid()
      throws ResourceNotFoundException, IOException {
    // given
    Mockito.when(jedis.get(anyString())).thenReturn(null);

    // when
    jedisRepository.getVersionedTO1Data(INVALID_GUID);

    // then
    Mockito.verify(jedis).get(INVALID_GUID);
  }

  @SuppressWarnings("unchecked")
  @Test(expectedExceptions = IOException.class)
  public void testGetVersionedTO1WithEcdsaInvalidTO1Data()
      throws ResourceNotFoundException, IOException {
    // given
    Mockito.when(jedis.get(anyString())).thenReturn(DATA);
    PowerMockito.when(JsonUtils.mapJsonToObject(any(), any())).thenThrow(IOException.class);

    // when
    jedisRepository.getVersionedTO1Data(GUID);

    // then
    Mockito.verify(jedis).get(GUID);
  }

  @Test
  public void testDeleteVersionedTO1WithEcdsa() {
    // given
    Mockito.when(jedis.get(GUID)).thenReturn(DATA);

    // when
    jedisRepository.deleteVersionedTO1Data(GUID);

    // then
    Mockito.verify(jedis, times(1)).del(GUID);
  }

  @Test
  public void testDeleteVersionedTO1WithEcdsaMissingGuid() {
    // given
    Mockito.when(jedis.get(anyString())).thenReturn(null);

    // when
    jedisRepository.deleteVersionedTO1Data(GUID);

    // then
    Mockito.verify(jedis, times(0)).del(anyString());
  }

  @Test
  public void testSetTransactionInfo() throws JsonProcessingException {
    TO1TransactionInfo transactionInfo = Mockito.mock(TO1TransactionInfo.class);
    PowerMockito.mockStatic(JsonUtils.class);
    PowerMockito.mockStatic(RandomUtils.class);
    Jedis jedis = Mockito.mock(Jedis.class);

    Mockito.when(transactionInfo.getCurrentTimestamp()).thenReturn(MOCKED_TIMESTAMP);
    Mockito.when(jedisPool.getResource()).thenReturn(jedis);
    PowerMockito.when(RandomUtils.generateRandomChars(RANDOM_STRING_SIZE))
        .thenReturn(MOCKED_RANDOM_SEQUENCE);
    PowerMockito.when(JsonUtils.mapObjectToJson(any())).thenReturn(MOCKED_LOG_RECORD);

    jedisRepository.setTransactionInfo(transactionInfo);

    Mockito.verify(jedis)
        .hset(HASH_NAME, MOCKED_TIMESTAMP + ":" + MOCKED_RANDOM_SEQUENCE, MOCKED_LOG_RECORD);
  }

  @SuppressWarnings("unchecked")
  @Test(expectedExceptions = JedisConnectionException.class)
  public void testSetTransactionInfoBrokenConnection() throws JsonProcessingException {
    Jedis jedis = Mockito.mock(Jedis.class);
    Mockito.when(jedisPool.getResource()).thenThrow(JedisConnectionException.class);
    jedisRepository.setTransactionInfo(null);
    Mockito.verify(jedis, never()).hset(anyString(), anyString(), anyString());
  }

  @SuppressWarnings("unchecked")
  @Test(expectedExceptions = JedisException.class)
  public void testSetTransactionInfoFailedToSetLogRecord() throws JsonProcessingException {
    TO1TransactionInfo transactionInfo = Mockito.mock(TO1TransactionInfo.class);
    PowerMockito.mockStatic(JsonUtils.class);
    PowerMockito.mockStatic(RandomUtils.class);
    Jedis jedis = Mockito.mock(Jedis.class);

    Mockito.when(transactionInfo.getCurrentTimestamp()).thenReturn(MOCKED_TIMESTAMP);
    Mockito.when(jedisPool.getResource()).thenReturn(jedis);
    PowerMockito.when(RandomUtils.generateRandomChars(RANDOM_STRING_SIZE))
        .thenReturn(MOCKED_RANDOM_SEQUENCE);
    PowerMockito.when(JsonUtils.mapObjectToJson(any())).thenReturn(MOCKED_LOG_RECORD);
    Mockito.when(jedis.hset(anyString(), anyString(), anyString())).thenThrow(JedisException.class);

    jedisRepository.setTransactionInfo(transactionInfo);

    Mockito.verify(jedis)
        .hset(HASH_NAME, MOCKED_TIMESTAMP + ":" + MOCKED_RANDOM_SEQUENCE, MOCKED_LOG_RECORD);
  }

  @SuppressWarnings("unchecked")
  @Test(expectedExceptions = JsonProcessingException.class)
  public void testSetTransactionInfoFailedToMapToJsonObject() throws JsonProcessingException {
    TO1TransactionInfo transactionInfo = Mockito.mock(TO1TransactionInfo.class);
    PowerMockito.mockStatic(JsonUtils.class);
    PowerMockito.mockStatic(RandomUtils.class);
    Jedis jedis = Mockito.mock(Jedis.class);

    Mockito.when(transactionInfo.getCurrentTimestamp()).thenReturn(MOCKED_TIMESTAMP);
    Mockito.when(jedisPool.getResource()).thenReturn(jedis);
    PowerMockito.when(RandomUtils.generateRandomChars(RANDOM_STRING_SIZE))
        .thenReturn(MOCKED_RANDOM_SEQUENCE);
    PowerMockito.when(JsonUtils.mapObjectToJson(any())).thenThrow(JsonProcessingException.class);

    jedisRepository.setTransactionInfo(transactionInfo);

    Mockito.verify(jedis, never())
        .hset(HASH_NAME, MOCKED_TIMESTAMP + ":" + MOCKED_RANDOM_SEQUENCE, MOCKED_LOG_RECORD);
  }
}
