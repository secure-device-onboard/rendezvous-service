// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.authenticators;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Arrays;
import org.sdo.rendezvous.exceptions.InvalidSignatureException;

/** The implementation of abtract class Authenticator specified for SHA384withECDSA. */
public class AuthenticatorOnDieEcdsaWithSha384 extends Authenticator {

  private static final String SIGNATURE_ALGORITHM = "SHA384withOnDieECDSA";
  private static final int taskInfoLength = 36; // length of the taskinfo part of OnDie signature
  private static final int rLength = 48; // length of the r field part of OnDie signature
  private static final int sLength = 48; // length of the s field part of OnDie signature

  private static byte[] convertSignature(byte[] signature, byte[] taskInfo)
      throws IllegalArgumentException, IOException {
    if (taskInfo.length != taskInfoLength) {
      throw new IllegalArgumentException("taskinfo length is incorrect: " + taskInfo.length);
    }

    // Format for signature should be as follows:
    // 0x30 b1 0x02 b2 (vr) 0x02 b3 (vs)
    // The b1 = length of remaining bytes,
    // b2 = length of R value (vr), b3 = length of S value (vs)
    byte[] rvalue = Arrays.copyOfRange(signature, taskInfo.length, taskInfo.length + 48);
    byte[] svalue = Arrays.copyOfRange(signature, taskInfo.length + 48, taskInfo.length + 96);

    // format signature: if upper most bit is set then prepend with a 0x00 and increase length by 1
    boolean appendZeroToR = false;
    boolean appendZeroToS = false;
    if ((rvalue[0] & 0x80) != 0) {
      appendZeroToR = true;
    }
    if ((svalue[0] & 0x80) != 0) {
      appendZeroToS = true;
    }

    ByteArrayOutputStream adjSignature = new ByteArrayOutputStream();
    adjSignature.write(0x30);
    // total length of remaining bytes
    adjSignature.write(
        4 + (appendZeroToR ? rLength + 1 : rLength) + (appendZeroToS ? sLength + 1 : sLength));
    adjSignature.write(0x02);
    // R value
    if (appendZeroToR) {
      adjSignature.write(rLength + 1);
      adjSignature.write(0x00);
      adjSignature.write(rvalue);
    } else {
      adjSignature.write(rLength);
      adjSignature.write(rvalue);
    }
    adjSignature.write(0x02);
    // S value
    if (appendZeroToS) {
      adjSignature.write(sLength + 1);
      adjSignature.write(0x00);
      adjSignature.write(svalue);
    } else {
      adjSignature.write(sLength);
      adjSignature.write(svalue);
    }
    return adjSignature.toByteArray();
  }

  @Override
  protected String getSignatureAlgorithm() {
    return SIGNATURE_ALGORITHM;
  }

  @Override
  public void authenticate(byte[] signature, byte[] payload, PublicKey publicKey)
      throws InvalidSignatureException {

    byte[] adjSignature = new byte[0];

    // check minimum length (taskinfo + R + S)
    if (signature.length < (36 + 48 + 48)) {
      throw new InvalidSignatureException();
    }
    try {
      // first 36 bytes are always the taskInfo
      byte[] taskInfo = Arrays.copyOfRange(signature, 0, 36);

      // adjust the signed data
      // data-to-verify format is: [ task-info | nonce (optional) | data ]
      // First 36 bytes of signature is the taskinfo. This value must be prepended
      // to the signedData
      ByteArrayOutputStream adjSignedData = new ByteArrayOutputStream();
      adjSignedData.write(taskInfo);
      adjSignedData.write(payload);

      adjSignature = convertSignature(signature, taskInfo);

      Signature sig = Signature.getInstance("SHA384withECDSA");
      sig.initVerify(publicKey);
      sig.update(adjSignedData.toByteArray());

      if (!sig.verify(adjSignature)) {
        throw new InvalidSignatureException();
      }
    } catch (Exception ex) {
      throw new InvalidSignatureException();
    }
  }
}
