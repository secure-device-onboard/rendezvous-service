// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdo.rendezvous.model.types.PkX509Enc;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PkX509EncSerializerTest {

  @Test
  public void testSerializePositive() throws Exception {
    String expectedJson = "[1,1,[4,\"AAAABQ==\"]]";

    ObjectMapper objectMapper = new ObjectMapper();

    PkX509Enc pkx509Enc =
        new PkX509Enc(PublicKeyType.RSA2048RESTR, new byte[] {0x00, 0x00, 0x00, 0x05});
    String outJason = objectMapper.writeValueAsString(pkx509Enc);

    Assert.assertEquals(outJason, expectedJson);
  }
}
