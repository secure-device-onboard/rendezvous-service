// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdo.rendezvous.model.types.Signature;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SignatureDeserializerTest {

  private ObjectMapper mapper;

  @BeforeMethod
  public void setUp() throws Exception {
    mapper = new ObjectMapper();
  }

  @Test
  public void testSignatureDeserializerPositive() throws Exception {
    String json = "[4,\"ESIzRA==\"]";
    Signature signature = mapper.readValue(json, Signature.class);
    Assert.assertEquals(signature.getBytes(), new byte[] {0x11, 0x22, 0x33, 0x44});
  }
}
