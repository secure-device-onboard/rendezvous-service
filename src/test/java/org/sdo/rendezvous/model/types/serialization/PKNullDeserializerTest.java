// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdo.rendezvous.model.types.PKNull;
import org.sdo.rendezvous.model.types.PublicKeyEncoding;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PKNullDeserializerTest {

  private ObjectMapper mapper;

  @BeforeMethod
  public void setUp() throws Exception {
    mapper = new ObjectMapper();
  }

  @Test
  public void testPKNullDeserializerPositive() throws Exception {
    String json = "[0,0,[0]]";
    PKNull nullKey = mapper.readValue(json, PKNull.class);
    Assert.assertEquals(nullKey.getPkType(), PublicKeyType.NONE);
    Assert.assertEquals(nullKey.getPkEnc(), PublicKeyEncoding.NONE);
  }

  @Test(expectedExceptions = JsonProcessingException.class)
  public void testPKNullDeserializerNotArrayNegative() throws Exception {
    String json = "[0,0,0]";
    mapper.readValue(json, PKNull.class);
  }

  @Test(expectedExceptions = JsonProcessingException.class)
  public void testPKNullDeserializerNotZeroNegative() throws Exception {
    String json = "[0,0,[1]]";
    mapper.readValue(json, PKNull.class);
  }

  @Test(expectedExceptions = JsonProcessingException.class)
  public void testPKNullDeserializerWrongTypeNegative() throws Exception {
    String json = "[1,0,[0]]";
    mapper.readValue(json, PKNull.class);
  }
}
