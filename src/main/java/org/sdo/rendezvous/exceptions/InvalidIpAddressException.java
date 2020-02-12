// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.exceptions;

// This exception extends RuntimeException as a workaround for an inability to throw any checked
// exception from JSON
// deserializer other than IOException
@SuppressWarnings("serial")
public class InvalidIpAddressException extends RuntimeException {

  public InvalidIpAddressException(String msg) {
    super(msg);
  }
}
