// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.authenticators;

import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.exceptions.InvalidPublicKeyTypeException;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.springframework.stereotype.Service;

/** Authenticator factory. */
@Service
@Slf4j
public class AuthenticatorFactory {

  /**
   * Returns the Authenticator instance specified by public key type - allowed authentication
   * algorithms SHA256withECDSA, SHA384withECDSA, SHA256withRSA.
   *
   * @param keyType public key type
   * @return return the authenticator specified by public key type
   * @throws InvalidPublicKeyTypeException if the pubic key type authenticator not supported
   */
  public Authenticator getAuthenticator(PublicKeyType keyType)
      throws InvalidPublicKeyTypeException {
    switch (keyType) {
      case ECDSA_P_256:
        return new AuthenticatorEcdsaWithSha256();
      case ECDSA_P_384:
        return new AuthenticatorEcdsaWithSha384();
      case RSA2048RESTR:
        return new AuthenticatorRsaWithSha256();
      default:
        log.debug("Unsupported public key type. Public key type function: {}", keyType.name());
        throw new InvalidPublicKeyTypeException(
            "Unsupported public key type for signature verification.");
    }
  }
}
