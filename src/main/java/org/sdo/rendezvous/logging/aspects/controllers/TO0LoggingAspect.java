// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.logging.aspects.controllers;

import java.util.stream.Collectors;
import javax.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.sdo.rendezvous.enums.AttributeName;
import org.sdo.rendezvous.exceptions.SdoException;
import org.sdo.rendezvous.logging.utils.LoggingUtils;
import org.sdo.rendezvous.model.beans.WhitelistedHashes;
import org.sdo.rendezvous.model.requests.to0.OwnerSignRequest;
import org.sdo.rendezvous.model.responses.to0.AcceptOwnerResponse;
import org.sdo.rendezvous.model.responses.to0.HelloAckResponse;
import org.sdo.rendezvous.utils.JsonUtils;
import org.sdo.rendezvous.utils.SessionAttributeHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@SuppressWarnings("unchecked")
class TO0LoggingAspect {
  private static final int FIRST_ARGUMENT = 0;

  private final WhitelistedHashes whitelistedHashes;

  TO0LoggingAspect(WhitelistedHashes whitelistedHashes) {
    this.whitelistedHashes = whitelistedHashes;
  }

  @Around(
      "execution(* org.sdo.rendezvous.controllers.transferownership"
          + ".TransferOwnership0Controller.hello(..))")
  private Object helloLogInputAndOutput(ProceedingJoinPoint pjp) throws Throwable {
    log.info(
        LoggingUtils.CUSTOMER,
        "TO0.Hello request received. Protocol version: {}.",
        LoggingUtils.getProtocolVersionFromUrl());
    ResponseEntity<String> retValue = (ResponseEntity<String>) pjp.proceed();
    log.info(
        LoggingUtils.CUSTOMER,
        "TO0.Hello request processing finished. Sending TO0.HelloAck response with "
            + "HTTP status code: {}. Response data: {}.",
        retValue.getStatusCode().value(),
        JsonUtils.mapJsonToObject(retValue.getBody(), HelloAckResponse.class));
    return retValue;
  }

  @AfterThrowing(
      pointcut =
          "execution(* org.sdo.rendezvous.controllers.transferownership"
              + ".TransferOwnership0Controller.hello(..))",
      throwing = "e")
  private void helloLogError(SdoException e) {
    log.info(
        LoggingUtils.CUSTOMER,
        "TO0.Hello request processing finished. Sending error response with "
            + "HTTP status code: {}. Response data: {}.",
        HttpStatus.BAD_REQUEST.value(),
        e.toString());
  }

  @Around(
      "execution(* org.sdo.rendezvous.controllers.transferownership"
          + ".TransferOwnership0Controller.ownerSign(..))")
  private Object ownerSignLogInputAndOutput(ProceedingJoinPoint pjp) throws Throwable {
    Object[] arguments = pjp.getArgs();
    SessionAttributeHolder.setAttributeValue(
        AttributeName.GUID, ((OwnerSignRequest) arguments[FIRST_ARGUMENT]).getGuid());
    log.info(
        LoggingUtils.CUSTOMER,
        "TO0.OwnerSign request received. Protocol version: {}. Request data: {}.",
        LoggingUtils.getProtocolVersionFromUrl(),
        arguments[FIRST_ARGUMENT]);
    ResponseEntity<String> retValue = (ResponseEntity<String>) pjp.proceed();
    log.info(
        LoggingUtils.CUSTOMER,
        "TO0.OwnerSign request processing finished. Sending TO0.AcceptOwner response with "
            + "HTTP status code: {}. Following hashes have been found on whitelist: {}. "
            + "Response data: {}.",
        retValue.getStatusCode().value(),
        whitelistedHashes.getAll().stream()
            .map(DatatypeConverter::printHexBinary)
            .collect(Collectors.toList()),
        JsonUtils.mapJsonToObject(retValue.getBody(), AcceptOwnerResponse.class));
    SessionAttributeHolder.removeAttribute(AttributeName.GUID);
    return retValue;
  }

  @AfterThrowing(
      pointcut =
          "execution(* org.sdo.rendezvous.controllers.transferownership"
              + ".TransferOwnership0Controller.ownerSign(..))",
      throwing = "e")
  private void ownerSignLogError(SdoException e) {
    log.info(
        LoggingUtils.CUSTOMER,
        "TO0.OwnerSign request processing finished. Sending error response with "
            + "HTTP status code: {}. Response data: {}.",
        HttpStatus.BAD_REQUEST.value(),
        e.toString());
  }
}
