// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.crypto.IHashGenerator;
import org.sdo.rendezvous.enums.DeviceAttestationMethod;
import org.sdo.rendezvous.exceptions.InvalidOwnershipVoucherException;
import org.sdo.rendezvous.exceptions.SdoException;
import org.sdo.rendezvous.model.database.VersionedTO1Data;
import org.sdo.rendezvous.model.log.to0.TO0TransactionInfo;
import org.sdo.rendezvous.model.requests.to0.OwnerSignRequest;
import org.sdo.rendezvous.model.types.Cert;
import org.sdo.rendezvous.model.types.OwnershipVoucherEntry;
import org.sdo.rendezvous.repositories.JedisRepository;
import org.sdo.rendezvous.repositories.TransferOwnership0Repository;
import org.sdo.rendezvous.utils.ByteConverter;
import org.sdo.rendezvous.utils.CertUtils;
import org.sdo.rendezvous.utils.JsonUtils;
import org.sdo.rendezvous.utils.validators.WaitSecondsValidator;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferOwnership0Service {

  private static final int DEVICE_CERT_INDEX = 0;

  private final TransferOwnership0Repository transferOwnership0Repository;
  private final CertUtils certUtils;
  private final WaitSecondsValidator wsValidator;
  private final JedisRepository jedisRepository;
  private final OwnerSignRequestVerifier ownerSignRequestVerifier;
  private final RendezvousConfig rendezvousConfig;
  private final IHashGenerator hashGenerator;

  /**
   * Returns how long the owner is willing to wait for a Device. The owner provides the Internet
   * location where it is waiting for a Device to connect. The wait time is negotiated with the
   * server.
   *
   * @param ownerSignRequest OwnerSignRequest instance, created from deserialized json, sent by new
   *     Owner Client
   * @param nonce 128-bit random number as an array of bytes, called a "challenge"
   * @return seconds how long the server will wait for the Device
   * @throws IOException if an I/O error occurs
   * @throws SdoException if the problem with validation owner sign request occurs
   */
  public int getWaitSeconds(OwnerSignRequest ownerSignRequest, byte[] nonce)
      throws IOException, SdoException {

    ownerSignRequestVerifier.verify(ownerSignRequest, nonce);
    final int waitSeconds = getNormalizedWaitSecond(ownerSignRequest.getTo0Data().getWaitSeconds());

    if (wsValidator.isRemovalRequired(waitSeconds)) {
      jedisRepository.deleteVersionedTO1Data(
          ByteConverter.getGuidFromByteArray(ownerSignRequest.getGuid()));
      log.info("WaitSeconds value is 0. TO1 data has been removed.");
    } else {
      VersionedTO1Data versionedTO1Data = new VersionedTO1Data(ownerSignRequest.getTo1Data());
      if (ownerSignRequestVerifier.getDeviceAttestationMethod(ownerSignRequest)
          == DeviceAttestationMethod.ECDSA) {
        Cert deviceCert = getDeviceCert(ownerSignRequest);
        versionedTO1Data.setEcdsaPublicKey(certUtils.getPubKeyBytesFromCert(deviceCert));
      }
      jedisRepository.setVersionedTO1Data(
          ByteConverter.getGuidFromByteArray(ownerSignRequest.getGuid()),
          JsonUtils.mapObjectToJson(versionedTO1Data),
          waitSeconds);
      saveTransactionInfo(ownerSignRequest);
      log.info(
          "Successfully registered data in TO0. Added TO1 data to database (TTL={}).", waitSeconds);
    }
    return waitSeconds;
  }

  private Cert getDeviceCert(OwnerSignRequest ownerSignRequest) {
    return ownerSignRequest
        .getTo0Data()
        .getOwnershipVoucher()
        .getCertificateChain()
        .getCerts()
        .get(DEVICE_CERT_INDEX);
  }

  private int getNormalizedWaitSecond(int waitSecond) {
    if (wsValidator.isAboveLimit(waitSecond)) {
      log.info(
          "WaitSeconds value exceeds the time limit. Changed to {}.",
          rendezvousConfig.getWaitSecondsLimit());
      return rendezvousConfig.getWaitSecondsLimit();
    }
    return waitSecond;
  }

  private void saveTransactionInfo(OwnerSignRequest ownerSignRequest)
      throws SdoException, JsonProcessingException {
    List<String> hashes = new ArrayList<>();
    try {
      for (OwnershipVoucherEntry ownershipVoucherEntry :
          ownerSignRequest.getTo0Data().getOwnershipVoucher().getOwnershipVoucherEntries()) {
        byte[] publicKey =
            ownershipVoucherEntry
                .getOwnershipVoucherEntryBody()
                .getPublicKey()
                .asJavaPublicKey()
                .getEncoded();
        byte[] hash = hashGenerator.hashSha256(publicKey);
        hashes.add(DatatypeConverter.printHexBinary(hash));
      }
    } catch (NoSuchAlgorithmException e) {
      throw new InvalidOwnershipVoucherException(e);
    }
    TO0TransactionInfo transactionInfo =
        new TO0TransactionInfo(
            ByteConverter.getGuidFromByteArray(ownerSignRequest.getGuid()),
            ownerSignRequest.getIpAddress().getHostAddress(),
            ownerSignRequest.getDns(),
            hashes);
    transferOwnership0Repository.setTransactionInfo(transactionInfo);
  }
}
