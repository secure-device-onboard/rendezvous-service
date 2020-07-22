// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types.serialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.Map;
import org.sdo.rendezvous.model.requests.to1.ProveToSdoRequest;
import org.sdo.rendezvous.model.types.PkEpidEnc;
import org.sdo.rendezvous.model.types.PkNull;
import org.sdo.rendezvous.model.types.PkOnDieEcdsaNull;
import org.sdo.rendezvous.model.types.ProveToSdoBody;
import org.sdo.rendezvous.model.types.PubKey;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.sdo.rendezvous.model.types.Signature;

public class ProveToSdoRequestDeserializer extends JsonDeserializer<ProveToSdoRequest> {

  private static final int PUBLIC_KEY_TYPE_INDEX = 0;
  private static final Map<PublicKeyType, Class<? extends PubKey>> PUBLIC_KEYS_TYPES =
      ImmutableMap.of(
          PublicKeyType.EPID_1_0, PkEpidEnc.class,
          PublicKeyType.EPID_1_1, PkEpidEnc.class,
          PublicKeyType.EPID_2_0, PkEpidEnc.class,
          PublicKeyType.ONDIE_ECDSA_384, PkOnDieEcdsaNull.class,
          PublicKeyType.NONE, PkNull.class);

  @Override
  public ProveToSdoRequest deserialize(JsonParser jsonParser, DeserializationContext ctxt)
      throws IOException {
    ObjectCodec objectCodec = jsonParser.getCodec();
    JsonNode jsonNode = objectCodec.readTree(jsonParser);
    ObjectMapper mapper = new ObjectMapper();

    ProveToSdoRequest proveToSdoRequest = new ProveToSdoRequest();
    proveToSdoRequest.setProveToSdoBody(
        mapper.readValue(
            jsonNode.get(ProveToSdoRequest.PROVE_TO_MP_BODY_TAG).toString(), ProveToSdoBody.class));

    PublicKeyType publicKeyType =
        PublicKeyType.valueOf(
            jsonNode.get(ProveToSdoRequest.PUBLIC_KEY_TAG).get(PUBLIC_KEY_TYPE_INDEX).asInt());
    if (PUBLIC_KEYS_TYPES.containsKey(publicKeyType)) {
      proveToSdoRequest.setPublicKey(
          mapper.readValue(
              jsonNode.get(ProveToSdoRequest.PUBLIC_KEY_TAG).toString(),
              PUBLIC_KEYS_TYPES.get(publicKeyType)));
    } else {
      throw new JsonParseException(
          jsonParser,
          String.format(
              "Couldn't find implementation for public key type: %s.", publicKeyType.name()));
    }
    proveToSdoRequest.setSignature(
        mapper.readValue(
            jsonNode.get(ProveToSdoRequest.SIGNATURE_TAG).toString(), Signature.class));
    return proveToSdoRequest;
  }
}
