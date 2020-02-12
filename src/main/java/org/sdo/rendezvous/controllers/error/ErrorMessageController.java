// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.controllers.error;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.enums.AttributeName;
import org.sdo.rendezvous.logging.aspects.generic.LogExecutionTime;
import org.sdo.rendezvous.model.SdoURLMapping;
import org.sdo.rendezvous.model.beans.DeviceInfo;
import org.sdo.rendezvous.model.requests.ErrorMessageRequest;
import org.sdo.rendezvous.model.types.Device;
import org.sdo.rendezvous.services.ErrorMessageService;
import org.sdo.rendezvous.utils.SessionAttributeHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** The class responsible for logging error message request sent by device. */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(
    value = {SdoURLMapping.TO_MSG_110, SdoURLMapping.TO_MSG_112, SdoURLMapping.TO_MSG_113},
    consumes = MediaType.APPLICATION_JSON_VALUE)
public class ErrorMessageController {

  private static final int ERROR_MESSAGE_MAX_LENGTH = 2000;
  private final DeviceInfo deviceInfo;
  private final ErrorMessageService errorMessageService;

  /**
   * Expose endpoints /mp/110/error, /mp/112/error, /mp/113/error for error message logging.
   *
   * @param error it is an error request message
   * @return HTTP status code
   */
  @LogExecutionTime
  @PostMapping(value = SdoURLMapping.ERROR_ENDPOINT)
  public ResponseEntity<String> logErrorMessage(@RequestBody @Valid ErrorMessageRequest error) {

    String errorMessage = error.getErrorMessage();
    error.setErrorMessage(
        errorMessage.substring(0, Math.min(errorMessage.length(), ERROR_MESSAGE_MAX_LENGTH)));
    Device device = new Device(deviceInfo.getGuid(), deviceInfo.getNonce());
    SessionAttributeHolder.setAttributeValue(AttributeName.GUID, device.getGuid());
    errorMessageService.logErrorMessageInput(error);
    errorMessageService.logErrorMessageResponse();

    return new ResponseEntity<>(HttpStatus.OK);
  }
}
