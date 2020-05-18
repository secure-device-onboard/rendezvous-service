// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdo.rendezvous.model.types.PkRmeEnc;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PkRmeEncSerializerTest {

  @Test
  public void testSerializePositive() throws Exception {
    String expectedJson = "[1,3,[4,\"AAAABQ==\",2,\"IiI=\"]]";

    ObjectMapper objectMapper = new ObjectMapper();

    PkRmeEnc pkRmeEnc =
        new PkRmeEnc(
            PublicKeyType.RSA2048RESTR,
            new byte[] {0x00, 0x00, 0x00, 0x05},
            new byte[] {0x22, 0x22});
    String outJason = objectMapper.writeValueAsString(pkRmeEnc);

    Assert.assertEquals(outJason, expectedJson);
  }
}
