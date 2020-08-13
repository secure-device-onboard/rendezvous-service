// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.StringWriter;
import java.io.Writer;
import org.sdo.rendezvous.model.types.PkOnDieEcdsaNull;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PkOnDieEcdsaNullSerializerTest {

  @Test
  public void testPkOnDieEcdsaNullSerializerPositive() throws Exception {
    Writer writer = new StringWriter();
    JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
    SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();

    PkOnDieEcdsaNullSerializer serializer = new PkOnDieEcdsaNullSerializer();
    serializer.serialize(new PkOnDieEcdsaNull(), jsonGenerator, serializerProvider);
    jsonGenerator.flush();

    Assert.assertEquals(writer.toString(), "[93,5,[0]]");
  }
}
