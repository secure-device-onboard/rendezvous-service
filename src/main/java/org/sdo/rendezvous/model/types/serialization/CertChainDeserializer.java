// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.sdo.rendezvous.model.types.Cert;
import org.sdo.rendezvous.model.types.CertChain;
import org.sdo.rendezvous.model.types.CertType;

public class CertChainDeserializer extends JsonDeserializer<CertChain> {

  private static final int CERT_TYPE_INDEX = 0;
  private static final int NUM_ENTRIES_INDEX = 1;
  private static final int CERT_ARRAY_INDEX = 2;

  private static final int CERT_LENGTH_INDEX = 0;
  private static final int CERT_DATA_INDEX = 1;

  private static final int MAX_UINT8_VALUE = 255;
  private static final int MAX_UINT16_VALUE = 65535;

  @Override
  public CertChain deserialize(JsonParser jsonParser, DeserializationContext ctxt)
      throws IOException {

    ObjectCodec objectCodec = jsonParser.getCodec();
    JsonNode jsonNode = objectCodec.readTree(jsonParser);

    CertType certType = CertType.valueOf(jsonNode.get(CERT_TYPE_INDEX).asInt());

    int numEntries = jsonNode.get(NUM_ENTRIES_INDEX).asInt();
    int numParsedCerts = jsonNode.get(CERT_ARRAY_INDEX).size();
    if (numEntries <= 0 || numEntries > MAX_UINT8_VALUE || numEntries != numParsedCerts) {
      throw new JsonParseException(jsonParser, "Invalid number of entries in certificate chain.");
    }

    List<Cert> parsedCerts = new ArrayList<>();
    Iterator<JsonNode> certificatesIter = jsonNode.get(CERT_ARRAY_INDEX).elements();

    while (certificatesIter.hasNext()) {
      JsonNode certificateData = certificatesIter.next();
      int certLength = certificateData.get(CERT_LENGTH_INDEX).asInt();
      byte[] parsedCert = certificateData.get(CERT_DATA_INDEX).binaryValue();
      if (certLength <= 0 || certLength > MAX_UINT16_VALUE || certLength != parsedCert.length) {
        throw new JsonParseException(jsonParser, "Invalid certificate length.");
      }
      parsedCerts.add(new Cert(certLength, parsedCert));
    }

    return new CertChain(certType, (short) numEntries, parsedCerts);
  }
}
