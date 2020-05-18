// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.exceptions.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.enums.ErrorCodes;
import org.sdo.rendezvous.exceptions.InvalidIpAddressException;
import org.sdo.rendezvous.exceptions.SdoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class DefaultSdoExceptionHandler {

  private final DefaultErrorResponseHandler errorResponseHandler;

  @ResponseBody
  @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public void handle405(HttpRequestMethodNotSupportedException exception) {
    log.info(exception.getMessage());
  }

  @ResponseBody
  @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
  @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
  public void handle415(HttpMediaTypeNotSupportedException exception) {
    log.info(exception.getMessage());
  }

  /**
   * Handles the HttpMessageNotReadableException exception and writes the response body encoded with
   * application/json into HttpServletResponse.
   *
   * @param exception is a HttpMessageNotReadableException which is thrown and forwarded to the
   *     method when HttpMessageConverter.read(java.lang.Class &lt;\? extends T&gt;,
   *     org.springframework.http.HttpInputMessage) method fails
   * @param response is an interface to provide HTTP-specific functionality, used as an output
   * @return ResponseEntity with status code
   * @throws IOException if an I/O error occurs
   */
  @ResponseBody
  @ExceptionHandler({HttpMessageNotReadableException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> handleDeserializationError(
      HttpMessageNotReadableException exception, HttpServletResponse response) throws IOException {
    log.info(exception.getMessage());
    // Below logic is a workaround for the JsonDeserializer behaviour to wrap every exception thrown
    // within deserialize method
    if (exception.getCause().getCause() instanceof InvalidIpAddressException) {
      return errorResponseHandler.handleException(
          new SdoException("IP address is invalid.", ErrorCodes.INVALID_IP_ADDRESS), response);
    }
    if (exception.getCause() instanceof JsonProcessingException) {
      return errorResponseHandler.handleException(
          new SdoException("Message body is structurally invalid.", ErrorCodes.MESSAGE_BODY_ERROR),
          response);
    }
    return errorResponseHandler.handleException(exception, response);
  }

  @ResponseBody
  @ExceptionHandler({Exception.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> handleAllExceptions(
      Exception exception, HttpServletResponse response) throws IOException {
    log.info(exception.getMessage());
    return errorResponseHandler.handleException(exception, response);
  }
}
