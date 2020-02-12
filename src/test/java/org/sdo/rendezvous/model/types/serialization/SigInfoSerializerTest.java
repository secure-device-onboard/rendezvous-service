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
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.sdo.rendezvous.model.types.SigInfo;
import org.sdo.rendezvous.utils.ArrayByteBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SigInfoSerializerTest {

  private static final byte[] SIGRL = new byte[] {0x00, 0x00};
  private static final byte[] PUBLICKEY = new byte[] {0x00, 0x00};

  private SigInfo sigInfo;
  private SigInfoSerializer sigInfoSerializer;

  @BeforeMethod
  public void setUp() throws IOException {
    byte[] sigInfoBytes =
        new ArrayByteBuilder()
            .append((short) SIGRL.length)
            .append(SIGRL)
            .append((short) PUBLICKEY.length)
            .append(PUBLICKEY)
            .build();
    sigInfo = new SigInfo(PublicKeyType.EPID_2_0, sigInfoBytes);
    sigInfoSerializer = new SigInfoSerializer();
  }

  @Test
  public void testSerializePositive() throws IOException {
    Writer writer = new StringWriter();
    JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
    SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();
    sigInfoSerializer.serialize(sigInfo, jsonGenerator, serializerProvider);
    jsonGenerator.flush();

    Assert.assertEquals(writer.toString(), "[92,8,\"AAIAAAACAAA=\"]");
  }
}
