// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.controllers;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.controllers.transferownership.TransferOwnership0Controller;
import org.sdo.rendezvous.crypto.TO0JWTokenFactory;
import org.sdo.rendezvous.enums.AttributeName;
import org.sdo.rendezvous.model.beans.Nonce;
import org.sdo.rendezvous.model.requests.to0.OwnerSignRequest;
import org.sdo.rendezvous.services.TransferOwnership0Service;
import org.sdo.rendezvous.utils.SessionAttributeHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*"})
@PrepareForTest({RequestContextHolder.class, SessionAttributeHolder.class})
public class TransferOwnership0ControllerTest extends PowerMockTestCase {

  private static final byte[] NONCE =
      DatatypeConverter.parseHexBinary(
          "E2FDCC410C84F8CC7F26FF6745D02F4BCF0295174445F125C90E309C2F0BF5EDE39D907CFE204643"
              + "AF9D07C832AC4C52CA32848116DAFE7AABC36D4B6F66EA0503922FD928543E060F3982FF5D7FBBEA");
  private static final String HMAC_SECRET = "AA51212120165121FC1221231ACFAA51";

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private OwnerSignRequest request;

  @Mock private Nonce nonce;

  @Mock private TransferOwnership0Service transferOwnership0Service;

  @Mock private HttpServletResponse httpServletResponse;

  @Mock private RendezvousConfig rendezvousConfig;

  @Mock private TO0JWTokenFactory to0JwtokenFactory;

  private TransferOwnership0Controller controller;

  @BeforeMethod
  public void setUp() throws Exception {
    PowerMockito.mockStatic(RequestContextHolder.class);
    PowerMockito.mockStatic(SessionAttributeHolder.class);
    MockitoAnnotations.initMocks(this);
    controller =
        new TransferOwnership0Controller(
            nonce,
            transferOwnership0Service,
            httpServletResponse,
            rendezvousConfig,
            to0JwtokenFactory);
    Mockito.when(rendezvousConfig.getHmacSecret()).thenReturn(HMAC_SECRET);
  }

  @Test
  public void testHello() throws Exception {
    PowerMockito.when(SessionAttributeHolder.getAttributeValue(AttributeName.SESSION_ID))
        .thenReturn(DatatypeConverter.printHexBinary(NONCE));
    ResponseEntity<String> helloResponse = controller.hello();
    Assert.assertNotNull(helloResponse.getBody());
  }

  @Test
  public void testOwnerSign() throws Exception {
    Mockito.when(nonce.getValue()).thenReturn(NONCE);
    Mockito.when(transferOwnership0Service.getWaitSeconds(request, NONCE)).thenReturn(5);
    ResponseEntity<String> response = controller.ownerSign(request);
    Assert.assertEquals(response.getBody(), "{\"ws\":5}");
  }
}
