// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.StringWriter;
import java.io.Writer;
import org.sdo.rendezvous.model.types.PkNull;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PkNullSerializerTest {

  @Test
  public void testPkNullSerializerPositive() throws Exception {
    Writer writer = new StringWriter();
    JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
    SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();

    PkNullSerializer serializer = new PkNullSerializer();
    serializer.serialize(new PkNull(), jsonGenerator, serializerProvider);
    jsonGenerator.flush();

    Assert.assertEquals(writer.toString(), "[0,0,[0]]");
  }
}
