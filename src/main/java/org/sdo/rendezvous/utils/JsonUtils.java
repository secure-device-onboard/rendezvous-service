// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonUtils {
  private static final ObjectMapper mapper = new ObjectMapper();

  public static String mapObjectToJson(Object object) throws JsonProcessingException {
    ObjectWriter objectWriter = mapper.writer();
    return objectWriter.writeValueAsString(object);
  }

  /**
   * Method serializes any Java value as a String.
   *
   * @param object the object to serialize
   * @param include defines which properties of Java Beans are to be included in serialization
   * @return the serialized Java object as a String
   * @throws JsonProcessingException if input contains invalid content of type JsonParser
   */
  public static String mapObjectToJson(Object object, JsonInclude.Include include)
      throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(include);
    ObjectWriter objectWriter = mapper.writer();
    return objectWriter.writeValueAsString(object);
  }

  public static <T> T mapJsonToObject(String jsonString, Class<T> contentClass) throws IOException {
    return mapper.readValue(jsonString, contentClass);
  }
}
