// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdo.rendezvous.model.types.PkNull;
import org.sdo.rendezvous.model.types.PublicKeyEncoding;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PkNullDeserializerTest {

  private ObjectMapper mapper;

  @BeforeMethod
  public void setUp() throws Exception {
    mapper = new ObjectMapper();
  }

  @Test
  public void testPkNullDeserializerPositive() throws Exception {
    String json = "[0,0,[0]]";
    PkNull nullKey = mapper.readValue(json, PkNull.class);
    Assert.assertEquals(nullKey.getPkType(), PublicKeyType.NONE);
    Assert.assertEquals(nullKey.getPkEnc(), PublicKeyEncoding.NONE);
  }

  @Test(expectedExceptions = JsonProcessingException.class)
  public void testPkNullDeserializerEncodingNotZeroNegative() throws Exception {
    String json = "[0,5,0]";
    mapper.readValue(json, PkNull.class);
  }

  @Test(expectedExceptions = JsonProcessingException.class)
  public void testPkNullDeserializerNotArrayNegative() throws Exception {
    String json = "[0,0,0]";
    mapper.readValue(json, PkNull.class);
  }

  @Test(expectedExceptions = JsonProcessingException.class)
  public void testPkNullDeserializerValueNotZeroNegative() throws Exception {
    String json = "[0,0,[1]]";
    mapper.readValue(json, PkNull.class);
  }

  @Test(expectedExceptions = JsonProcessingException.class)
  public void testPkNullDeserializerWrongTypeNegative() throws Exception {
    String json = "[1,0,[0]]";
    mapper.readValue(json, PkNull.class);
  }
}
