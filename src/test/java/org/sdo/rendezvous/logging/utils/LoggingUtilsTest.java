// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.logging.utils;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@PrepareForTest({RequestContextHolder.class, ServletRequestAttributes.class})
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*"})
public class LoggingUtilsTest extends PowerMockTestCase {

  private static final byte[] SHORT_ARRAY = parseHexBinary("ABABABAB");
  private static final byte[] LONG_ARRAY =
      parseHexBinary(
          String.join("", Collections.nCopies(16, "A"))
              + "BB"
              + String.join("", Collections.nCopies(16, "C")));

  @Mock private ServletRequestAttributes servletRequestAttributes;

  @Mock private HttpServletRequest httpServletRequest;

  /**
   * Variable initialization.
   */
  @BeforeMethod
  public void setUp() {
    PowerMockito.mockStatic(RequestContextHolder.class);
    MockitoAnnotations.initMocks(this);
    servletRequestAttributes = PowerMockito.mock(ServletRequestAttributes.class);
    httpServletRequest = PowerMockito.mock(HttpServletRequest.class);
    PowerMockito.when(RequestContextHolder.currentRequestAttributes())
        .thenReturn(servletRequestAttributes);
    PowerMockito.when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
  }

  @Test
  public void testGetEdgeBytesOfArray_shouldPrintFullArray() {
    Assert.assertEquals(LoggingUtils.getEdgeBytesOfArray(SHORT_ARRAY), printHexBinary(SHORT_ARRAY));
  }

  @Test
  public void testGetEdgeBytesOfArray_shouldPrintEdgeBytes() {
    Assert.assertEquals(
        LoggingUtils.getEdgeBytesOfArray(LONG_ARRAY),
        String.format(
            "(%s...%s, length=%d)",
            String.join("", Collections.nCopies(16, "A")),
            String.join("", Collections.nCopies(16, "C")),
            LONG_ARRAY.length));
  }

  @Test
  public void testGetProtocolVersionFromUrl_shouldReturnProtocolVersionForMsgEndpoint() {
    PowerMockito.when(httpServletRequest.getRequestURI()).thenReturn("/mp/112/msg/20");
    Assert.assertEquals(LoggingUtils.getProtocolVersionFromUrl(), "112");
  }

  @Test
  public void testGetProtocolVersionFromUrl_shouldReturnProtocolVersionForHealthEndpoint() {
    Mockito.when(httpServletRequest.getRequestURI()).thenReturn("/mp/110/health");
    Assert.assertEquals(LoggingUtils.getProtocolVersionFromUrl(), "110");
  }

  @Test
  public void testGetProtocolVersionFromUrl_shouldReturnUnknownForInvalidProtocolVersion() {
    String uriWithoutCorrectProtocolVersion = "/mp/10/msg/20";
    Mockito.when(httpServletRequest.getRequestURI()).thenReturn(uriWithoutCorrectProtocolVersion);
    Assert.assertEquals(LoggingUtils.getProtocolVersionFromUrl(), uriWithoutCorrectProtocolVersion);
  }
}
