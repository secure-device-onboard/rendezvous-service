// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdo.rendezvous.model.types.PkEpidEnc;
import org.sdo.rendezvous.model.types.PublicKeyEncoding;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PkEpidEncDeserializerTest {

  @Test
  public void testDeserializePositive() throws Exception {
    String json = "[92,4,[16,\"AAAAAAAAAAAAAAAAAQIDBA==\"]]";
    ObjectMapper mapper = new ObjectMapper();
    PkEpidEnc pubkey = mapper.readValue(json, PkEpidEnc.class);

    Assert.assertEquals(pubkey.getPkType(), PublicKeyType.EPID_2_0);
    Assert.assertEquals(pubkey.getPkEnc(), PublicKeyEncoding.EPID);
    Assert.assertEquals(
        pubkey.getEpidGroupNo(),
        new byte[] {
          0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x1, 0x2, 0x3, 0x4
        });
  }

  @Test(expectedExceptions = JsonParseException.class)
  public void testDeserializeNegative() throws Exception {
    String json = "[92,4,16,\"AAAAAAAAAAAAAAAAAQIDBA==\"]";
    ObjectMapper mapper = new ObjectMapper();
    mapper.readValue(json, PkEpidEnc.class);
  }

  @Test(expectedExceptions = JsonParseException.class)
  public void testDeserializeWrongTypeNegative() throws Exception {
    String json = "[1,4,[16,\"AAAAAAAAAAAAAAAAAQIDBA==\"]]";
    ObjectMapper mapper = new ObjectMapper();
    mapper.readValue(json, PkEpidEnc.class);
  }
}
