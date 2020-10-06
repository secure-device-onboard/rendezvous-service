// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.Arrays;
import javax.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.enums.ErrorCodes;
import org.sdo.rendezvous.exceptions.GuidBlacklistedException;
import org.sdo.rendezvous.exceptions.InvalidGroupIdException;
import org.sdo.rendezvous.exceptions.InvalidGuidException;
import org.sdo.rendezvous.exceptions.InvalidNonceException;
import org.sdo.rendezvous.exceptions.InvalidProveRequestException;
import org.sdo.rendezvous.exceptions.InvalidSigInfoException;
import org.sdo.rendezvous.exceptions.SdoException;
import org.sdo.rendezvous.model.database.VersionedTo1Data;
import org.sdo.rendezvous.model.log.to1.EpidTO1TransactionInfo;
import org.sdo.rendezvous.model.log.to1.TO1TransactionInfo;
import org.sdo.rendezvous.model.requests.to1.HelloSdoRequest;
import org.sdo.rendezvous.model.requests.to1.ProveToSdoRequest;
import org.sdo.rendezvous.model.responses.to1.HelloSdoAckResponse;
import org.sdo.rendezvous.model.types.Device;
import org.sdo.rendezvous.model.types.OwnerSignTo1Data;
import org.sdo.rendezvous.model.types.PkEcdsaEnc;
import org.sdo.rendezvous.model.types.PkEpidEnc;
import org.sdo.rendezvous.model.types.PkOnDieEcdsaEnc;
import org.sdo.rendezvous.model.types.PubKey;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.sdo.rendezvous.model.types.SigInfo;
import org.sdo.rendezvous.repositories.JedisRepository;
import org.sdo.rendezvous.services.signature.SignatureVerifier;
import org.sdo.rendezvous.utils.ByteConverter;
import org.sdo.rendezvous.utils.validators.GuidValidator;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferOwnership1Service {

  public static final String[] appIdList = {"e2cb06bd11a323419d1d3563b809bc46"};
  private final GuidValidator guidValidator;
  private final EpidMaterialService epidMaterialService;
  private final JedisRepository jedisRepository;
  private final SignatureVerifier signatureVerifier;

  /**
   * Establishes the presence of the device at the Rendezvous Server. Returns an instance of
   * HelloSdoAckResponse if the request is valid.
   *
   * @param helloRequest the hello sdo request send from Device TEE to Rendezvous server
   * @return the instance of HelloSdoAckResponse
   * @throws InvalidGuidException if guid is invalid
   * @throws InvalidSigInfoException if sig info is invalid
   * @throws GuidBlacklistedException if guid in on the blacklist
   * @throws InvalidGroupIdException if rpid group doesn't exist
   */
  public HelloSdoAckResponse getHelloSdoAckResponse(HelloSdoRequest helloRequest)
      throws InvalidGuidException, InvalidSigInfoException, GuidBlacklistedException,
          InvalidGroupIdException {
    log.debug("Received helloSdo request.");

    guidValidator.validateGuidLength(helloRequest.getGuid());
    log.info("Guid length successfully verified.");
    guidValidator.verifyAgainstBlackList(helloRequest.getGuid());
    log.info("Guid successfully verified against the black list.");

    SigInfo sigInfo = getSigInfo(helloRequest);

    return HelloSdoAckResponse.generateInstance(sigInfo);
  }

  /**
   * Proves validity of device identity to the Rendezvous Server for the Device seeking its owner,
   * and indicates its GUID, “g2”. Returns an instance of OwnerSignTO1Data if the request is valid.
   *
   * @param device an instance of Device created from DeviceInfo (a instance of DeviceInfo created
   *     from JWT claims)
   * @param proveRequest the prove to sdo request send from Device TEE to Rendezvous server
   * @return the instance of OwnerSignTO1Data
   * @throws IOException if an I/O error occurs
   * @throws SdoException if the problem with validation owner sign request occurs
   */
  public OwnerSignTo1Data getProveToSdoResponse(Device device, ProveToSdoRequest proveRequest)
      throws IOException, SdoException {

    verifyTokenData(proveRequest, device);
    log.info("Token data successfully verified.");

    VersionedTo1Data versionedTO1Data =
        jedisRepository.getVersionedTO1Data(
            ByteConverter.getGuidFromByteArray(proveRequest.getProveToSdoBody().getGuid()));
    PubKey publicKey = getPubKey(proveRequest, versionedTO1Data);

    if (publicKey.getPkType() == PublicKeyType.EPID_1_0
        || publicKey.getPkType() == PublicKeyType.EPID_1_1
        || publicKey.getPkType() == PublicKeyType.EPID_2_0) {
      verifyAppId(proveRequest);
      log.info("App Id successfully verified.");
    }

    signatureVerifier.verify(
        proveRequest.getProveToSdoBody(), publicKey, proveRequest.getSignature().getBytes());
    log.info("Signature successfully verified.");
    saveTransactionInfo(proveRequest);
    return new OwnerSignTo1Data(versionedTO1Data.getTo1Data());
  }

  private void saveTransactionInfo(ProveToSdoRequest proveToSdoRequest)
      throws JsonProcessingException, InvalidGuidException {
    TO1TransactionInfo transactionInfo;
    if (isPublicKeyEcdsa(proveToSdoRequest) || isPublicKeyOnDieEcdsa(proveToSdoRequest)) {
      transactionInfo =
          new TO1TransactionInfo(
              ByteConverter.getGuidFromByteArray(proveToSdoRequest.getProveToSdoBody().getGuid()));
    } else {
      transactionInfo =
          new EpidTO1TransactionInfo(
              ByteConverter.getGuidFromByteArray(proveToSdoRequest.getProveToSdoBody().getGuid()),
              DatatypeConverter.printHexBinary(
                  ((PkEpidEnc) proveToSdoRequest.getPublicKey()).getEpidGroupNo()));
    }
    jedisRepository.setTransactionInfo(transactionInfo);
  }

  private PubKey getPubKey(ProveToSdoRequest proveRequest, VersionedTo1Data versionedTO1Data) {
    if (isPublicKeyEcdsa(proveRequest)) {
      return new PkEcdsaEnc(
          proveRequest.getPublicKey().getPkType(), versionedTO1Data.getEcdsaPublicKey());
    } else if (isPublicKeyOnDieEcdsa(proveRequest)) {
      return new PkOnDieEcdsaEnc(
          proveRequest.getPublicKey().getPkType(), versionedTO1Data.getEcdsaPublicKey());
    }
    return proveRequest.getPublicKey();
  }

  private SigInfo getSigInfo(HelloSdoRequest helloRequest)
      throws InvalidSigInfoException, InvalidGroupIdException {
    if (isPublicKeyEcdsa(helloRequest) || isPublicKeyOnDieEcdsa(helloRequest)) {
      return helloRequest.getSigInfo();
    }
    return epidMaterialService.getSigInfo(helloRequest.getSigInfo());
  }

  private void verifyTokenData(ProveToSdoRequest request, Device device)
      throws InvalidProveRequestException, InvalidNonceException {
    if (!Arrays.equals(request.getProveToSdoBody().getGuid(), device.getGuid())) {
      throw new InvalidProveRequestException("GUIDs in request and JWT don't match.");
    }
    if (!Arrays.equals(request.getProveToSdoBody().getNonce(), device.getNonce())) {
      throw new InvalidNonceException("The nonce from JWT doesn't match the nonce from body.");
    }
  }

  private void verifyAppId(ProveToSdoRequest request) throws SdoException {
    boolean isAppIdValid = false;
    final byte[] proveDeviceAppId = request.getProveToSdoBody().getAppId().getAppIdBytes();
    for (final String appId : appIdList) {
      if (Arrays.equals(proveDeviceAppId, DatatypeConverter.parseHexBinary(appId))) {
        isAppIdValid = true;
        break;
      }
    }
    if (!isAppIdValid) {
      throw new SdoException("Invalid appId", ErrorCodes.MESSAGE_BODY_ERROR);
    }
  }

  private boolean isPublicKeyEcdsa(HelloSdoRequest helloSdoRequest) {
    return helloSdoRequest.getSigInfo().getSigInfoType().isEcdsa();
  }

  private boolean isPublicKeyEcdsa(ProveToSdoRequest proveToSdoRequest) {
    return proveToSdoRequest.getPublicKey().getPkType().isNone();
  }

  private boolean isPublicKeyOnDieEcdsa(HelloSdoRequest helloSdoRequest) {
    return helloSdoRequest.getSigInfo().getSigInfoType().isOnDieEcdsa();
  }

  private boolean isPublicKeyOnDieEcdsa(ProveToSdoRequest proveToSdoRequest) {
    return proveToSdoRequest.getPublicKey().getPkType().isOnDieEcdsa();
  }
}
