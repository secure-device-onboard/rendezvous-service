// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services.signature;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.exceptions.InvalidEpidSignatureException;
import org.sdo.rendezvous.exceptions.InvalidPublicKeyTypeException;
import org.sdo.rendezvous.exceptions.SdoException;
import org.sdo.rendezvous.model.types.PKEPIDEnc;
import org.sdo.rendezvous.model.types.ProveToSdoBody;
import org.sdo.rendezvous.model.types.PubKey;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.sdo.rendezvous.services.EpidVerificationService;
import org.sdo.rendezvous.utils.ArrayByteBuilder;
import org.sdo.rendezvous.utils.JsonUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@RequiredArgsConstructor
@Service
@Slf4j
class EpidSignatureVerifier implements ISignatureVerifier {

  private static final String PROOF_ENDPOINT_EPID11 = "v1/epid11/proof";
  private static final String PROOF_ENDPOINT_EPID20 = "v1/epid20/proof";

  private final EpidVerificationService epidVerificationService;

  @Override
  public void verify(ProveToSdoBody signatureBody, PubKey pkEpid, byte[] signature)
      throws SdoException {
    if (!(pkEpid instanceof PKEPIDEnc)) {
      throw new InvalidPublicKeyTypeException("Public key is not EPID Public key.");
    }
    PKEPIDEnc pkepidEnc = (PKEPIDEnc) pkEpid;
    try {
      log.info("Trying to verify EPID signature.");
      byte[] msg = createEpidSignatureBodyMessage(signatureBody, pkepidEnc.getPkType());
      String proofEndpoint = getProofEndpoint(pkepidEnc.getPkType());

      epidVerificationService.verifySignature(
          pkepidEnc.getEpidGroupNo(), msg, signature, proofEndpoint);
    } catch (HttpClientErrorException | IOException ex) {
      log.error(String.format("EPID Signature verification failed, Error: %s", ex.getMessage()));
      // TODO above error message would be better for exception message but must be adjusted with
      // functional tests and checked with guide for error messages in
      // SDO_Rendezvous_Service_Errors.md
      throw new InvalidEpidSignatureException("Signature verification failed.");
    }
  }

  private byte[] createEpidSignatureBodyMessage(ProveToSdoBody signatureBody, PublicKeyType pkType)
      throws IOException {
    byte[] jsonMsgBytes = JsonUtils.mapObjectToJson(signatureBody).getBytes();

    switch (pkType) {
      case EPID_1_0:
        return new ArrayByteBuilder()
            .append((byte) signatureBody.getAppId().getAppIdBytes().length)
            .append(signatureBody.getAppId().getAppIdBytes())
            .append(signatureBody.getNonce())
            .append(jsonMsgBytes)
            .build();
      case EPID_1_1:
        return new ArrayByteBuilder()
            .append((byte) 0, 4) // prefix start
            .append((byte) 0x48)
            .append((byte) 0, 3)
            .append((byte) 0x08)
            .append((byte) 0, 39) // prefix end
            .append(signatureBody.getAppId().getAppIdBytes())
            .append((byte) 0, 16)
            .append(signatureBody.getNonce())
            .append((byte) 0, 16)
            .append(jsonMsgBytes)
            .build();

      default:
        return jsonMsgBytes;
    }
  }

  private String getProofEndpoint(PublicKeyType pkType) throws InvalidEpidSignatureException {
    switch (pkType) {
      case EPID_1_0:
      case EPID_1_1:
        return PROOF_ENDPOINT_EPID11;
      case EPID_2_0:
        return PROOF_ENDPOINT_EPID20;
      default:
        log.error("Invalid EPID public key type.");
        throw new InvalidEpidSignatureException("Signature verification failed.");
    }
  }
}
