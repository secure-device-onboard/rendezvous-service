// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils;

import static org.mockito.Mockito.mock;

import javax.servlet.http.HttpServletResponse;
import org.mockito.Mockito;
import org.sdo.rendezvous.model.types.Signature;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ResponseUtilsTest {

  @Test
  public void testAddTokenHeaderPositive() {
    HttpServletResponse response = mock(HttpServletResponse.class);
    ResponseUtils.addTokenHeader(response, "{TOKEN}");
    Mockito.verify(response).addHeader(HttpHeaders.AUTHORIZATION, "Bearer {TOKEN}");
  }

  @Test
  public void testCreateJsonResponsePositive() throws Exception {
    Signature sig = new Signature(new byte[] {0x11, 0x22, 0x33, 0x44});
    ResponseEntity<String> response = ResponseUtils.createJsonResponse(sig, HttpStatus.OK);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(response.getBody(), "[4,\"ESIzRA==\"]");
    Assert.assertEquals(
        response.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0),
        MediaType.APPLICATION_JSON_VALUE);
  }
}
