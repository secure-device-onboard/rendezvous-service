// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services.signature;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.authenticators.AuthenticatorFactory;
import org.sdo.rendezvous.authenticators.IAuthenticator;
import org.sdo.rendezvous.exceptions.InvalidPublicKeyTypeException;
import org.sdo.rendezvous.exceptions.SdoException;
import org.sdo.rendezvous.model.types.ProveToSdoBody;
import org.sdo.rendezvous.model.types.PubKey;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.sdo.rendezvous.utils.JsonUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
class OnDieEcdsaSignatureVerifier implements ISignatureVerifier {

  private final AuthenticatorFactory authenticatorFactory;

  @Override
  public void verify(ProveToSdoBody signatureBody, PubKey publicKey, byte[] signature)
      throws SdoException, IOException {
    log.info("Trying to verify OnDie ECDSA signature.");

    try {
      PublicKey onDieEcdsaPublicKey = publicKey.asJavaPublicKey();
      byte[] payload = createEcdsaSignatureBodyMessage(signatureBody);
      IAuthenticator authenticator =
          authenticatorFactory.getAuthenticator(getKeyType(onDieEcdsaPublicKey));
      authenticator.authenticate(signature, payload, onDieEcdsaPublicKey);
    } catch (NoSuchAlgorithmException e) {
      log.debug("Cannot construct a valid public key. Error: {}", e.getMessage());
      throw new InvalidPublicKeyTypeException("Invalid public key.");
    }

    log.info("Successfully verified On Die ECDSA signature.");
  }

  private PublicKeyType getKeyType(PublicKey publicKey) throws InvalidPublicKeyTypeException {
    return PublicKeyType.ONDIE_ECDSA_384;
  }

  private byte[] createEcdsaSignatureBodyMessage(ProveToSdoBody signatureBody)
      throws JsonProcessingException {
    return JsonUtils.mapObjectToJson(signatureBody).getBytes();
  }
}
