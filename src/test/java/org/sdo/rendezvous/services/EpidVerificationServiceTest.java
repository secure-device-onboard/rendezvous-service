// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import javax.xml.bind.DatatypeConverter;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.enums.EpidVersion;
import org.sdo.rendezvous.http.HttpClient;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.sdo.rendezvous.model.types.SigInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@PrepareForTest(HttpClient.class)
public class EpidVerificationServiceTest extends PowerMockTestCase {

  private static final byte[] GID_EPID11 = new byte[] {0x00, 0x00, 0x00, 0x05};
  private static final byte[] GID_EPID20 =
      new byte[] {
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x05
      };
  private static final String VERIFICATION_HOST = "localhost";
  @Captor private ArgumentCaptor<String> argumentCaptor;

  private EpidVerificationService epidVerificationService;

  @Mock private HttpClient httpClient;

  @Mock private RendezvousConfig rendezvousConfig;

  private SigInfo sigInfo11;

  private SigInfo sigInfo20;

  @BeforeMethod
  public void beforeMethod() {
    MockitoAnnotations.initMocks(this);

    PowerMockito.mockStatic(HttpClient.class);
    Mockito.when(httpClient.doPost(any(), any())).thenReturn(new byte[] {});
    Mockito.when(rendezvousConfig.getVerificationServiceHost()).thenReturn(VERIFICATION_HOST);
    PowerMockito.when(HttpClient.buildUrl(any(), any())).thenReturn("");
    sigInfo11 = new SigInfo(PublicKeyType.EPID_1_1, GID_EPID11);
    sigInfo20 = new SigInfo(PublicKeyType.EPID_2_0, GID_EPID20);
    epidVerificationService = new EpidVerificationService(rendezvousConfig, httpClient);
  }

  @Test
  public void testVerifySignaturePositiveEpid11() throws Exception {
    byte[] message = new byte[] {};
    byte[] signature = new byte[] {};
    String proofEndPointEpid11 = "v1/epid11/proof";
    epidVerificationService.verifySignature(GID_EPID11, message, signature, proofEndPointEpid11);
  }

  @Test
  public void testVerifySignaturePositiveEpid20() throws Exception {
    byte[] message = new byte[] {};
    byte[] signature = new byte[] {};
    String proofEndPointEpid20 = "v1/epid20/proof";
    epidVerificationService.verifySignature(GID_EPID20, message, signature, proofEndPointEpid20);
  }

  @Test
  public void testGetLatestSigrlPositive() throws Exception {
    byte[] sigrl = DatatypeConverter.parseBase64Binary("00000000");
    Mockito.when(httpClient.doGet(anyString())).thenReturn(sigrl);
    byte[] expectedSigrl = epidVerificationService.getSigrl(sigInfo20, EpidVersion.EPID20);
    PowerMockito.verifyStatic(HttpClient.class);
    HttpClient.buildUrl(anyString(), argumentCaptor.capture());

    String expectedUrlPath = "v2/epid20/00000000000000000000000000000005/sigrl";
    Assert.assertEquals(sigrl, expectedSigrl);
    Assert.assertEquals(argumentCaptor.getValue(), expectedUrlPath);
  }

  @Test
  public void testGetEmptySigrlPositive() throws Exception {

    Mockito.when(httpClient.doGet(anyString())).thenReturn(null);

    byte[] expectedSigrl = epidVerificationService.getSigrl(sigInfo11, EpidVersion.EPID20);
    byte[] sigrl = new byte[] {};
    PowerMockito.verifyStatic(HttpClient.class);
    HttpClient.buildUrl(anyString(), argumentCaptor.capture());
    Assert.assertEquals(sigrl, expectedSigrl);

    String expectedUrlPath = "v2/epid20/00000005/sigrl";
    Assert.assertEquals(argumentCaptor.getValue(), expectedUrlPath);
  }

  @Test
  public void testGetGroupCertSigma10() throws Exception {
    byte[] sigrl = DatatypeConverter.parseBase64Binary("00000000");

    Mockito.when(httpClient.doGet(anyString())).thenReturn(sigrl);
    byte[] expectedSigrl = epidVerificationService.getGroupCertSigma10(sigInfo11);

    PowerMockito.verifyStatic(HttpClient.class);
    HttpClient.buildUrl(anyString(), argumentCaptor.capture());
    Assert.assertEquals(sigrl, expectedSigrl);
    String expectedUrlPath = "v2/epid11/00000005/pubkey.crt.bin";
    Assert.assertEquals(argumentCaptor.getValue(), expectedUrlPath);
  }

  @Test
  public void testGetPublicKeyPositive() throws Exception {
    byte[] publicKey = DatatypeConverter.parseBase64Binary("00000000");
    Mockito.when(httpClient.doGet(anyString())).thenReturn(publicKey);

    byte[] expectedPublicKey = epidVerificationService.getPublicKey(sigInfo20);

    PowerMockito.verifyStatic(HttpClient.class);
    HttpClient.buildUrl(anyString(), argumentCaptor.capture());
    String expectedUrlPath = "v2/epid20/00000000000000000000000000000005/pubkey";
    Assert.assertEquals(publicKey, expectedPublicKey);
    Assert.assertEquals(argumentCaptor.getValue(), expectedUrlPath);
  }

  @Test
  public void testGetGroupCertSigma11() throws Exception {
    byte[] publicKey = DatatypeConverter.parseBase64Binary("00000000");

    Mockito.when(httpClient.doGet(anyString())).thenReturn(publicKey);

    byte[] expectedPublicKey = epidVerificationService.getGroupCertSigma11(sigInfo11);
    PowerMockito.verifyStatic(HttpClient.class);
    HttpClient.buildUrl(anyString(), argumentCaptor.capture());
    Assert.assertEquals(publicKey, expectedPublicKey);

    String expectedUrlPath = "v2/epid11/00000005/pubkey.crt";
    Assert.assertEquals(argumentCaptor.getValue(), expectedUrlPath);
  }
}
