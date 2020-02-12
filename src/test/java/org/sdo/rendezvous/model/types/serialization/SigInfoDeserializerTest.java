// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.sdo.rendezvous.model.types.SigInfo;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SigInfoDeserializerTest {

  @Test
  public void testDeserializePositiveEpid() throws IOException {
    String json = "[92,4,\"AAAABQ==\"]";

    ObjectMapper mapper = new ObjectMapper();
    SigInfo sigInfo = mapper.readValue(json, SigInfo.class);

    Assert.assertEquals(sigInfo.getBytes(), new byte[] {0x00, 0x00, 0x00, 0x05});
    Assert.assertEquals(sigInfo.getSigInfoType(), PublicKeyType.EPID_2_0);
  }

  @Test
  public void testDeserializePositiveEcdsa() throws IOException {
    String json = "[13,4,\"AAAABQ==\"]";

    ObjectMapper mapper = new ObjectMapper();
    SigInfo sigInfo = mapper.readValue(json, SigInfo.class);

    Assert.assertEquals(sigInfo.getBytes(), new byte[] {0x00, 0x00, 0x00, 0x05});
    Assert.assertEquals(sigInfo.getSigInfoType(), PublicKeyType.ECDSA_P_256);
  }

  @Test(expectedExceptions = JsonParseException.class)
  public void testDeserializeNegativeInvalidJson() throws IOException {
    String json = "4,\"AAAABQ==\"";
    ObjectMapper mapper = new ObjectMapper();
    mapper.readValue(json, SigInfo.class);
  }

  @Test(expectedExceptions = JsonParseException.class)
  public void testDeserializeNegativeInvalidPkType() throws IOException {
    String json = "[1,4,\"AAAABQ==\"]";
    ObjectMapper mapper = new ObjectMapper();
    mapper.readValue(json, SigInfo.class);
  }
}
