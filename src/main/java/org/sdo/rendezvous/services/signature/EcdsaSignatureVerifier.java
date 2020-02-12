// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services.signature;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.sdo.rendezvous.authenticators.AuthenticatorFactory;
import org.sdo.rendezvous.authenticators.IAuthenticator;
import org.sdo.rendezvous.enums.CurveType;
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
class EcdsaSignatureVerifier implements ISignatureVerifier {

  private final AuthenticatorFactory authenticatorFactory;

  @Override
  public void verify(ProveToSdoBody signatureBody, PubKey publicKey, byte[] signature)
      throws SdoException, IOException {
    log.info("Trying to verify ECDSA signature.");

    try {
      PublicKey ecdsaPublicKey = publicKey.asJavaPublicKey();
      byte[] payload = createEcdsaSignatureBodyMessage(signatureBody);
      IAuthenticator authenticator =
          authenticatorFactory.getAuthenticator(getKeyType(ecdsaPublicKey));
      authenticator.authenticate(signature, payload, ecdsaPublicKey);
    } catch (NoSuchAlgorithmException e) {
      log.debug("Cannot construct a valid public key. Error: {}", e.getMessage());
      throw new InvalidPublicKeyTypeException("Invalid public key.");
    }

    log.info("Successfully verified ECDSA signature.");
  }

  private PublicKeyType getKeyType(PublicKey publicKey) throws InvalidPublicKeyTypeException {
    int curveLength = ((BCECPublicKey) publicKey).getParameters().getCurve().getFieldSize();

    switch (CurveType.getEnum(curveLength)) {
      case Secp256r1:
        return PublicKeyType.ECDSA_P_256;
      case Secp384r1:
        return PublicKeyType.ECDSA_P_384;
      case None:
      default:
        throw new InvalidPublicKeyTypeException(
            String.format("Invalid ECDSA public key. Key length is %d.", curveLength));
    }
  }

  private byte[] createEcdsaSignatureBodyMessage(ProveToSdoBody signatureBody)
      throws JsonProcessingException {
    return JsonUtils.mapObjectToJson(signatureBody).getBytes();
  }
}
