// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sdo.rendezvous.enums.EpidVersion;
import org.sdo.rendezvous.exceptions.InvalidGroupIdException;
import org.sdo.rendezvous.exceptions.InvalidSigInfoException;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.sdo.rendezvous.model.types.SigInfo;
import org.springframework.web.client.HttpClientErrorException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class EpidMaterialServiceTest {

  private static final byte[] GROUP_ID_11 = DatatypeConverter.parseHexBinary("00000005");
  private static final byte[] GROUP_ID_20 =
      DatatypeConverter.parseHexBinary("00000000000000000000000000000005");

  @Mock private EpidVerificationService epidVerificationService;

  @Mock private HttpServletRequest httpServletRequest;

  private EpidMaterialService epidMaterialService;

  @DataProvider(name = "urls")
  public static Object[][] getUrls() {
    return new Object[][] {{new String("/mp/112/msg/30")}, {new String("/mp/113/msg/30")}};
  }

  @BeforeMethod
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    epidMaterialService = new EpidMaterialService(httpServletRequest, epidVerificationService);
  }

  @Test
  public void testGetEpidMaterialEpid10Positive() throws Exception {
    byte[] getGroupCertSigma10 = DatatypeConverter.parseHexBinary("0000");
    SigInfo sigInfo10 = new SigInfo(PublicKeyType.EPID_1_0, GROUP_ID_11);
    SigInfo sigInfo11 = new SigInfo(PublicKeyType.EPID_1_1, GROUP_ID_11);
    Mockito.when(epidVerificationService.getGroupCertSigma10(sigInfo11))
        .thenReturn(getGroupCertSigma10);

    byte[] expectedEpidMaterial = DatatypeConverter.parseHexBinary("00020000");

    SigInfo epidMaterial = epidMaterialService.getSigInfo(sigInfo10);
    Assert.assertEquals(epidMaterial.getBytes(), expectedEpidMaterial);
  }

  @Test(dataProvider = "urls")
  public void testGetEpidMaterialEpid11PositiveProtocol(String url) throws Exception {
    byte[] getGroupCertSigma10 = DatatypeConverter.parseHexBinary("0000");
    byte[] getGroupCertSigma11 = DatatypeConverter.parseHexBinary("0000");
    byte[] sigrl = DatatypeConverter.parseHexBinary("0001");
    SigInfo sigInfo = new SigInfo(PublicKeyType.EPID_1_1, GROUP_ID_11);

    Mockito.when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer(url));
    Mockito.when(epidVerificationService.getGroupCertSigma10(sigInfo))
        .thenReturn(getGroupCertSigma10);
    Mockito.when(epidVerificationService.getGroupCertSigma11(sigInfo))
        .thenReturn(getGroupCertSigma11);
    Mockito.when(epidVerificationService.getSigrl(sigInfo, EpidVersion.EPID11)).thenReturn(sigrl);

    byte[] expectedEpidMaterial = DatatypeConverter.parseHexBinary("000200000002000000020001");

    SigInfo epidMaterial = epidMaterialService.getSigInfo(sigInfo);
    Assert.assertEquals(epidMaterial.getBytes(), expectedEpidMaterial);
  }

  @Test(dataProvider = "urls")
  public void testGetEpidMaterialEpid11PositiveProtocolEmptySigrl(String url) throws Exception {
    byte[] getGroupCertSigma10 = DatatypeConverter.parseHexBinary("0000");
    byte[] getGroupCertSigma11 = DatatypeConverter.parseHexBinary("0000");
    byte[] sigrl = new byte[] {};
    SigInfo sigInfo = new SigInfo(PublicKeyType.EPID_1_1, GROUP_ID_11);

    Mockito.when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("url"));
    Mockito.when(epidVerificationService.getGroupCertSigma10(sigInfo))
        .thenReturn(getGroupCertSigma10);
    Mockito.when(epidVerificationService.getGroupCertSigma11(sigInfo))
        .thenReturn(getGroupCertSigma11);
    Mockito.when(epidVerificationService.getSigrl(sigInfo, EpidVersion.EPID11)).thenReturn(sigrl);

    byte[] expectedEpidMaterial = DatatypeConverter.parseHexBinary("00020000000200000000");

    SigInfo epidMaterial = epidMaterialService.getSigInfo(sigInfo);
    Assert.assertEquals(epidMaterial.getBytes(), expectedEpidMaterial);
  }

  @Test
  public void testGetEpidMaterialEpid11PositiveProtocol110() throws Exception {
    byte[] getGroupCertSigma10 = DatatypeConverter.parseHexBinary("0000");
    byte[] getGroupCertSigma11 = DatatypeConverter.parseHexBinary("0000");
    SigInfo sigInfo = new SigInfo(PublicKeyType.EPID_1_1, GROUP_ID_11);

    Mockito.when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("/mp/110/msg/30"));
    Mockito.when(epidVerificationService.getGroupCertSigma10(sigInfo))
        .thenReturn(getGroupCertSigma10);
    Mockito.when(epidVerificationService.getGroupCertSigma11(sigInfo))
        .thenReturn(getGroupCertSigma11);

    byte[] expectedEpidMaterial = DatatypeConverter.parseHexBinary("0002000000020000");
    SigInfo epidMaterial = epidMaterialService.getSigInfo(sigInfo);

    Assert.assertEquals(epidMaterial.getBytes(), expectedEpidMaterial);
  }

  @Test
  public void testGetEpidMaterialEpid20Positive() throws Exception {
    byte[] sigrl = DatatypeConverter.parseHexBinary("0000");
    byte[] pubkey = DatatypeConverter.parseHexBinary("0000");
    SigInfo sigInfo = new SigInfo(PublicKeyType.EPID_2_0, GROUP_ID_20);
    Mockito.when(epidVerificationService.getSigrl(sigInfo, EpidVersion.EPID20)).thenReturn(sigrl);
    Mockito.when(epidVerificationService.getPublicKey(sigInfo)).thenReturn(pubkey);

    byte[] expectedEpidMaterial = DatatypeConverter.parseHexBinary("0002000000020000");

    SigInfo epidMaterial = epidMaterialService.getSigInfo(sigInfo);
    Assert.assertEquals(epidMaterial.getBytes(), expectedEpidMaterial);
  }

  @Test(expectedExceptions = InvalidSigInfoException.class)
  public void testGetEpidMaterialThrowExceptionWhenMissingGroupIdEpid20() throws Exception {
    SigInfo sigInfo = new SigInfo(PublicKeyType.EPID_2_0, GROUP_ID_20);
    Mockito.doThrow(HttpClientErrorException.class)
        .when(epidVerificationService)
        .getSigrl(sigInfo, EpidVersion.EPID20);
    Mockito.doThrow(HttpClientErrorException.class)
        .when(epidVerificationService)
        .getPublicKey(sigInfo);

    epidMaterialService.getSigInfo(sigInfo);
  }

  @Test(expectedExceptions = InvalidSigInfoException.class)
  public void testGetEpidMaterialThrowExceptionWhenMissingGroupIdEpid11() throws Exception {
    SigInfo sigInfo = new SigInfo(PublicKeyType.EPID_1_1, GROUP_ID_11);
    Mockito.doThrow(HttpClientErrorException.class)
        .when(epidVerificationService)
        .getGroupCertSigma10(sigInfo);
    Mockito.doThrow(HttpClientErrorException.class)
        .when(epidVerificationService)
        .getGroupCertSigma11(sigInfo);

    epidMaterialService.getSigInfo(sigInfo);
  }

  @Test(expectedExceptions = InvalidSigInfoException.class)
  public void testGetEpidMaterialThrowExceptionWhenWrongPublicKeyType() throws Exception {
    epidMaterialService.getSigInfo(new SigInfo(PublicKeyType.NONE, GROUP_ID_11));
  }

  @Test(expectedExceptions = InvalidGroupIdException.class)
  public void testGetEpidMaterialThrowExceptionWhenWrongLength11() throws Exception {
    SigInfo sigInfo = new SigInfo(PublicKeyType.EPID_1_1, new byte[] {0x55});
    epidMaterialService.getSigInfo(sigInfo);
  }

  @Test(expectedExceptions = InvalidGroupIdException.class)
  public void testGetEpidMaterialThrowExceptionWhenWrongLength20() throws Exception {
    SigInfo sigInfo = new SigInfo(PublicKeyType.EPID_2_0, new byte[] {0x55});
    epidMaterialService.getSigInfo(sigInfo);
  }
}
