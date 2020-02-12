// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("serial")
public class MessageTypeNames {

  private static final String UNKNOWN_EMSG_ID = "Unknown Message ID";
  private static final Map<Integer, String> EMSG =
      Collections.unmodifiableMap(
          new HashMap<Integer, String>() {
            {
              put(20, "TO0.Hello");
              put(21, "TO0.HelloAck");
              put(22, "TO0.OwnerSign");
              put(25, "TO0.AcceptOwner");
              put(30, "TO1.HelloSDO");
              put(31, "TO1.HelloSDOAck");
              put(32, "TO1.ProveToSDO");
              put(33, "TO1.SDORedirect");
            }
          });

  public static String getEmsgNameById(int emsgId) {
    return Optional.ofNullable(EMSG.get(emsgId)).orElse(UNKNOWN_EMSG_ID);
  }
}
