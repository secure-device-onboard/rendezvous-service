// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.exceptions.handler;

import com.fasterxml.jackson.core.JsonParseException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sdo.rendezvous.enums.ErrorCodes;
import org.sdo.rendezvous.exceptions.InvalidIpAddressException;
import org.sdo.rendezvous.exceptions.InvalidSigInfoException;
import org.sdo.rendezvous.exceptions.SdoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DefaultSdoExceptionHandlerTest {

  @Mock HttpServletResponse response;
  @Mock private DefaultErrorResponseHandler errorResponseHandler;
  private DefaultSdoExceptionHandler defaultSdoExceptionHandler;

  @BeforeMethod
  private void beforeMethod() {
    MockitoAnnotations.initMocks(this);
    defaultSdoExceptionHandler = new DefaultSdoExceptionHandler(errorResponseHandler);
  }

  @Test
  public void testValidHandleException() throws IOException {
    Exception exception = new InvalidSigInfoException("Test");
    Mockito.when(errorResponseHandler.handleException(exception, response))
        .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    ResponseEntity<String> result =
        defaultSdoExceptionHandler.handleAllExceptions(exception, response);
    Assert.assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST);
  }

  @Test
  public void testHandleDeserializationErrorJsonParseException() throws Exception {

    HttpMessageNotReadableException exception = Mockito.mock(HttpMessageNotReadableException.class);
    Mockito.when(exception.getCause()).thenReturn(Mockito.mock(JsonParseException.class));

    ResponseEntity<String> expectedResponse =
        new ResponseEntity<>(
            "{\"ec\":100,\"emsg\":255,\"em\":\"Message body is structurally invalid.\"}",
            HttpStatus.BAD_REQUEST);
    Mockito.when(
            errorResponseHandler.handleException(
                new SdoException(
                    "Message body is structurally invalid.", ErrorCodes.MESSAGE_BODY_ERROR),
                response))
        .thenReturn(expectedResponse);
    ResponseEntity<String> result =
        defaultSdoExceptionHandler.handleDeserializationError(exception, response);
    Assert.assertEquals(result, expectedResponse);
  }

  @Test
  public void testHandleDeserializationErrorIpAddressInvalid() throws Exception {

    HttpMessageNotReadableException exception = Mockito.mock(HttpMessageNotReadableException.class);
    Mockito.when(exception.getCause()).thenReturn(Mockito.mock(JsonParseException.class));
    Mockito.when(exception.getCause().getCause())
        .thenReturn(Mockito.mock(InvalidIpAddressException.class));

    ResponseEntity<String> expectedResponse =
        new ResponseEntity<>(
            "{\"ec\":4,\"emsg\":255,\"em\":\"Invalid ip.\"}", HttpStatus.BAD_REQUEST);
    Mockito.when(
            errorResponseHandler.handleException(
                new SdoException("IP address is invalid.", ErrorCodes.INVALID_IP_ADDRESS),
                response))
        .thenReturn(expectedResponse);
    ResponseEntity<String> result =
        defaultSdoExceptionHandler.handleDeserializationError(exception, response);
    Assert.assertEquals(result, expectedResponse);
  }

  @Test
  public void testHandleDeserializationErrorExceptionNotSupported() throws Exception {
    HttpMessageNotReadableException exception = Mockito.mock(HttpMessageNotReadableException.class);
    Mockito.when(exception.getCause()).thenReturn(Mockito.mock(IllegalArgumentException.class));

    ResponseEntity<String> expectedResponse =
        new ResponseEntity<>(
            "{\"ec\":500,\"emsg\":255,\"em\":\"Generic error.\"", HttpStatus.BAD_REQUEST);
    Mockito.when(errorResponseHandler.handleException(exception, response))
        .thenReturn(expectedResponse);
    ResponseEntity<String> result =
        defaultSdoExceptionHandler.handleDeserializationError(exception, response);
    Assert.assertEquals(result, expectedResponse);
  }
}
