// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services.op;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.authenticators.AuthenticatorFactory;
import org.sdo.rendezvous.authenticators.IAuthenticator;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.crypto.IHashGenerator;
import org.sdo.rendezvous.exceptions.InternalCryptoException;
import org.sdo.rendezvous.exceptions.InvalidOwnershipVoucherException;
import org.sdo.rendezvous.exceptions.InvalidPublicKeyTypeException;
import org.sdo.rendezvous.exceptions.InvalidSignatureException;
import org.sdo.rendezvous.exceptions.ResourceNotFoundException;
import org.sdo.rendezvous.model.beans.AllowlistHashes;
import org.sdo.rendezvous.model.types.HashType;
import org.sdo.rendezvous.model.types.Hmac;
import org.sdo.rendezvous.model.types.OwnershipVoucher;
import org.sdo.rendezvous.model.types.OwnershipVoucherEntry;
import org.sdo.rendezvous.model.types.OwnershipVoucherEntryBody;
import org.sdo.rendezvous.model.types.OwnershipVoucherHeader;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.sdo.rendezvous.utils.JsonUtils;
import org.sdo.rendezvous.utils.validators.OvPublicKeyTrustValidator;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
class OwnershipVoucherVerifier implements IOwnershipVoucherVerifier {

  private final RendezvousConfig rendezvousConfig;
  private final OvPublicKeyTrustValidator ovPublicKeyTrustValidator;
  private final AuthenticatorFactory authenticatorFactory;
  private final AllowlistHashes allowlistHashes;
  private final IHashGenerator hashGenerator;

  @Override
  public void verify(OwnershipVoucher ownershipVoucher) throws InvalidOwnershipVoucherException {
    if (!rendezvousConfig.isOpKeyVerification()) {
      return;
    }

    try {
      List<OwnershipVoucherEntry> ownershipVoucherEntries =
          Arrays.asList(ownershipVoucher.getOwnershipVoucherEntries());

      verifyOwnershipVoucherEntriesCount(
          ownershipVoucherEntries.size(), ownershipVoucher.getNumberOfEntries());
      verifyOwnershipVoucherEntriesCountAgainstMaxAllowedValue(ownershipVoucherEntries.size());

      OwnershipVoucherHeader header = ownershipVoucher.getOwnershipVoucherHeader();

      log.debug("Verifying trust of owner key in ownership voucher.");
      PublicKeyType previousKeyType = header.getManufacturerPublicKey().getPkType();
      verifyKeyType(previousKeyType);

      byte[] previousHashData =
          generatePreviousHashDataForFirstEntry(header, ownershipVoucher.getHmac());

      PublicKey previousKey = header.getManufacturerPublicKey().asJavaPublicKey();
      boolean isKeyAllowlist = validateAgainstAllowAndDenyList(previousKey);

      validateKeysEncodingAgainstHashAlgorithms(previousKeyType, ownershipVoucherEntries);

      for (OwnershipVoucherEntry entry : ownershipVoucherEntries) {

        log.debug(
            "Verifying trust of ownership voucher key for entry {}.",
            ownershipVoucherEntries.indexOf(entry));
        isKeyAllowlist |=
            validateAgainstAllowAndDenyList(
                entry.getOwnershipVoucherEntryBody().getPublicKey().asJavaPublicKey());
        byte[] previousHash =
            hashGenerator.generate(
                previousHashData,
                entry.getOwnershipVoucherEntryBody().getPreviousEntryHash().getHashType());
        assertHashesEquals(
            previousHash, entry.getOwnershipVoucherEntryBody().getPreviousEntryHash().getHash());

        final byte[] payload =
            JsonUtils.mapObjectToJson(
                    entry.getOwnershipVoucherEntryBody(), JsonInclude.Include.NON_NULL)
                .getBytes();
        final byte[] signature = entry.getSignature().getBytes();
        verifySignature(previousKey, previousKeyType, payload, signature);
        assertKeyTypeEquals(
            entry.getOwnershipVoucherEntryBody().getPublicKey().getPkType(), previousKeyType);

        previousKeyType = entry.getOwnershipVoucherEntryBody().getPublicKey().getPkType();
        previousKey = entry.getOwnershipVoucherEntryBody().getPublicKey().asJavaPublicKey();
        previousHashData =
            JsonUtils.mapObjectToJson(
                    entry.getOwnershipVoucherEntryBody(), JsonInclude.Include.NON_NULL)
                .getBytes();
      }

      if (!isKeyAllowlist) {
        log.error("No public key from ownership voucher is on the Allowlist.");
        throw new InvalidOwnershipVoucherException();
      }

    } catch (ResourceNotFoundException
        | NoSuchAlgorithmException
        | InternalCryptoException
        | JsonProcessingException e) {
      log.debug("Ownership voucher verification has failed. Error: {}", e.getMessage());
      throw new InvalidOwnershipVoucherException();
    }
  }

  private void validateKeysEncodingAgainstHashAlgorithms(
      PublicKeyType manufacturerPublicKeyType, List<OwnershipVoucherEntry> ownershipVoucherEntries)
      throws InvalidOwnershipVoucherException {
    PublicKeyType previousKeyType = manufacturerPublicKeyType;
    for (OwnershipVoucherEntry entry : ownershipVoucherEntries) {
      OwnershipVoucherEntryBody entryBody = entry.getOwnershipVoucherEntryBody();
      if (previousKeyType.isEcdsa()) {
        validateEcdsaPubkeyEncodingAgainstHashType(
            previousKeyType, entryBody.getPreviousEntryHash().getHashType());
      }
      previousKeyType = entryBody.getPublicKey().getPkType();
    }
  }

  private void validateEcdsaPubkeyEncodingAgainstHashType(
      PublicKeyType pubkeyType, HashType hashType) throws InvalidOwnershipVoucherException {
    if (pubkeyType.equals(PublicKeyType.ECDSA_P_256) && hashType.equals(HashType.SHA256)) {
      return;
    }
    if (pubkeyType.equals(PublicKeyType.ECDSA_P_384) && hashType.equals(HashType.SHA384)) {
      return;
    }
    log.error(
        "Public Key encoding does not match hash algorithm. Public key type is {}, "
            + "and hash type is {}",
        pubkeyType,
        hashType);
    throw new InvalidOwnershipVoucherException();
  }

  private byte[] generatePreviousHashDataForFirstEntry(
      OwnershipVoucherHeader header, Hmac ownershipVoucherHmac) throws JsonProcessingException {
    String jsonHeader = JsonUtils.mapObjectToJson(header, JsonInclude.Include.NON_NULL);
    String jsonHmac = JsonUtils.mapObjectToJson(ownershipVoucherHmac, JsonInclude.Include.NON_NULL);
    return (jsonHeader + jsonHmac).getBytes();
  }

  private void assertKeyTypeEquals(PublicKeyType first, PublicKeyType second)
      throws InvalidOwnershipVoucherException {
    if (first != second) {
      log.error(
          "One public key type in ownership voucher entry is different than "
              + "the type in the header.");
      throw new InvalidOwnershipVoucherException();
    }
  }

  private void verifyKeyType(PublicKeyType keyType) throws InvalidOwnershipVoucherException {
    if (keyType != PublicKeyType.RSA2048RESTR && !keyType.isEcdsa()) {
      log.error(
          "Invalid public key type in ownership voucher entry. Key type = {}.", keyType.name());
      throw new InvalidOwnershipVoucherException();
    }
  }

  private void verifySignature(
      PublicKey publicKey, PublicKeyType keyType, byte[] payload, byte[] signature)
      throws InvalidOwnershipVoucherException {
    try {
      IAuthenticator authenticator = authenticatorFactory.getAuthenticator(keyType);
      authenticator.authenticate(signature, payload, publicKey);
    } catch (InvalidPublicKeyTypeException
        | InternalCryptoException
        | InvalidSignatureException e) {
      log.debug(
          "Verification of signature in ownership voucher entry has failed. Error: {}",
          e.getMessage());
      throw new InvalidOwnershipVoucherException();
    }
  }

  private void assertHashesEquals(byte[] previous, byte[] current)
      throws InvalidOwnershipVoucherException {
    if (!Arrays.equals(previous, current)) {
      log.error(
          "Hashes in ownership voucher entry does not match. Previous {}, current {}",
          DatatypeConverter.printHexBinary(previous),
          DatatypeConverter.printHexBinary(current));
      throw new InvalidOwnershipVoucherException();
    }
  }

  private void verifyOwnershipVoucherEntriesCount(int actual, int expected)
      throws InvalidOwnershipVoucherException {
    if (actual == 0 || actual != expected) {
      log.error(
          "Invalid ownership voucher entries count! Actual = {}, expected = {}.", actual, expected);
      throw new InvalidOwnershipVoucherException();
    }
  }

  private void verifyOwnershipVoucherEntriesCountAgainstMaxAllowedValue(int count)
      throws InvalidOwnershipVoucherException {
    if (count > rendezvousConfig.getOwnershipVoucherMaxEntries()) {
      log.error(
          "Ownership voucher has to  many entries ({} found while maximum of {} is allowed).",
          count,
          rendezvousConfig.getOwnershipVoucherMaxEntries());
      throw new InvalidOwnershipVoucherException();
    }
  }

  private boolean validateAgainstAllowAndDenyList(PublicKey key)
      throws NoSuchAlgorithmException, InvalidOwnershipVoucherException {
    byte[] keyHash = hashGenerator.hashSha256(key.getEncoded());
    boolean isAllowlist = ovPublicKeyTrustValidator.verify(keyHash);
    if (isAllowlist) {
      allowlistHashes.add(keyHash);
    }
    return isAllowlist;
  }
}
