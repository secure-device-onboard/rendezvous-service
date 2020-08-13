// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services.signature;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.exceptions.InvalidPublicKeyTypeException;
import org.sdo.rendezvous.exceptions.SdoException;
import org.sdo.rendezvous.model.types.ProveToSdoBody;
import org.sdo.rendezvous.model.types.PubKey;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignatureVerifier implements ISignatureVerifier {

  private final EpidSignatureVerifier epidSignatureVerifier;
  private final EcdsaSignatureVerifier ecdsaSignatureVerifier;
  private final OnDieEcdsaSignatureVerifier onDieEcdsaSignatureVerifier;
  private final RendezvousConfig rendezvousConfig;

  @Override
  public void verify(ProveToSdoBody signatureBody, PubKey publicKey, byte[] signature)
      throws SdoException, IOException {
    if (rendezvousConfig.isSignatureVerification()) {
      if (publicKey.getPkType().isNone()) {
        ecdsaSignatureVerifier.verify(signatureBody, publicKey, signature);
      } else if (publicKey.getPkType().isEpid()) {
        epidSignatureVerifier.verify(signatureBody, publicKey, signature);
      } else if (publicKey.getPkType().isOnDieEcdsa()) {
        onDieEcdsaSignatureVerifier.verify(signatureBody, publicKey, signature);
      } else {
        throw new InvalidPublicKeyTypeException(
            String.format(
                "Cannot validate signature using this type of key. Key type: %s.",
                publicKey.getPkType()));
      }
    }
  }
}
