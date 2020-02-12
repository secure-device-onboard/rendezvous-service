// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import org.sdo.rendezvous.model.types.RendezvousInstr;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RendezvousInstrDeserializerTest {

  private ObjectMapper objectMapper;

  @BeforeMethod
  public void setUp() {
    this.objectMapper = new ObjectMapper();
  }

  @Test
  public void testDeserializeOnlyRequiredFieldPositive() throws Exception {
    String json = "[4,{\"ip\":[4,\"fwAAAQ==\"],\"me\":\"eth0\",\"dn\":\"DNSNAME\",\"po\":8080}]";
    RendezvousInstr instr = objectMapper.readValue(json, RendezvousInstr.class);
    Assert.assertEquals(instr.getIpAddress().get().getHostAddress(), "127.0.0.1");
    Assert.assertEquals(instr.getMedium().get(), "eth0");
    Assert.assertEquals(instr.getDns().get(), "DNSNAME");
    Assert.assertEquals((int) instr.getPortDevice().get(), 8080);
  }

  @Test
  public void testDeserializeWithWifiSettingsPositive() throws Exception {
    String json =
        "[4,{\"ip\":[4,\"fwAAAQ==\"],\"me\":\"wifi1\",\"ss\":\"MYSSID\",\"pw\":\"admin\","
            + "\"dn\":\"DNSNAME\",\"po\":8080,\"wsp\":\"WPA\"}]";
    RendezvousInstr instr = objectMapper.readValue(json, RendezvousInstr.class);
    Assert.assertEquals(instr.getIpAddress().get().getHostAddress(), "127.0.0.1");
    Assert.assertEquals(instr.getMedium().get(), "wifi1");
    Assert.assertEquals(instr.getDns().get(), "DNSNAME");
    Assert.assertEquals((int) instr.getPortDevice().get(), 8080);
    Assert.assertEquals(instr.getWifiSsid().get(), "MYSSID");
    Assert.assertEquals(instr.getWifiPassword().get(), "admin");
    Assert.assertEquals(instr.getWifiSecurityProtocol().get(), "WPA");
  }

  @Test
  public void testDeserializeWithTlsHashesFieldPositive() throws Exception {
    byte[] hash = new byte[32];
    Arrays.fill(hash, (byte) 11);

    String json =
        "[4,{\"ip\":[4,\"fwAAAQ==\"],\"me\":\"eth0\",\"dn\":\"DNSNAME\",\"po\":8080,"
            + "\"sch\":[32,8,\"CwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCws=\" ],"
            + "\"cch\":[32,8,\"CwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCwsLCws=\"]}]";
    RendezvousInstr instr = objectMapper.readValue(json, RendezvousInstr.class);
    Assert.assertEquals(instr.getIpAddress().get().getHostAddress(), "127.0.0.1");
    Assert.assertEquals(instr.getMedium().get(), "eth0");
    Assert.assertEquals(instr.getDns().get(), "DNSNAME");
    Assert.assertEquals((int) instr.getPortDevice().get(), 8080);
    Assert.assertEquals(instr.getTlsServerCertHash().get().getHash(), hash);
    Assert.assertEquals(instr.getCaCertHash().get().getHash(), hash);
  }
}
