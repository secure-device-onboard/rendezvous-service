// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.crypto.IHashGenerator;
import org.sdo.rendezvous.enums.DeviceAttestationMethod;
import org.sdo.rendezvous.exceptions.FailedCertChainVerificationException;
import org.sdo.rendezvous.exceptions.GuidDenylistException;
import org.sdo.rendezvous.exceptions.InvalidAttestationMethodException;
import org.sdo.rendezvous.exceptions.InvalidCertChainHashException;
import org.sdo.rendezvous.exceptions.InvalidGuidException;
import org.sdo.rendezvous.exceptions.SdoException;
import org.sdo.rendezvous.model.requests.to0.OwnerSignRequest;
import org.sdo.rendezvous.model.types.Hash;
import org.sdo.rendezvous.model.types.OwnershipVoucher;
import org.sdo.rendezvous.services.op.IOwnershipVoucherVerifier;
import org.sdo.rendezvous.utils.JsonUtils;
import org.sdo.rendezvous.utils.validators.CertChainValidator;
import org.sdo.rendezvous.utils.validators.GuidValidator;
import org.sdo.rendezvous.utils.validators.WaitSecondsValidator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
class OwnerSignRequestVerifier {

  private final GuidValidator guidValidator;
  private final CertChainValidator certChainValidator;
  private final DeviceAttestationService deviceAttestationService;
  private final IOwnershipVoucherVerifier ownershipVoucherVerifier;
  private final OwnerSignDataVerifier ownerSignDataVerifier;
  private final WaitSecondsValidator wsValidator;
  private final IHashGenerator hashGenerator;

  /**
   * Verifies owner sign request, including ownership voucher, guid, signatures.
   *
   * @param ownerSignRequest OwnerSignRequest instance, created from deserialized json, sent by new
   *     Owner Client
   * @param nonce 128-bit random number as an array of bytes, called a "challenge"
   * @throws SdoException if the problem with validation owner sign request occurs
   * @throws IOException if an I/O error occurs
   */
  void verify(OwnerSignRequest ownerSignRequest, byte[] nonce) throws SdoException, IOException {
    OwnershipVoucher ownershipVoucher = ownerSignRequest.getTo0Data().getOwnershipVoucher();

    verifyGuid(ownerSignRequest.getGuid());
    ownershipVoucherVerifier.verify(ownershipVoucher);
    log.info("Ownership Voucher successfully verified.");
    ownerSignDataVerifier.verify(ownerSignRequest, nonce);
    log.info("Owner sign body successfully verified.");

    DeviceAttestationMethod deviceAttestationMethod =
        deviceAttestationService.getDeviceAttestationMethod(ownershipVoucher);
    if (deviceAttestationMethod == DeviceAttestationMethod.ECDSA) {
      log.info("Device verification method is ECDSA.");
      verifyOwnershipVoucherCertChain(ownershipVoucher);
      log.info("Certificate chain successfully validated.");
    } else {
      log.info("Device verification method is EPID.");
    }

    int waitSeconds = ownerSignRequest.getTo0Data().getWaitSeconds();
    wsValidator.validateCorrectness(waitSeconds);
  }

  DeviceAttestationMethod getDeviceAttestationMethod(OwnerSignRequest ownerSignRequest)
      throws InvalidAttestationMethodException {
    return deviceAttestationService.getDeviceAttestationMethod(
        ownerSignRequest.getTo0Data().getOwnershipVoucher());
  }

  private void verifyGuid(byte[] guid) throws InvalidGuidException, GuidDenylistException {
    guidValidator.validateGuidLength(guid);
    log.info("Guid length is valid.");
    guidValidator.verifyAgainstDenyList(guid);
    log.info("Guid successfully verified against the denylist.");
  }

  private void verifyOwnershipVoucherCertChain(OwnershipVoucher ownershipVoucher)
      throws InvalidCertChainHashException, FailedCertChainVerificationException, IOException {
    try {
      Hash requestHash = ownershipVoucher.getOwnershipVoucherHeader().getDeviceCertChainHash();

      byte[] validHash =
          hashGenerator.generate(
              JsonUtils.mapObjectToJson(ownershipVoucher.getCertificateChain()).getBytes(),
              requestHash.getHashType());
      if (!Arrays.equals(validHash, requestHash.getHash())) {
        log.error("CertChain hash from ownership voucher header doesn't match to0d.op.dc");
        throw new InvalidCertChainHashException();
      }
    } catch (NoSuchAlgorithmException | JsonProcessingException e) {
      log.debug("Verifying ownership voucher failed. Error: {}", e.getMessage());
      throw new InvalidCertChainHashException(e);
    }
    certChainValidator.validateCertChain(ownershipVoucher.getCertificateChain());
  }
}
