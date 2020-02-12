// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services;

import com.google.common.base.Joiner;
import java.io.IOException;
import javax.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.enums.EpidResource;
import org.sdo.rendezvous.enums.EpidVersion;
import org.sdo.rendezvous.http.HttpClient;
import org.sdo.rendezvous.model.requests.to1.EpidVerificationRequest;
import org.sdo.rendezvous.model.types.SigInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EpidVerificationService {

  private final RendezvousConfig rendezvousConfig;
  private final HttpClient httpClient;

  /**
   * Verifies epid signature.
   *
   * @param gid the identifier of epid public group
   * @param message the message which was signed
   * @param signature the epid signature bytes to be verified
   * @param proofEndpoint the path to a proof endpoint where the signature must be sent
   * @throws IOException if an I/O error occurs, thrown by class EpidVerificationRequest
   */
  public void verifySignature(byte[] gid, byte[] message, byte[] signature, String proofEndpoint)
      throws IOException {
    EpidVerificationRequest request = new EpidVerificationRequest(gid, message, signature);
    httpClient.doPost(
        HttpClient.buildUrl(rendezvousConfig.getVerificationServiceHost(), proofEndpoint),
        request.toByteArray());
  }

  byte[] getSigrl(SigInfo sigInfo, EpidVersion epidVersion) {
    byte[] sigrlResponse =
        getEpidVerificationServiceResource(sigInfo, EpidResource.SIGRL, epidVersion);
    if (sigrlResponse == null) {
      return new byte[] {};
    }
    return sigrlResponse;
  }

  byte[] getPublicKey(SigInfo sigInfo) {
    return getEpidVerificationServiceResource(sigInfo, EpidResource.PUBKEY, EpidVersion.EPID20);
  }

  byte[] getGroupCertSigma10(SigInfo sigInfo) {
    return getEpidVerificationServiceResource(
        sigInfo, EpidResource.GROUPCERTSIGMA10, EpidVersion.EPID11);
  }

  byte[] getGroupCertSigma11(SigInfo sigInfo) {
    return getEpidVerificationServiceResource(
        sigInfo, EpidResource.GROUPCERTSIGMA11, EpidVersion.EPID11);
  }

  private byte[] getEpidVerificationServiceResource(
      SigInfo sigInfo, EpidResource resource, EpidVersion epidVersion) {
    String path =
        Joiner.on(HttpClient.URL_PATH_SEPARATOR)
            .join(
                epidVersion.getEpidVersion(),
                DatatypeConverter.printHexBinary(sigInfo.getBytes()),
                resource.toLowerCaseString());
    return httpClient.doGet(
        HttpClient.buildUrl(rendezvousConfig.getVerificationServiceHost(), path));
  }

  /**
   * Returns the status of Verification service.
   *
   * @param url the url to the Verificaton service
   * @return the Verification service status as an Entity of String with HTTP status code
   */
  ResponseEntity<String> getEpidVerificationHealth(String url) {
    try {
      return httpClient.getForStringEntity(url);
    } catch (RestClientException exception) {
      log.error(
          String.format(
              "Connection to verification service failed. Error: %s", exception.getMessage()));
      return new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
