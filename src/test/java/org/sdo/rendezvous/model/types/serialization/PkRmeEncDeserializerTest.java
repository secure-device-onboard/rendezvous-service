// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdo.rendezvous.model.types.PkRmeEnc;
import org.sdo.rendezvous.model.types.PublicKeyEncoding;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PkRmeEncDeserializerTest {

  @Test
  public void testDeserializePositive() throws Exception {

    String json = "[1,3,[4,\"ESIzRA==\",3,\"ESIz\"]]";
    ObjectMapper mapper = new ObjectMapper();
    PkRmeEnc pubkey = mapper.readValue(json, PkRmeEnc.class);

    Assert.assertEquals(pubkey.getPkType(), PublicKeyType.RSA2048RESTR);
    Assert.assertEquals(pubkey.getPkEnc(), PublicKeyEncoding.RSAMODEXP);
    Assert.assertEquals(pubkey.getModba(), new byte[] {0x11, 0x22, 0x33, 0x44});
    Assert.assertEquals(pubkey.getExpba(), new byte[] {0x11, 0x22, 0x33});
  }

  @Test(expectedExceptions = JsonParseException.class)
  public void testDeserializeNegative() throws Exception {

    String json = "[1,3,5,[\"AAA=\",\"ERE=\"]]";
    ObjectMapper mapper = new ObjectMapper();
    mapper.readValue(json, PkRmeEnc.class);
  }

  @Test(expectedExceptions = JsonParseException.class)
  public void testDeserializeWrongTypeNegative() throws Exception {
    String json = "[92,3,[4,\"ESIzRA==\",3,\"ESIz\"]]";
    ObjectMapper mapper = new ObjectMapper();
    mapper.readValue(json, PkRmeEnc.class);
  }
}
