// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SigInfoTest {

  private static final byte[] EPID_DATA = parseHexBinary("000011111111222233335555");

  private SigInfo sigInfo;

  @Test
  void testToString_shouldReturnOnlySignatureInfoWhenSigInfoTypeIsEcdsa() {
    sigInfo = new SigInfo(PublicKeyType.ECDSA_P_256, null);
    Assert.assertEquals(sigInfo.toString(), "[(signature info type)=13(ECDSA_P_256)]");
  }

  @Test
  void testToString_shouldReturnSignatureInfoAndBytesWhenSigInfoTypeIsEpid() {
    sigInfo = new SigInfo(PublicKeyType.EPID_2_0, EPID_DATA);
    Assert.assertEquals(
        sigInfo.toString(),
        "[(signature info type)=92(EPID_2_0), (bytes)=" + printHexBinary(EPID_DATA) + "]");
  }
}
