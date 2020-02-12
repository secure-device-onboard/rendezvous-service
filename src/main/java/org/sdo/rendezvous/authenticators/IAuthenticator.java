// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.authenticators;

import java.security.PublicKey;
import org.sdo.rendezvous.exceptions.InternalCryptoException;
import org.sdo.rendezvous.exceptions.InvalidSignatureException;

/** This is interface for all authenticators, contains only one method - authenticate. */
public interface IAuthenticator {
  void authenticate(byte[] signature, byte[] payload, PublicKey publicKey)
      throws InternalCryptoException, InvalidSignatureException;
}
