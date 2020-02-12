// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sdo.rendezvous.model.requests.ErrorMessageRequest;
import org.sdo.rendezvous.services.errormessage.ErrorMessageLogger;
import org.sdo.rendezvous.services.errormessage.MessageSanitizer;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ErrorMessageServiceTest {

  @InjectMocks private ErrorMessageService errorMessageService;

  @Mock private ErrorMessageRequest errorMessageRequest;

  @Mock private ErrorMessageLogger errorMessageLogger;

  @Mock private MessageSanitizer messageSanitizer;

  @BeforeTest
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testLogErrorMessageResponse() {
    errorMessageService.logErrorMessageResponse();
    Mockito.verify(errorMessageLogger).logOutputErrorMessage();
  }

  @Test
  public void testLogErrorMessageInput() {
    Mockito.when(messageSanitizer.sanitizeMessage(any())).thenReturn(Optional.empty());
    errorMessageService.logErrorMessageInput(errorMessageRequest);
    Mockito.verify(errorMessageLogger).logInputErrorMessage(errorMessageRequest, Optional.empty());
  }
}
