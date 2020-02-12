// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services.errormessage;

import java.text.MessageFormat;
import java.util.Optional;
import javax.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.enums.ErrorCodes;
import org.sdo.rendezvous.logging.utils.LoggingUtils;
import org.sdo.rendezvous.model.MessageTypeNames;
import org.sdo.rendezvous.model.requests.ErrorMessageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ErrorMessageLogger {

  /**
   * Logs error message whenever processing cannot be continued by Device.
   *
   * @param errorMessage the error message instance sent by Device if something wrong happened
   * @param sanitizedMessage a sanitized message - check the sanitizeMessage method from class
   *     MessageSanitizer
   * @see MessageSanitizer
   */
  public void logInputErrorMessage(
      ErrorMessageRequest errorMessage, Optional<String> sanitizedMessage) {
    String logMsg =
        MessageFormat.format(
            "Error request received. Protocol version: {0}. Request data: [ec(error"
                + " code)={1}({2}), emsg(message type)={3}({4}), em(error message)={5}]",
            /* {0} */ LoggingUtils.getProtocolVersionFromUrl(),
            /* {1} */ errorMessage.getErrorCode(),
            /* {2} */ ErrorCodes.getDescriptionById((short) errorMessage.getErrorCode()),
            /* {3} */ errorMessage.getResumeCode(),
            /* {4} */ MessageTypeNames.getEmsgNameById(errorMessage.getResumeCode()),
            /* {5} */ sanitizedMessage
                .map(
                    s ->
                        getSanitizedErrorMessageToLog(
                            "\"" + s + "\"", errorMessage.getErrorMessage()))
                .orElseGet(() -> "\"" + errorMessage.getErrorMessage() + "\""));

    log.info(LoggingUtils.CUSTOMER, logMsg);
  }

  /** Logs the information, that request processing finished. */
  public void logOutputErrorMessage() {
    log.info(
        LoggingUtils.CUSTOMER,
        "Error request processing finished. Sending response with HTTP status code {}",
        HttpStatus.OK.value());
  }

  private String getSanitizedErrorMessageToLog(String sanitizedEm, String originalEm) {
    return "[sanitized(illegal characters replaced with '#')="
        + sanitizedEm
        + ", original(BASE64)="
        + DatatypeConverter.printBase64Binary(originalEm.getBytes())
        + "]";
  }
}
