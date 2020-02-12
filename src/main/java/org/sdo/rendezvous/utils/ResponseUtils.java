// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import javax.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sdo.rendezvous.model.MpConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResponseUtils {

  public static void addTokenHeader(HttpServletResponse response, String token) {
    response.addHeader(HttpHeaders.AUTHORIZATION, MpConstants.BEARER_PREFIX + token);
  }

  /**
   * Creates a new ResponseEntity with the given status code and serialized Java object as a String.
   *
   * @param object the object to serialize
   * @param status the enumeration of HTTP status codes
   * @return the created ResponseEntity with serialized Java object as String
   * @throws JsonProcessingException if input contains invalid content of type JsonParser
   */
  public static ResponseEntity<String> createJsonResponse(Object object, HttpStatus status)
      throws JsonProcessingException {
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    return new ResponseEntity<>(JsonUtils.mapObjectToJson(object), responseHeaders, status);
  }
}
