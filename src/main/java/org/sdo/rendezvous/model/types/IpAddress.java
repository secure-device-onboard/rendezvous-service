// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.exceptions.InvalidIpAddressException;
import org.sdo.rendezvous.model.types.serialization.IpAddressDeserializer;
import org.sdo.rendezvous.model.types.serialization.IpAddressSerializer;

@Slf4j
@JsonDeserialize(using = IpAddressDeserializer.class)
@JsonSerialize(using = IpAddressSerializer.class)
@Getter
public class IpAddress {

  private InetAddress address;

  /**
   * Creates an InetAddress based on the provided IP address as bytes.
   *
   * @param ipBytes the raw IP address in network byte order
   * @throws InvalidIpAddressException if no IP address for the host could be found (wrapper for
   *     UnknownHostException)
   */
  public IpAddress(byte[] ipBytes) throws InvalidIpAddressException {
    try {
      address = InetAddress.getByAddress(ipBytes);
    } catch (UnknownHostException e) {
      log.error(String.format("Invalid IP address. Error: %s", e.getMessage()));
      throw new InvalidIpAddressException("IP address is invalid.");
    }
  }

  /**
   * Creates an InetAddress based on the provided IP address as String.
   *
   * @param ipAddress the specified host
   * @throws InvalidIpAddressException if no IP address for the host could be found (wrapper for
   *     UnknownHostException)
   */
  public IpAddress(String ipAddress) throws InvalidIpAddressException {
    try {
      address = InetAddress.getByName(ipAddress);
    } catch (UnknownHostException e) {
      log.error(String.format("Invalid IP address. Error: %s", e.getMessage()));
      throw new InvalidIpAddressException("IP address is invalid.");
    }
  }

  /**
   * Returns the raw IP address of this InetAddress object.
   *
   * @return the raw IP address of this object
   */
  public byte[] getAddressAsBytes() {
    return address.getAddress();
  }

  /**
   * Returns the IP address string in textual presentation.
   *
   * @return the raw IP address in a string format
   */
  public String getHostAddress() {
    return address.getHostAddress();
  }

  @Override
  public String toString() {
    return address.getHostAddress();
  }
}
