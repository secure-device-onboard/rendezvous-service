// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.database.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.sdo.rendezvous.exceptions.InvalidIpAddressException;
import org.sdo.rendezvous.model.database.VersionedTo1Data;
import org.sdo.rendezvous.model.types.HashType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class VersionedTo1DataDeserializerTest {

  @Test
  public void testDeserializerPositiveWithEcdsa() throws IOException, InvalidIpAddressException {
    // given
    String json =
        "{\"modelVersion\":\"2\",\"bo\":{\"i1\":[4,\"fwAAAQ==\"],\"dns1\":\"du.da.com\","
            + "\"port1\":0,\"to0dh\":[32,8,\"Ieo/tjn1HaZaemKLUng74GU9VMnsrhZEHMuMLhA14Tg=\"]},"
            + "\"pk\":[0,0,[0]],\"sg\":[4,\"AAAAAQ==\"],\"ecdsaPublicKey\":\"AAAAAw==\"}";
    ObjectMapper objectMapper = new ObjectMapper();

    // when
    VersionedTo1Data versionedTO1Data = objectMapper.readValue(json, VersionedTo1Data.class);

    // then
    Assert.assertEquals(versionedTO1Data.getModelVersion(), "2");
    Assert.assertEquals(
        versionedTO1Data.getTo1Data().getSignature().getBytes(),
        new byte[] {0x00, 0x00, 0x00, 0x01});

    Assert.assertEquals(versionedTO1Data.getTo1Data().getBody().getDns(), "du.da.com");
    Assert.assertEquals(
        versionedTO1Data.getTo1Data().getBody().getIpAddress().getHostAddress(), "127.0.0.1");
    Assert.assertEquals(versionedTO1Data.getTo1Data().getBody().getPort(), 0);
    Assert.assertEquals(
        versionedTO1Data.getTo1Data().getBody().getTo0DataHash().getHashType(), HashType.SHA256);

    Assert.assertEquals(versionedTO1Data.getEcdsaPublicKey(), new byte[] {0x00, 0x00, 0x00, 0x03});
  }

  @Test
  public void testDeserializerPositiveWithoutEcdsa() throws IOException, InvalidIpAddressException {
    // given
    String json =
        "{\"modelVersion\":\"2\",\"bo\":{\"i1\":[4,\"fwAAAQ==\"],\"dns1\":\"du.da.com\","
            + "\"port1\":0,\"to0dh\":[32,8,\"Ieo/tjn1HaZaemKLUng74GU9VMnsrhZEHMuMLhA14Tg=\"]},"
            + "\"pk\":[0,0,[0]],\"sg\":[4,\"AAAAAQ==\"]}";

    ObjectMapper objectMapper = new ObjectMapper();

    // when
    VersionedTo1Data versionedTO1Data = objectMapper.readValue(json, VersionedTo1Data.class);

    // then
    Assert.assertEquals(versionedTO1Data.getModelVersion(), "2");
    Assert.assertEquals(
        versionedTO1Data.getTo1Data().getSignature().getBytes(),
        new byte[] {0x00, 0x00, 0x00, 0x01});

    Assert.assertEquals(versionedTO1Data.getTo1Data().getBody().getDns(), "du.da.com");
    Assert.assertEquals(
        versionedTO1Data.getTo1Data().getBody().getIpAddress().getHostAddress(), "127.0.0.1");
    Assert.assertEquals(versionedTO1Data.getTo1Data().getBody().getPort(), 0);
    Assert.assertEquals(
        versionedTO1Data.getTo1Data().getBody().getTo0DataHash().getHashType(), HashType.SHA256);

    Assert.assertNull(versionedTO1Data.getEcdsaPublicKey());
  }

  @Test
  public void testDeserializerPositiveAdditionalFields()
      throws IOException, InvalidIpAddressException {
    // given
    String json =
        "{\"modelVersion\":\"2\",\"bo\":{\"i1\":[4,\"fwAAAQ==\"],\"dns1\":\"du.da.com\","
            + "\"port1\":0,\"to0dh\":[32,8,\"Ieo/tjn1HaZaemKLUng74GU9VMnsrhZEHMuMLhA14Tg=\"]},"
            + "\"pk\":[0,0,[0]],\"sg\":[4,\"AAAAAQ==\"],"
            + "\"additionalField\":\"2\"}";

    ObjectMapper objectMapper = new ObjectMapper();

    // when
    objectMapper.readValue(json, VersionedTo1Data.class);

    // then
  }

  @Test
  public void testDeserializerPositiveWithoutModelVersion()
      throws IOException, InvalidIpAddressException {
    // given
    String json =
        "{\"bo\":{\"i1\":[4,\"fwAAAQ==\"],\"dns1\":\"du.da.com\","
            + "\"port1\":0,\"to0dh\":[32,8,\"Ieo/tjn1HaZaemKLUng74GU9VMnsrhZEHMuMLhA14Tg=\"]},"
            + "\"pk\":[0,0,[0]],\"sg\":[4,\"AAAAAQ==\"]}";

    ObjectMapper objectMapper = new ObjectMapper();

    // when
    VersionedTo1Data versionedTO1Data = objectMapper.readValue(json, VersionedTo1Data.class);

    // then
    Assert.assertEquals(versionedTO1Data.getModelVersion(), "1");
  }
}
