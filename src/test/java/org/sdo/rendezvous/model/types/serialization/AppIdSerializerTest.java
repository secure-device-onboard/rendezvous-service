// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.sdo.rendezvous.model.types.AppId;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AppIdSerializerTest {

  private static final int AI_TYPE = 2;
  private static final byte[] AI_BYTES = new byte[] {0x00, 0x01};

  private static final String EXPECTED_JSON = "[2,2,\"AAE=\"]";

  private AppId appId;
  private AppIdSerializer appIdSerializer;

  @BeforeMethod
  public void setUp() {
    appId = new AppId(AI_TYPE, AI_BYTES);
    appIdSerializer = new AppIdSerializer();
  }

  @Test
  public void testSerializePositive() throws IOException {
    Writer writer = new StringWriter();
    JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
    SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();
    appIdSerializer.serialize(appId, jsonGenerator, serializerProvider);
    jsonGenerator.flush();

    Assert.assertEquals(writer.toString(), EXPECTED_JSON);
  }
}
