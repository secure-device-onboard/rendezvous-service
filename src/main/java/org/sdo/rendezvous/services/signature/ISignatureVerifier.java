// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services.signature;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.sdo.rendezvous.exceptions.SdoException;
import org.sdo.rendezvous.model.types.ProveToSdoBody;
import org.sdo.rendezvous.model.types.PubKey;

interface ISignatureVerifier {
  void verify(ProveToSdoBody signatureBody, PubKey publicKey, byte[] signature)
      throws SdoException, IOException, InvalidKeySpecException, NoSuchAlgorithmException;
}
