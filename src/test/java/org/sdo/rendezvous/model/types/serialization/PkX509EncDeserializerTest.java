// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdo.rendezvous.model.types.PkX509Enc;
import org.sdo.rendezvous.model.types.PublicKeyEncoding;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PkX509EncDeserializerTest {

  @Test
  public void testDeserializePositive() throws Exception {
    String json = "[1,1,[4,\"ESIzRA==\"]]";
    ObjectMapper mapper = new ObjectMapper();
    PkX509Enc pubkey = mapper.readValue(json, PkX509Enc.class);

    Assert.assertEquals(pubkey.getPkType(), PublicKeyType.RSA2048RESTR);
    Assert.assertEquals(pubkey.getPkEnc(), PublicKeyEncoding.X509);
    Assert.assertEquals(pubkey.getPkX509(), new byte[] {0x11, 0x22, 0x33, 0x44});
  }

  @Test(expectedExceptions = JsonParseException.class)
  public void testDeserializeNegative() throws Exception {
    String json = "[1,1,4,\"ESIzRA==\"]";
    ObjectMapper mapper = new ObjectMapper();
    mapper.readValue(json, PkX509Enc.class);
  }

  @Test(expectedExceptions = JsonParseException.class)
  public void testDeserializeWrongTypeNegative() throws Exception {
    String json = "[92,1,[4,\"ESIzRA==\"]]";
    ObjectMapper mapper = new ObjectMapper();
    mapper.readValue(json, PkX509Enc.class);
  }
}
