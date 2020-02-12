// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import lombok.NoArgsConstructor;
import org.sdo.rendezvous.model.types.serialization.RendezvousInstrDeserializer;
import org.sdo.rendezvous.model.types.serialization.RendezvousInstrSerializer;

@NoArgsConstructor
@JsonSerialize(using = RendezvousInstrSerializer.class)
@JsonDeserialize(using = RendezvousInstrDeserializer.class)
public class RendezvousInstr {

  public static final String ONLY_TAG = "only";
  public static final String IP_ADDRESS_TAG = "ip";
  public static final String PORT_DEVICE_TAG = "po";
  public static final String PORT_OWNER_TAG = "pow";
  public static final String DNS_TAG = "dn";
  public static final String TLS_SERVER_CERT_HASH_TAG = "sch";
  public static final String TLS_CA_CERT_HASH_TAG = "cch";
  public static final String USER_INPUT_TAG = "ui";
  public static final String WIRELESS_SSID_TAG = "ss";
  public static final String WIRELESS_PASSWORD_TAG = "pw";
  public static final String WIRELESS_SECURITY_PROTOCOL_TAG = "wsp";
  public static final String MEDIUM_TAG = "me";
  public static final String PROTOCOL_TAG = "pr";
  public static final String DELAY_SEC_TAG = "delaysec";

  private Map<String, Object> values = new TreeMap<>();

  public int getLength() {
    return values.size();
  }

  public Map<String, Object> getValues() {
    return Collections.unmodifiableMap(values);
  }

  public Optional<String> getOnly() {
    return Optional.ofNullable((String) values.get(ONLY_TAG));
  }

  public Optional<IpAddress> getIpAddress() {
    return Optional.ofNullable((IpAddress) values.get(IP_ADDRESS_TAG));
  }

  public Optional<Integer> getPortDevice() {
    return Optional.ofNullable((int) values.get(PORT_DEVICE_TAG));
  }

  public Optional<Integer> getPortOwner() {
    return Optional.ofNullable((int) values.get(PORT_OWNER_TAG));
  }

  public Optional<String> getDns() {
    return Optional.ofNullable((String) values.get(DNS_TAG));
  }

  public Optional<Hash> getTlsServerCertHash() {
    return Optional.ofNullable((Hash) values.get(TLS_SERVER_CERT_HASH_TAG));
  }

  public Optional<Hash> getCaCertHash() {
    return Optional.ofNullable((Hash) values.get(TLS_CA_CERT_HASH_TAG));
  }

  public Optional<Integer> getUserInput() {
    return Optional.ofNullable((int) values.get(USER_INPUT_TAG));
  }

  public Optional<String> getWifiSsid() {
    return Optional.ofNullable(((String) values.get(WIRELESS_SSID_TAG)));
  }

  public Optional<String> getWifiPassword() {
    return Optional.ofNullable((String) values.get(WIRELESS_PASSWORD_TAG));
  }

  public Optional<String> getWifiSecurityProtocol() {
    return Optional.ofNullable((String) values.get(WIRELESS_SECURITY_PROTOCOL_TAG));
  }

  public Optional<String> getMedium() {
    return Optional.ofNullable((String) values.get(MEDIUM_TAG));
  }

  public Optional<String> getProtocol() {
    return Optional.ofNullable((String) values.get(PROTOCOL_TAG));
  }

  public Optional<Integer> getDelaySec() {
    return Optional.ofNullable((int) values.get(DELAY_SEC_TAG));
  }

  public void setOnly(String only) {
    values.put(ONLY_TAG, only);
  }

  public void setIpAddress(IpAddress ip) {
    values.put(IP_ADDRESS_TAG, ip);
  }

  public void setPortDevice(int port) {
    values.put(PORT_DEVICE_TAG, port);
  }

  public void setPortOwner(int port) {
    values.put(PORT_OWNER_TAG, port);
  }

  public void setDns(String dns) {
    values.put(DNS_TAG, dns);
  }

  public void setTlsServerCertHash(Hash hash) {
    values.put(TLS_SERVER_CERT_HASH_TAG, hash);
  }

  public void setTlsCaCertHash(Hash hash) {
    values.put(TLS_CA_CERT_HASH_TAG, hash);
  }

  public void setUserInput(int userInput) {
    values.put(USER_INPUT_TAG, userInput);
  }

  public void setWifiSsid(String ssid) {
    values.put(WIRELESS_SSID_TAG, ssid);
  }

  public void setWifiPassword(String password) {
    values.put(WIRELESS_PASSWORD_TAG, password);
  }

  public void setWifiSecurityProtocol(String securityProtocol) {
    values.put(WIRELESS_SECURITY_PROTOCOL_TAG, securityProtocol);
  }

  public void setMedium(String medium) {
    values.put(MEDIUM_TAG, medium);
  }

  public void setProtocol(String protocol) {
    values.put(PROTOCOL_TAG, protocol);
  }

  public void setDelaySec(int delaySec) {
    values.put(DELAY_SEC_TAG, delaySec);
  }

  @Override
  public String toString() {
    HashMap<String, String> namesToDisplay = new HashMap<>();

    namesToDisplay.put(ONLY_TAG, "(only for)");
    namesToDisplay.put(IP_ADDRESS_TAG, "(ip address)");
    namesToDisplay.put(PORT_OWNER_TAG, "(port)");
    namesToDisplay.put(PORT_DEVICE_TAG, "(port owner)");
    namesToDisplay.put(DNS_TAG, "(dns name)");
    namesToDisplay.put(TLS_SERVER_CERT_HASH_TAG, "(tls server cert hash)");
    namesToDisplay.put(TLS_CA_CERT_HASH_TAG, "(tls CA cert hash)");
    namesToDisplay.put(USER_INPUT_TAG, "(user input)");
    namesToDisplay.put(WIRELESS_SSID_TAG, "(ssid)");
    namesToDisplay.put(WIRELESS_PASSWORD_TAG, "(wireless password)");
    namesToDisplay.put(WIRELESS_SECURITY_PROTOCOL_TAG, "(wireless security password)");
    namesToDisplay.put(MEDIUM_TAG, "(medium)");
    namesToDisplay.put(PROTOCOL_TAG, "(protocol)");
    namesToDisplay.put(DELAY_SEC_TAG, "(delay)");

    StringBuilder output = new StringBuilder();

    for (Map.Entry<String, Object> value : values.entrySet()) {
      output
          .append(value.getKey())
          .append(namesToDisplay.get(value.getKey()))
          .append("=")
          .append(value.getValue())
          .append(", ");
    }

    output.setLength(output.length() - 2);

    return "[" + output + "]";
  }
}
