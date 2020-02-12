// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import org.sdo.rendezvous.exceptions.InvalidIpAddressException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RendezvousInstrTest {

  @Test
  public void createInstancePositive() throws InvalidIpAddressException {
    RendezvousInstr instr = new RendezvousInstr();
    instr.setOnly("dev");
    instr.setIpAddress(new IpAddress("127.0.0.1"));
    instr.setPortDevice(8080);
    instr.setPortOwner(9090);
    instr.setDns("DNSNAME");
    instr.setTlsServerCertHash(new Hash(HashType.SHA256, new byte[] {0x66}));
    instr.setTlsCaCertHash(new Hash(HashType.SHA256, new byte[] {0x55}));
    instr.setUserInput(1);
    instr.setWifiSsid("NETWORK");
    instr.setWifiPassword("PASSWORD");
    instr.setWifiSecurityProtocol("WPA");
    instr.setMedium("eth0");
    instr.setProtocol("https");
    instr.setDelaySec(123);
    Assert.assertEquals(instr.getOnly().get(), "dev");
    Assert.assertEquals(instr.getIpAddress().get().getHostAddress(), "127.0.0.1");
    Assert.assertEquals((int) instr.getPortDevice().get(), 8080);
    Assert.assertEquals((int) instr.getPortOwner().get(), 9090);
    Assert.assertEquals(instr.getDns().get(), "DNSNAME");
    Assert.assertEquals(instr.getTlsServerCertHash().get().getHash(), new byte[] {0x66});
    Assert.assertEquals(instr.getCaCertHash().get().getHash(), new byte[] {0x55});
    Assert.assertEquals((int) instr.getUserInput().get(), 1);
    Assert.assertEquals(instr.getWifiSsid().get(), "NETWORK");
    Assert.assertEquals(instr.getWifiPassword().get(), "PASSWORD");
    Assert.assertEquals(instr.getWifiSecurityProtocol().get(), "WPA");
    Assert.assertEquals(instr.getMedium().get(), "eth0");
    Assert.assertEquals(instr.getProtocol().get(), "https");
    Assert.assertEquals((int) instr.getDelaySec().get(), 123);
  }
}
