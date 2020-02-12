// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.StringWriter;
import java.io.Writer;
import org.sdo.rendezvous.model.types.PKNull;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PKNullSerializerTest {

  @Test
  public void testPKNullSerializerPositive() throws Exception {
    Writer writer = new StringWriter();
    JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
    SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();

    PKNullSerializer serializer = new PKNullSerializer();
    serializer.serialize(new PKNull(), jsonGenerator, serializerProvider);
    jsonGenerator.flush();

    Assert.assertEquals(writer.toString(), "[0,0,[0]]");
  }
}
