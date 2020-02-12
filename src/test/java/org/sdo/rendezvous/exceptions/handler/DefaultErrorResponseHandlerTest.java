// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.exceptions.handler;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.testng.PowerMockTestCase;
import org.sdo.rendezvous.exceptions.InvalidEpidSignatureException;
import org.sdo.rendezvous.exceptions.InvalidGuidException;
import org.sdo.rendezvous.exceptions.InvalidJwtTokenException;
import org.sdo.rendezvous.exceptions.InvalidOwnerSignBodyException;
import org.sdo.rendezvous.exceptions.InvalidOwnershipVoucherException;
import org.sdo.rendezvous.exceptions.InvalidProveRequestException;
import org.sdo.rendezvous.exceptions.ResourceNotFoundException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DefaultErrorResponseHandlerTest extends PowerMockTestCase {

  @Mock HttpServletResponse response;

  @Mock ServletOutputStream stream;

  DefaultErrorResponseHandler handler;

  @BeforeMethod
  private void beforeMethod() {
    MockitoAnnotations.initMocks(this);
    handler = new DefaultErrorResponseHandler();
  }

  @Test
  public void testHandleException() throws Exception {
    when(response.getOutputStream()).thenReturn(stream);
    handler.handleException(new Exception(), response);
    verify(response).setStatus(SC_BAD_REQUEST);
    verify(stream).write("{\"ec\":500,\"emsg\":255,\"em\":\"Generic error.\"}".getBytes());
  }

  @Test
  public void testHandleInvalidJwTokenException() throws Exception {
    when(response.getOutputStream()).thenReturn(stream);
    handler.handleException(
        new InvalidJwtTokenException(
            "JWT token is missing or authorization header value does not start with 'Bearer '."),
        response);
    verify(response).setStatus(SC_BAD_REQUEST);
    verify(stream)
        .write(
            ("{\"ec\":1,\"emsg\":255,\"em\":\"JWT token is missing or authorization header value "
                    + "does not start with 'Bearer '.\"}")
                .getBytes());
  }

  @Test
  public void testHandleInvalidJwTokenExceptionWithMsg() throws Exception {
    when(response.getOutputStream()).thenReturn(stream);
    handler.handleException(
        new InvalidJwtTokenException(InvalidJwtTokenException.JWT_EXPIRED), response);
    verify(response).setStatus(SC_BAD_REQUEST);
    verify(stream).write("{\"ec\":1,\"emsg\":255,\"em\":\"JWT token has expired.\"}".getBytes());
  }

  @Test
  public void testHandleInvalidOwnershipException() throws Exception {
    when(response.getOutputStream()).thenReturn(stream);
    handler.handleException(
        new InvalidOwnershipVoucherException("Ownership voucher is invalid."), response);
    verify(response).setStatus(SC_BAD_REQUEST);
    verify(stream)
        .write("{\"ec\":2,\"emsg\":255,\"em\":\"Ownership voucher is invalid.\"}".getBytes());
  }

  @Test
  public void testHandleInvalidOwnerSignBodyException() throws Exception {
    when(response.getOutputStream()).thenReturn(stream);
    handler.handleException(
        new InvalidOwnerSignBodyException("Signature of owner message is invalid."), response);
    verify(response).setStatus(SC_BAD_REQUEST);
    verify(stream)
        .write(
            "{\"ec\":3,\"emsg\":255,\"em\":\"Signature of owner message is invalid.\"}".getBytes());
  }

  @Test
  public void testHandleInvalidGuidException() throws Exception {
    when(response.getOutputStream()).thenReturn(stream);
    handler.handleException(new InvalidGuidException("GUID is invalid."), response);
    verify(response).setStatus(SC_BAD_REQUEST);
    verify(stream).write("{\"ec\":5,\"emsg\":255,\"em\":\"GUID is invalid.\"}".getBytes());
  }

  @Test
  public void testHandleResourceNotFoundException() throws Exception {
    when(response.getOutputStream()).thenReturn(stream);
    handler.handleException(
        new ResourceNotFoundException(
            "Not found owner connection info for guid: 00000000-0000-0000-0000-000000000001."),
        response);
    verify(response).setStatus(SC_BAD_REQUEST);
    verify(stream)
        .write(
            ("{\"ec\":6,\"emsg\":255,\"em\":\"Not found owner connection info for guid: "
                    + "00000000-0000-0000-0000-000000000001.\"}")
                .getBytes());
  }

  @Test
  public void testHandleInvalidProveRequestException() throws Exception {
    when(response.getOutputStream()).thenReturn(stream);
    handler.handleException(
        new InvalidProveRequestException("Nonces in request and JWT don't match."), response);
    verify(response).setStatus(SC_BAD_REQUEST);
    verify(stream)
        .write(
            "{\"ec\":101,\"emsg\":255,\"em\":\"Nonces in request and JWT don't match.\"}"
                .getBytes());
  }

  @Test
  public void testHandleInvalidEpidSignatureException() throws Exception {
    when(response.getOutputStream()).thenReturn(stream);
    handler.handleException(
        new InvalidEpidSignatureException("Signature verification failed."), response);
    verify(response).setStatus(SC_BAD_REQUEST);
    verify(stream)
        .write("{\"ec\":101,\"emsg\":255,\"em\":\"Signature verification failed.\"}".getBytes());
  }
}
