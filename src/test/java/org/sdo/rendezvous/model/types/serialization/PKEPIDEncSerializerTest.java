// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdo.rendezvous.model.types.PKEPIDEnc;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PKEPIDEncSerializerTest {

  @Test
  public void testSerializePositive() throws Exception {
    String expectedJson = "[91,4,[4,\"AAAABQ==\"]]";

    ObjectMapper objectMapper = new ObjectMapper();

    PKEPIDEnc pkepidEnc =
        new PKEPIDEnc(PublicKeyType.EPID_1_1, new byte[] {0x00, 0x00, 0x00, 0x05});
    String outJason = objectMapper.writeValueAsString(pkepidEnc);

    Assert.assertEquals(outJason, expectedJson);
  }
}
