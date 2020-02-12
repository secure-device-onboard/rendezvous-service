// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services;

import static org.sdo.rendezvous.utils.ByteConversionUtils.createLVs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.enums.EpidVersion;
import org.sdo.rendezvous.exceptions.InvalidGroupIdException;
import org.sdo.rendezvous.exceptions.InvalidSigInfoException;
import org.sdo.rendezvous.model.SdoURLMapping;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.sdo.rendezvous.model.types.SigInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@Slf4j
@RequiredArgsConstructor
class EpidMaterialService {

  private static final int EPID_20_LENGTH = 16;

  private final HttpServletRequest httpServletRequest;
  private final EpidVerificationService epidVerificationService;

  SigInfo getSigInfo(SigInfo sigInfo) throws InvalidSigInfoException, InvalidGroupIdException {
    try {
      EpidVersion epidVersion = getEpidVersion(sigInfo);
      checkGroupIdLength(sigInfo.getBytes().length, epidVersion.getGroupIdLength());
      List<byte[]> sigInfoBytes = new ArrayList<>();
      switch (epidVersion) {
        case EPID10:
          // enforce to request certificates for EPID11 (provided as static content by URL)
          SigInfo certRequestSigInfo = new SigInfo(PublicKeyType.EPID_1_1, sigInfo.getBytes());
          sigInfoBytes.add(epidVerificationService.getGroupCertSigma10(certRequestSigInfo));
          break;
        case EPID11:
          sigInfoBytes.add(epidVerificationService.getGroupCertSigma10(sigInfo));
          sigInfoBytes.add(epidVerificationService.getGroupCertSigma11(sigInfo));

          if (!httpServletRequest
              .getRequestURL()
              .toString()
              .contains(SdoURLMapping.PROTOCOL_VERSION_110)) {
            sigInfoBytes.add(epidVerificationService.getSigrl(sigInfo, epidVersion));
          }
          break;
        case EPID20:
          sigInfoBytes.add(epidVerificationService.getSigrl(sigInfo, EpidVersion.EPID20));
          sigInfoBytes.add(epidVerificationService.getPublicKey(sigInfo));
          break;
        default:
          throw new IllegalArgumentException("EpidVersion is invalid.");
      }
      return new SigInfo(sigInfo.getSigInfoType(), createLVs(sigInfoBytes));
    } catch (HttpClientErrorException | IOException ex) {
      log.error(
          "Problem with connection to verification service or sigrl and "
              + "pubkey do not exist for specified group ID.");
      throw new InvalidSigInfoException("Could not find SigInfo for specified group ID.");
    }
  }

  private EpidVersion getEpidVersion(SigInfo sigInfo) throws InvalidSigInfoException {
    switch (sigInfo.getSigInfoType()) {
      case EPID_1_0:
        return EpidVersion.EPID10;
      case EPID_1_1:
        return EpidVersion.EPID11;
      case EPID_2_0:
        return EpidVersion.EPID20;
      default:
        log.error(
            "Invalid EPID public key type. Signature info type: {}.",
            sigInfo.getSigInfoType().name());
        throw new InvalidSigInfoException("SigInfo is invalid.");
    }
  }

  private void checkGroupIdLength(int groupIdLength, int expectedGroupIdLength)
      throws InvalidGroupIdException {
    String group = expectedGroupIdLength == EPID_20_LENGTH ? "2.0" : "1.1";
    if (groupIdLength != expectedGroupIdLength) {
      log.error(
          "Invalid group ID from SigInfo, incorrect length for EPID {} group {} (expected {}).",
          group,
          groupIdLength,
          expectedGroupIdLength);
      throw new InvalidGroupIdException("Incorrect group ID length.");
    }
  }
}
