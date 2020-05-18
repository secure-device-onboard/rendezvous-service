// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.logging.aspects.controllers;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.sdo.rendezvous.enums.AttributeName;
import org.sdo.rendezvous.exceptions.SdoException;
import org.sdo.rendezvous.logging.utils.LoggingUtils;
import org.sdo.rendezvous.model.requests.to1.HelloSdoRequest;
import org.sdo.rendezvous.model.requests.to1.ProveToSdoRequest;
import org.sdo.rendezvous.model.responses.to1.HelloSdoAckResponse;
import org.sdo.rendezvous.model.types.OwnerSignTo1Data;
import org.sdo.rendezvous.utils.JsonUtils;
import org.sdo.rendezvous.utils.SessionAttributeHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@SuppressWarnings("unchecked")
class TO1LoggingAspect {
  private static final int FIRST_ARGUMENT = 0;

  @Around(
      "execution(* org.sdo.rendezvous.controllers.transferownership"
          + ".TransferOwnership1Controller.helloSdo(..))")
  private Object helloSdoLogInputAndOutput(ProceedingJoinPoint pjp) throws Throwable {
    Object[] arguments = pjp.getArgs();

    // logger properties
    SessionAttributeHolder.setAttributeValue(
        AttributeName.GUID, ((HelloSdoRequest) arguments[FIRST_ARGUMENT]).getGuid());

    log.info(
        LoggingUtils.CUSTOMER,
        "TO1.HelloSDO request received. Protocol version: {}. Request data: {}.",
        LoggingUtils.getProtocolVersionFromUrl(),
        arguments[FIRST_ARGUMENT]);
    ResponseEntity<String> retValue = (ResponseEntity<String>) pjp.proceed();
    log.info(
        LoggingUtils.CUSTOMER,
        "TO1.HelloSDO request processing finished. Sending TO1.HelloSDOAck response with "
            + "HTTP status code {}. Response data: {}.",
        retValue.getStatusCode().value(),
        JsonUtils.mapJsonToObject(retValue.getBody(), HelloSdoAckResponse.class));
    SessionAttributeHolder.removeAttribute(AttributeName.GUID);
    return retValue;
  }

  @AfterThrowing(
      pointcut =
          "execution(* org.sdo.rendezvous.controllers.transferownership"
              + ".TransferOwnership1Controller.helloSdo(..))",
      throwing = "e")
  private void helloSdoLogError(SdoException e) {
    log.info(
        LoggingUtils.CUSTOMER,
        "TO1.HelloSDO request processing finished. Sending error response with "
            + "HTTP status code: {}. Response data: {}.",
        HttpStatus.BAD_REQUEST.value(),
        e.toString());
  }

  @Around(
      "execution(* org.sdo.rendezvous.controllers.transferownership"
          + ".TransferOwnership1Controller.proveToSdo(..))")
  private Object proveToSdoLogInputAndOutput(ProceedingJoinPoint pjp) throws Throwable {
    Object[] arguments = pjp.getArgs();

    SessionAttributeHolder.setAttributeValue(
        AttributeName.GUID,
        ((ProveToSdoRequest) arguments[FIRST_ARGUMENT]).getProveToSdoBody().getGuid());

    log.info(
        LoggingUtils.CUSTOMER,
        "TO1.ProveToSDO request received. Protocol version: {}. Request data: {}.",
        LoggingUtils.getProtocolVersionFromUrl(),
        arguments[FIRST_ARGUMENT]);
    ResponseEntity<String> retValue = (ResponseEntity<String>) pjp.proceed();
    log.info(
        LoggingUtils.CUSTOMER,
        "TO1.ProveToSDO request processing finished. Sending TO1.SDORedirect response with "
            + "HTTP status code {}. Response data: {}.",
        retValue.getStatusCode().value(),
        JsonUtils.mapJsonToObject(retValue.getBody(), OwnerSignTo1Data.class));
    SessionAttributeHolder.removeAttribute(AttributeName.GUID);
    return retValue;
  }

  @AfterThrowing(
      pointcut =
          "execution(* org.sdo.rendezvous.controllers.transferownership"
              + ".TransferOwnership1Controller.proveToSdo(..))",
      throwing = "e")
  private void proveToSdoLogError(SdoException e) {
    log.info(
        LoggingUtils.CUSTOMER,
        "TO1.ProveToSDO request processing finished. Sending error response with "
            + "HTTP status code: {}. Response data: {}.",
        HttpStatus.BAD_REQUEST.value(),
        e.toString());
  }
}
