// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdo.rendezvous.model.types.PkOnDieEcdsaNull;
import org.sdo.rendezvous.model.types.PublicKeyEncoding;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PkOnDieEcdsaNullDeserializerTest {

  private ObjectMapper mapper;

  @BeforeMethod
  public void setUp() throws Exception {
    mapper = new ObjectMapper();
  }

  @Test
  public void testPkOnDieEcdsaNullDeserializerPositive() throws Exception {
    String json = "[93,5,[0]]";
    PkOnDieEcdsaNull nullKey = mapper.readValue(json, PkOnDieEcdsaNull.class);
    Assert.assertEquals(nullKey.getPkType(), PublicKeyType.ONDIE_ECDSA_384);
    Assert.assertEquals(nullKey.getPkEnc(), PublicKeyEncoding.ONDIE_ECDSA);
  }

  @Test(expectedExceptions = JsonProcessingException.class)
  public void testPkOnDieEcdsaNullDeserializerEncodingNotFiveNegative() throws Exception {
    String json = "[93,0,0]";
    mapper.readValue(json, PkOnDieEcdsaNull.class);
  }

  @Test(expectedExceptions = JsonProcessingException.class)
  public void testPkOnDieEcdsaNullDeserializerNotArrayNegative() throws Exception {
    String json = "[93,5,0]";
    mapper.readValue(json, PkOnDieEcdsaNull.class);
  }

  @Test(expectedExceptions = JsonProcessingException.class)
  public void testPkOnDieEcdsaNullDeserializerValueNotZeroNegative() throws Exception {
    String json = "[93,5,[1]]";
    mapper.readValue(json, PkOnDieEcdsaNull.class);
  }

  @Test(expectedExceptions = JsonProcessingException.class)
  public void testPkOnDieEcdsaNullDeserializerWrongTypeNegative() throws Exception {
    String json = "[1,5,[0]]";
    mapper.readValue(json, PkOnDieEcdsaNull.class);
  }
}
