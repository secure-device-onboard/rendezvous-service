// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.xml.bind.DatatypeConverter;
import org.sdo.rendezvous.model.types.Signature;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SignatureSerializerTest {

  @Test
  public void testSerializePositive() throws Exception {
    String expectedJson = "[4,\"ESIzRA==\"]";

    ObjectMapper objectMapper = new ObjectMapper();

    byte[] exemplarySignature = DatatypeConverter.parseHexBinary("11223344");
    Signature signature = new Signature(exemplarySignature);
    String outJason = objectMapper.writeValueAsString(signature);

    Assert.assertEquals(outJason, expectedJson);
  }
}
