// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.exceptions.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.enums.AttributeName;
import org.sdo.rendezvous.exceptions.SdoException;
import org.sdo.rendezvous.model.responses.ErrorResponse;
import org.sdo.rendezvous.utils.JsonUtils;
import org.sdo.rendezvous.utils.SessionAttributeHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Component
class DefaultErrorResponseHandler {

  /**
   * Handles the exception and writes the response body encoded with application/json into
   * HttpServletResponse.
   *
   * @param e is a caught exception forwarded during method execution
   * @param response is an interface to provide HTTP-specific functionality, used as an output
   * @return response entity with status code
   * @throws IOException if an I/O error occurs
   */
  @ResponseBody
  ResponseEntity<String> handleException(Exception e, HttpServletResponse response)
      throws IOException {
    log.error(String.format("Exception caught. Returning 'Bad Request', %s", e.getMessage()));

    String responseBody;
    if (e instanceof SdoException) {
      responseBody = handleSdoException((SdoException) e);
    } else {
      responseBody = JsonUtils.mapObjectToJson(new ErrorResponse());
    }

    response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    response.getOutputStream().write(responseBody.getBytes());

    SessionAttributeHolder.removeAttribute(AttributeName.GUID);

    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  private String handleSdoException(SdoException e) throws JsonProcessingException {
    ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getMessage());
    return JsonUtils.mapObjectToJson(errorResponse);
  }
}
