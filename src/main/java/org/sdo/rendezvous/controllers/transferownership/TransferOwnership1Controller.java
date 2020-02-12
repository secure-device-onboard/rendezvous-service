// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.controllers.transferownership;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.crypto.TO1JWTokenFactory;
import org.sdo.rendezvous.exceptions.SdoException;
import org.sdo.rendezvous.logging.aspects.generic.LogExecutionTime;
import org.sdo.rendezvous.model.SdoURLMapping;
import org.sdo.rendezvous.model.beans.DeviceInfo;
import org.sdo.rendezvous.model.requests.to1.HelloSdoRequest;
import org.sdo.rendezvous.model.requests.to1.ProveToSdoRequest;
import org.sdo.rendezvous.model.responses.to1.HelloSdoAckResponse;
import org.sdo.rendezvous.model.types.Device;
import org.sdo.rendezvous.model.types.OwnerSignTO1Data;
import org.sdo.rendezvous.services.TokenParserService;
import org.sdo.rendezvous.services.TransferOwnership1Service;
import org.sdo.rendezvous.utils.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The class represents the main logic for Transfer ownership protocol (TO1) Transfer Ownership
 * Protocol 1 (TO1) finishes the rendezvous started between the New Owner and the Rendezvous Server
 * in the Transfer Ownership Protocol 0 (TO0). In this protocol, the Device TEE communicates with
 * the Rendezvous Server and obtains the IP addressing info for the (potential) new Owner.
 */
@Slf4j
@RestController
@RequestMapping(
    value = {SdoURLMapping.TO_MSG_110, SdoURLMapping.TO_MSG_112, SdoURLMapping.TO_MSG_113},
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TransferOwnership1Controller {

  private final DeviceInfo deviceInfo;
  private final HttpServletResponse httpServletResponse;
  private final RendezvousConfig rendezvousConfig;
  private final TransferOwnership1Service transferOwnership1Service;
  private final TokenParserService tokenParserService;
  private final TO1JWTokenFactory to1JWTokenFactory;

  /**
   * Exposes endpoints /mp/110/msg/30, /mp/112/msg/30, /mp/113/msg/30. Establishes the presence of
   * the device at the Rendezvous Server. The Device GUID is included to help a REST server create a
   * token.
   *
   * @param helloRequest HelloSdoRequest instance, created from deserialized json
   * @return HelloSdoAckResponse instance serialized to json with status code
   * @throws SdoException is thrown when something wrong
   * @throws IOException unexpected exception
   */
  @LogExecutionTime
  @PostMapping(value = SdoURLMapping.TO1_HELLO_SDO_ENDPOINT)
  public ResponseEntity<String> helloSdo(@RequestBody @Valid HelloSdoRequest helloRequest)
      throws SdoException, IOException {

    HelloSdoAckResponse response = transferOwnership1Service.getHelloSdoAckResponse(helloRequest);
    String token =
        to1JWTokenFactory.buildToken(
            helloRequest.getGuid(), response.getNonce(), rendezvousConfig.getHmacSecret());

    ResponseUtils.addTokenHeader(httpServletResponse, token);

    return ResponseUtils.createJsonResponse(response, HttpStatus.OK);
  }

  /**
   * Exposes endpoints /mp/110/msg/32, /mp/112/msg/32, /mp/113/msg/32. Validates of device identity
   * to the Rendezvous Server for the Device seeking its owner, and indicates its GUID.
   *
   * @param proveRequest ProveToSdoRequest instance, created from deserialized json
   * @return OwnerSignTO1Data instance serialized to json with status code
   * @throws SdoException is thrown when something wrong went happen
   * @throws IOException unexpected exception
   */
  @LogExecutionTime
  @PostMapping(value = SdoURLMapping.TO1_PROVE_TO_SDO_ENDPOINT)
  public ResponseEntity<String> proveToSdo(@RequestBody @Valid ProveToSdoRequest proveRequest)
      throws SdoException, IOException {

    Device device = new Device(deviceInfo.getGuid(), deviceInfo.getNonce());
    OwnerSignTO1Data to1Data =
        transferOwnership1Service.getProveToSdoResponse(device, proveRequest);

    ResponseUtils.addTokenHeader(httpServletResponse, tokenParserService.getToken(device));

    return ResponseUtils.createJsonResponse(to1Data, HttpStatus.OK);
  }
}
