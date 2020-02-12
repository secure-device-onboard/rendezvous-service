// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.sdo.rendezvous.model.types.AppId;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AppIdDeserializerTest {

  @Test
  public void testDeserializePositive() throws IOException {
    String json = "[2,2,\"AAE=\"]";
    ObjectMapper mapper = new ObjectMapper();
    AppId appId = mapper.readValue(json, AppId.class);

    Assert.assertEquals(appId.getType(), 2);
    Assert.assertEquals(appId.getAppIdBytes(), new byte[] {0x00, 0x01});
  }

  @Test(expectedExceptions = JsonParseException.class)
  public void testDeserializeNegative() throws IOException {
    String json = "2,2,\"AAE=\"";
    ObjectMapper mapper = new ObjectMapper();
    mapper.readValue(json, AppId.class);
  }
}
