// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.authenticators;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.sdo.rendezvous.exceptions.InternalCryptoException;
import org.sdo.rendezvous.exceptions.InvalidSignatureException;

/** Abstract base class for implementations returned by AuthenticatorFactory. */
@Slf4j
public abstract class Authenticator implements IAuthenticator {

  @Override
  public void authenticate(byte[] signature, byte[] payload, PublicKey publicKey)
      throws InternalCryptoException, InvalidSignatureException {
    try {
      Signature verifier =
          Signature.getInstance(getSignatureAlgorithm(), new BouncyCastleProvider());
      verifier.initVerify(publicKey);
      verifier.update(payload);
      boolean isSignatureValid = verifier.verify(signature);
      if (!isSignatureValid) {
        throw new InvalidSignatureException();
      }
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      log.debug("Could not authenticate request. Error: {}", e.getMessage());
      throw new InternalCryptoException("Could not authenticate request.");
    } catch (SignatureException e) {
      log.debug("Signature verification failed with SignatureException. Error: {}", e.getMessage());
      throw new InvalidSignatureException();
    }
  }

  protected abstract String getSignatureAlgorithm();
}
