// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sdo.rendezvous.model.types.IpAddress;
import org.sdo.rendezvous.model.types.RendezvousInfo;
import org.sdo.rendezvous.model.types.RendezvousInstr;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RendezvousInfoSerializerTest {
  @Test
  public void testSerializePositive() throws Exception {
    RendezvousInstr rendezvousInstr = new RendezvousInstr();
    rendezvousInstr.setIpAddress(new IpAddress("127.0.0.1"));
    rendezvousInstr.setPortDevice(8080);
    rendezvousInstr.setDns("DNSNAME");
    rendezvousInstr.setMedium("eth0");

    RendezvousInstr rendezvousInstr2 = new RendezvousInstr();
    rendezvousInstr2.setIpAddress(new IpAddress("127.0.0.1"));
    rendezvousInstr2.setPortDevice(9090);
    rendezvousInstr2.setDns("DNSNAME2");
    rendezvousInstr2.setMedium("wifi5");
    rendezvousInstr2.setWifiSsid("MYSSID");
    rendezvousInstr2.setWifiPassword("admin");
    rendezvousInstr2.setWifiSecurityProtocol("WPA");
    ObjectMapper objectMapper = new ObjectMapper();
    String expectedJson =
        "[2,[4,{\"dn\":\"DNSNAME\",\"ip\":[4,\"fwAAAQ==\"],\"me\":\"eth0\","
            + "\"po\":8080}],[7,{\"dn\":\"DNSNAME2\",\"ip\":[4,\"fwAAAQ==\"],"
            + "\"me\":\"wifi5\",\"po\":9090,\"pw\":\"admin\",\"ss\":\"MYSSID\",\"wsp\":\"WPA\"}]]";

    RendezvousInfo rendezvousInfo = new RendezvousInfo();
    rendezvousInfo.add(rendezvousInstr);
    rendezvousInfo.add(rendezvousInstr2);
    String outJason = objectMapper.writeValueAsString(rendezvousInfo);
    Assert.assertEquals(outJason, expectedJson);
  }
}
