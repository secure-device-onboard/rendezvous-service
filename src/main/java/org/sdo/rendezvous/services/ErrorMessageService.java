// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services;

import lombok.RequiredArgsConstructor;
import org.sdo.rendezvous.model.requests.ErrorMessageRequest;
import org.sdo.rendezvous.services.errormessage.ErrorMessageLogger;
import org.sdo.rendezvous.services.errormessage.MessageSanitizer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ErrorMessageService {
  public final ErrorMessageLogger errorMessageLogger;
  public final MessageSanitizer messageSanitizer;

  public void logErrorMessageInput(ErrorMessageRequest errorMessage) {
    errorMessageLogger.logInputErrorMessage(
        errorMessage, messageSanitizer.sanitizeMessage(errorMessage.getErrorMessage()));
  }

  public void logErrorMessageResponse() {
    errorMessageLogger.logOutputErrorMessage();
  }
}
