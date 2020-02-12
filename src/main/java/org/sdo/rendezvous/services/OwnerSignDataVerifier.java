// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.authenticators.Authenticator;
import org.sdo.rendezvous.authenticators.AuthenticatorFactory;
import org.sdo.rendezvous.crypto.IHashGenerator;
import org.sdo.rendezvous.exceptions.InternalCryptoException;
import org.sdo.rendezvous.exceptions.InvalidHashException;
import org.sdo.rendezvous.exceptions.InvalidNonceException;
import org.sdo.rendezvous.exceptions.InvalidOwnerSignBodyException;
import org.sdo.rendezvous.exceptions.InvalidPublicKeyTypeException;
import org.sdo.rendezvous.exceptions.InvalidSignatureException;
import org.sdo.rendezvous.exceptions.ResourceNotFoundException;
import org.sdo.rendezvous.model.requests.to0.OwnerSignRequest;
import org.sdo.rendezvous.model.types.Hash;
import org.sdo.rendezvous.model.types.OwnershipVoucher;
import org.sdo.rendezvous.model.types.OwnershipVoucherEntry;
import org.sdo.rendezvous.model.types.PubKey;
import org.sdo.rendezvous.utils.JsonUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class OwnerSignDataVerifier {

  private final AuthenticatorFactory authenticatorFactory;
  private final IHashGenerator hashGenerator;

  void verify(OwnerSignRequest ownerSignRequest, byte[] tokenNonce)
      throws InvalidOwnerSignBodyException, InternalCryptoException, InvalidNonceException,
          InvalidHashException, ResourceNotFoundException {
    verifyOwnerSignTO0Data(ownerSignRequest, tokenNonce);
    verifyOwnerSignTO1Data(ownerSignRequest);
  }

  private void verifyOwnerSignTO0Data(OwnerSignRequest ownerSignRequest, byte[] tokenNonce)
      throws InvalidOwnerSignBodyException, InvalidHashException, InvalidNonceException {
    try {
      Hash requestHash = ownerSignRequest.getTo1Data().getBody().getTo0DataHash();
      byte[] validHash =
          hashGenerator.generate(
              JsonUtils.mapObjectToJson(ownerSignRequest.getTo0Data(), JsonInclude.Include.NON_NULL)
                  .getBytes(),
              requestHash.getHashType());
      if (!Arrays.equals(validHash, requestHash.getHash())) {
        throw new InvalidHashException("TO0 data hash doesn't match to1d.to0dh from message body.");
      }
      if (!Arrays.equals(tokenNonce, ownerSignRequest.getTo0Data().getNonce())) {
        throw new InvalidNonceException("The nonce from JWT doesn't match the nonce from body.");
      }
    } catch (NoSuchAlgorithmException | JsonProcessingException e) {
      log.debug("Verifying owner sign to0 data failed. Error: {}", e.getMessage());
      throw new InvalidOwnerSignBodyException(e);
    }
  }

  private void verifyOwnerSignTO1Data(OwnerSignRequest ownerSignRequest)
      throws InvalidOwnerSignBodyException, InternalCryptoException, ResourceNotFoundException {
    try {
      OwnershipVoucher ownershipVoucher = ownerSignRequest.getTo0Data().getOwnershipVoucher();
      OwnershipVoucherEntry[] ownershipVoucherEntries =
          ownershipVoucher.getOwnershipVoucherEntries();
      OwnershipVoucherEntry ownerEntry =
          ownershipVoucherEntries[ownershipVoucherEntries.length - 1];

      PubKey ownerKey = ownerEntry.getOwnershipVoucherEntryBody().getPublicKey();
      byte[] payload =
          JsonUtils.mapObjectToJson(ownerSignRequest.getTo1Data().getBody()).getBytes();
      byte[] signature = ownerSignRequest.getTo1Data().getSignature().getBytes();

      Authenticator authenticator = authenticatorFactory.getAuthenticator(ownerKey.getPkType());
      authenticator.authenticate(signature, payload, ownerKey.asJavaPublicKey());
    } catch (JsonProcessingException
        | InvalidPublicKeyTypeException
        | InvalidSignatureException
        | NoSuchAlgorithmException e) {
      log.debug("Failed to verify signature of Owner Sign Body. Error: {}", e.getMessage());
      throw new InvalidOwnerSignBodyException();
    }
  }
}
