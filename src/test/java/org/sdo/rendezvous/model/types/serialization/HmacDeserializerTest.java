// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdo.rendezvous.model.types.HashType;
import org.sdo.rendezvous.model.types.Hmac;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class HmacDeserializerTest {

  private ObjectMapper mapper;

  @BeforeMethod
  public void setUp() throws Exception {
    mapper = new ObjectMapper();
  }

  @Test
  public void testHmacDeserializerPositive() throws Exception {
    String json = "[4,8,\"AAAABQ==\"]";
    Hmac hmac = mapper.readValue(json, Hmac.class);
    Assert.assertEquals(hmac.getHashType(), HashType.SHA256);
    Assert.assertEquals(hmac.getHmac(), new byte[] {0x00, 0x00, 0x00, 0x05});
  }
}
