// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.database.serialization;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.bind.DatatypeConverter;
import org.mockito.MockitoAnnotations;
import org.sdo.rendezvous.exceptions.InvalidIpAddressException;
import org.sdo.rendezvous.model.database.VersionedTo1Data;
import org.sdo.rendezvous.model.types.Hash;
import org.sdo.rendezvous.model.types.HashType;
import org.sdo.rendezvous.model.types.IpAddress;
import org.sdo.rendezvous.model.types.OwnerSignTo1Data;
import org.sdo.rendezvous.model.types.OwnerSignTo1DataBody;
import org.sdo.rendezvous.model.types.PkNull;
import org.sdo.rendezvous.model.types.Signature;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class VersionedTo1DataSerializerTest {

  private VersionedTo1Data versionedTO1Data;
  private VersionedTo1WithEcdsaSerializer versionedTO1WithEcdsaSerializer;

  private OwnerSignTo1Data to1Data;

  /**
   * Variable initialization.
   * @throws InvalidIpAddressException for handling exception thrown while
   *                                   configuring TO1 data.
   */
  @BeforeMethod
  public void setUp() throws InvalidIpAddressException {
    MockitoAnnotations.initMocks(this);
    setTo1Data();
    versionedTO1WithEcdsaSerializer = new VersionedTo1WithEcdsaSerializer();
  }

  @Test
  public void testSerializePositiveWithoutEcdsa() throws IOException, InvalidIpAddressException {
    // given
    Writer writer = new StringWriter();
    JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
    ObjectMapper mapper = getObjectMapper();
    jsonGenerator.setCodec(getObjectMapper());
    SerializerProvider serializerProvider = mapper.getSerializerProvider();
    String expectedValue =
        "{\"modelVersion\":\"2\",\"bo\":{\"i1\":[4,\"fwAAAQ==\"],\"dns1\":\"du.da.com\","
            + "\"port1\":0,\"to0dh\":[32,8,\"Ieo/tjn1HaZaemKLUng74GU9VMnsrhZEHMuMLhA14Tg=\"]},"
            + "\"pk\":[0,0,[0]],\"sg\":[4,\"AAAAAQ==\"]}";

    // when
    versionedTO1WithEcdsaSerializer.serialize(versionedTO1Data, jsonGenerator, serializerProvider);
    jsonGenerator.flush();

    // then
    Assert.assertEquals(writer.toString(), expectedValue);
  }

  @Test
  public void testSerializePositiveWithEcdsa() throws IOException, InvalidIpAddressException {
    // given
    Writer writer = new StringWriter();
    JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
    ObjectMapper mapper = getObjectMapper();
    jsonGenerator.setCodec(getObjectMapper());
    SerializerProvider serializerProvider = mapper.getSerializerProvider();
    versionedTO1Data.setEcdsaPublicKey(DatatypeConverter.parseHexBinary("00000003"));
    String expectedValue =
        "{\"modelVersion\":\"2\",\"bo\":{\"i1\":[4,\"fwAAAQ==\"],\"dns1\":\"du.da.com\","
            + "\"port1\":0,\"to0dh\":[32,8,\"Ieo/tjn1HaZaemKLUng74GU9VMnsrhZEHMuMLhA14Tg=\"]},"
            + "\"pk\":[0,0,[0]],\"sg\":[4,\"AAAAAQ==\"],\"ecdsaPublicKey\":\"AAAAAw==\"}";

    // when
    versionedTO1WithEcdsaSerializer.serialize(versionedTO1Data, jsonGenerator, serializerProvider);
    jsonGenerator.flush();

    // then
    Assert.assertEquals(writer.toString(), expectedValue);
  }

  private void setTo1Data() throws InvalidIpAddressException {
    to1Data = new OwnerSignTo1Data();
    to1Data.setPubKey(new PkNull());
    to1Data.setBody(new OwnerSignTo1DataBody());
    to1Data.getBody().setDns("du.da.com");
    to1Data.getBody().setIpAddress(new IpAddress("127.0.0.1"));
    to1Data
        .getBody()
        .setTo0DataHash(
            new Hash(
                HashType.SHA256,
                DatatypeConverter.parseHexBinary(
                    "21EA3FB639F51DA65A7A628B52783BE0653D54C9ECAE16441CCB8C2E1035E138")));
    to1Data.setSignature(new Signature(DatatypeConverter.parseHexBinary("00000001")));
    versionedTO1Data = new VersionedTo1Data(to1Data);
  }

  private ObjectMapper getObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    mapper.registerModule(module);
    return mapper;
  }
}
