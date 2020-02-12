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
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.sdo.rendezvous.model.types.Hash;
import org.sdo.rendezvous.model.types.IpAddress;
import org.sdo.rendezvous.model.types.RendezvousInstr;

@AllArgsConstructor
public class RendezvousInstrDeserializer extends JsonDeserializer<RendezvousInstr> {

  private static final int RENDEZVOUS_INSTR_VALUES_INDEX = 1;

  @Override
  public RendezvousInstr deserialize(JsonParser jsonParser, DeserializationContext ctxt)
      throws IOException {

    ObjectCodec objectCodec = jsonParser.getCodec();
    JsonNode jsonNode = objectCodec.readTree(jsonParser);
    JsonNode values = jsonNode.get(RENDEZVOUS_INSTR_VALUES_INDEX);

    RendezvousInstr instr = new RendezvousInstr();

    Optional.ofNullable(values.get(RendezvousInstr.ONLY_TAG))
        .ifPresent(only -> instr.setOnly(only.asText()));

    Optional<JsonNode> ipAddressNode =
        Optional.ofNullable(values.get(RendezvousInstr.IP_ADDRESS_TAG));
    if (ipAddressNode.isPresent()) {
      instr.setIpAddress(objectCodec.treeToValue(ipAddressNode.get(), IpAddress.class));
    }

    Optional.ofNullable(values.get(RendezvousInstr.PORT_DEVICE_TAG))
        .ifPresent(portDevice -> instr.setPortDevice(portDevice.asInt()));

    Optional.ofNullable(values.get(RendezvousInstr.PORT_OWNER_TAG))
        .ifPresent(portOwner -> instr.setPortOwner(portOwner.asInt()));

    Optional.ofNullable(values.get(RendezvousInstr.DNS_TAG))
        .ifPresent(dns -> instr.setDns(dns.asText()));

    Optional<JsonNode> tlsServerCertHashNode =
        Optional.ofNullable(values.get(RendezvousInstr.TLS_SERVER_CERT_HASH_TAG));
    if (tlsServerCertHashNode.isPresent()) {
      instr.setTlsServerCertHash(objectCodec.treeToValue(tlsServerCertHashNode.get(), Hash.class));
    }

    Optional<JsonNode> tlsCaCertHashNode =
        Optional.ofNullable(values.get(RendezvousInstr.TLS_CA_CERT_HASH_TAG));
    if (tlsCaCertHashNode.isPresent()) {
      instr.setTlsCaCertHash(objectCodec.treeToValue(tlsCaCertHashNode.get(), Hash.class));
    }

    Optional.ofNullable(values.get(RendezvousInstr.USER_INPUT_TAG))
        .ifPresent(userInput -> instr.setUserInput(userInput.asInt()));

    Optional.ofNullable(values.get(RendezvousInstr.WIRELESS_SSID_TAG))
        .ifPresent(ssid -> instr.setWifiSsid(ssid.asText()));

    Optional.ofNullable(values.get(RendezvousInstr.WIRELESS_PASSWORD_TAG))
        .ifPresent(password -> instr.setWifiPassword(password.asText()));

    Optional.ofNullable(values.get(RendezvousInstr.WIRELESS_SECURITY_PROTOCOL_TAG))
        .ifPresent(secProtocol -> instr.setWifiSecurityProtocol(secProtocol.asText()));

    Optional.ofNullable(values.get(RendezvousInstr.MEDIUM_TAG))
        .ifPresent(medium -> instr.setMedium(medium.asText()));

    Optional.ofNullable(values.get(RendezvousInstr.PROTOCOL_TAG))
        .ifPresent(protocol -> instr.setProtocol(protocol.asText()));

    Optional.ofNullable(values.get(RendezvousInstr.DELAY_SEC_TAG))
        .ifPresent(delaySec -> instr.setDelaySec(delaySec.asInt()));

    if (instr.getLength() == 0) {
      throw new JsonParseException(jsonParser, "Instruction cannot be empty.");
    }

    return instr;
  }
}
