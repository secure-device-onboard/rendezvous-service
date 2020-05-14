// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.controllers;

import javax.xml.bind.DatatypeConverter;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.testng.PowerMockTestCase;
import org.sdo.rendezvous.controllers.error.ErrorMessageController;
import org.sdo.rendezvous.logging.utils.LoggingUtils;
import org.sdo.rendezvous.model.beans.DeviceInfo;
import org.sdo.rendezvous.model.requests.ErrorMessageRequest;
import org.sdo.rendezvous.services.ErrorMessageService;
import org.sdo.rendezvous.utils.SessionAttributeHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
@PrepareForTest({RequestContextHolder.class, SessionAttributeHolder.class, LoggingUtils.class})
public class ErrorMessageControllerTest extends PowerMockTestCase {

  private static final String NONCE =
      "E2FDCC410C84F8CC7F26FF6745D02F4BCF0295174445F125C90E309C2F0BF5EDE39D907CFE204643AF9D07C8"
          + "32AC4C52CA32848116DAFE7AABC36D4B6F66EA0503922FD928543E060F3982FF5D7FBBEA";

  private static final String GUID = "AA51212120165121FC1221231ACFAA51";
  private static final int EC = 100;
  private static final short EMSG = 100;
  private static final String EM = "Error has occurred";

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ErrorMessageRequest request;

  @Mock private DeviceInfo deviceInfo;

  @Mock private ErrorMessageService errorMessageService;

  @InjectMocks private ErrorMessageController controller;

  @BeforeMethod
  public void setUp() {
    PowerMockito.mockStatic(RequestContextHolder.class);
    PowerMockito.mockStatic(SessionAttributeHolder.class);
    PowerMockito.mockStatic(LoggingUtils.class);
    MockitoAnnotations.initMocks(this);
    Mockito.when(request.getErrorCode()).thenReturn(EC);
    Mockito.when(request.getResumeCode()).thenReturn(EMSG);
    Mockito.when(request.getErrorMessage()).thenReturn(EM);
    Mockito.when(request.toString())
        .thenReturn(
            String.format(
                "{\"ec\":%d,\"emsg\":%d,\"em\":\"%s\"}",
                EC, EMSG, EM.substring(0, Math.min(EM.length(), 2000))));
    controller = new ErrorMessageController(deviceInfo, errorMessageService);
  }

  @Test
  public void testLogErrorMessage() {
    Mockito.when(deviceInfo.getGuid()).thenReturn(DatatypeConverter.parseHexBinary(GUID));
    Mockito.when(deviceInfo.getNonce()).thenReturn(DatatypeConverter.parseHexBinary(NONCE));
    ResponseEntity<String> response = controller.logErrorMessage(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
  }
}
