// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.requests.to1;

import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.sdo.rendezvous.utils.ArrayByteBuilder;

@Getter
@Setter
@AllArgsConstructor
public class EpidVerificationRequest {

  private static final short BASENAME_SIZE = 0;

  private byte[] groupId;

  private byte[] msg;

  private byte[] epidSignature;

  /**
   * Returns request for Verification Service as an array of bytes.
   *
   * @return an array of bytes in form accepted by Verification Service
   * @throws IOException if an I/O error occurs
   */
  public byte[] toByteArray() throws IOException {

    return new ArrayByteBuilder()
        .append(groupId)
        .append((short) msg.length)
        .append(msg)
        .append(BASENAME_SIZE)
        .append(epidSignature)
        .build();
  }
}
