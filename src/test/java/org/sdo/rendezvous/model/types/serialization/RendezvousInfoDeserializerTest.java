// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdo.rendezvous.model.types.RendezvousInfo;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RendezvousInfoDeserializerTest {

  @Test
  public void testDeserializeTwoEntriesPositive() throws Exception {

    String json =
        "[2,[4,{\"ip\":[8,\"fwAAAQ==\"],\"me\":\"eth0\",\"dn\":\"DNSNAME\","
            + "\"po\":8080}],[6,{\"ss\":\"MYSSID\",\"ip\":[8,\"fwAAAQ==\"],"
            + "\"pw\":\"admin\",\"me\":\"wifi5\",\"dn\":\"DNSNAME2\",\"po\":9090}]]";
    ObjectMapper mapper = new ObjectMapper();
    RendezvousInfo rendezvousInfo = mapper.readValue(json, RendezvousInfo.class);
    Assert.assertEquals(rendezvousInfo.getLength(), 2);
  }
}
