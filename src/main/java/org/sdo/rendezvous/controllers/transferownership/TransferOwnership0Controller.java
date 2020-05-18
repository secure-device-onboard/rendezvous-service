// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.controllers.transferownership;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.crypto.To0JwTokenFactory;
import org.sdo.rendezvous.exceptions.SdoException;
import org.sdo.rendezvous.logging.aspects.generic.LogExecutionTime;
import org.sdo.rendezvous.model.SdoUrlMapping;
import org.sdo.rendezvous.model.beans.Nonce;
import org.sdo.rendezvous.model.requests.to0.OwnerSignRequest;
import org.sdo.rendezvous.model.responses.to0.AcceptOwnerResponse;
import org.sdo.rendezvous.model.responses.to0.HelloAckResponse;
import org.sdo.rendezvous.services.TransferOwnership0Service;
import org.sdo.rendezvous.utils.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The class represents the main logic for Transfer ownership protocol (TO0) The function of
 * Transfer Ownership Protocol 0 (TO0) is to register the new owner’s current Internet location with
 * the Rendezvous server under the GUID of the device being registered.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(
    value = {SdoUrlMapping.TO_MSG_110, SdoUrlMapping.TO_MSG_112, SdoUrlMapping.TO_MSG_113},
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
public class TransferOwnership0Controller {
  private final Nonce nonce;
  private final TransferOwnership0Service transferOwnership0Service;
  private final HttpServletResponse httpServletResponse;
  private final RendezvousConfig rendezvousConfig;
  private final To0JwTokenFactory to0JwTokenFactory;

  /**
   * Exposes endpoints /mp/110/msg/20, /mp/112/msg/20, /mp/113/msg/20 which initiate the TO0
   * protocol.
   *
   * @return hello ack response with HTTP status code
   * @throws JsonProcessingException if a JSON processing problem occurs
   */
  @LogExecutionTime
  @PostMapping(value = SdoUrlMapping.TO0_HELLO_ENDPOINT)
  public ResponseEntity<String> hello() throws JsonProcessingException {

    HelloAckResponse helloAckResponse = HelloAckResponse.generateInstance();
    String token =
        to0JwTokenFactory.buildToken(helloAckResponse.getNonce(), rendezvousConfig.getHmacSecret());
    ResponseUtils.addTokenHeader(httpServletResponse, token);

    return ResponseUtils.createJsonResponse(helloAckResponse, HttpStatus.OK);
  }

  /**
   * Exposes endpoints /mp/110/msg/22, /mp/112/msg/22, /mp/113/msg/22. The main logic for endpoints
   * 22: validates and verifies the Ownership Voucher.
   *
   * @param ownerSignRequest OwnerSignRequest instance, created from deserialized json, sent by new
   *     Owner Client
   * @return AcceptOwnerResponse instance with HTTP status code
   * @throws SdoException is thrown when something wrong happen during validation of
   *     ownerSignRequest
   * @throws IOException if an I/O error occurs
   */
  @LogExecutionTime
  @PostMapping(value = SdoUrlMapping.TO0_OWNER_SIGN_ENDPOINT)
  public ResponseEntity<String> ownerSign(@RequestBody @Valid OwnerSignRequest ownerSignRequest)
      throws SdoException, IOException {

    AcceptOwnerResponse acceptOwnerResponse =
        new AcceptOwnerResponse(
            transferOwnership0Service.getWaitSeconds(ownerSignRequest, nonce.getValue()));

    return ResponseUtils.createJsonResponse(acceptOwnerResponse, HttpStatus.OK);
  }
}
