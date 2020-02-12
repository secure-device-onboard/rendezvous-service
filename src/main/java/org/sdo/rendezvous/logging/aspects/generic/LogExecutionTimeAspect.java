// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.logging.aspects.generic;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
class LogExecutionTimeAspect {

  @Around("@annotation(annotation)")
  private Object methodCallLogger(ProceedingJoinPoint pjp, LogExecutionTime annotation)
      throws Throwable {
    String className = pjp.getTarget().getClass().getSimpleName();
    String methodName = pjp.getSignature().getName();
    String signature = className + "." + methodName;
    log.debug("Method '{}' called.", signature);

    long startTime = System.currentTimeMillis();
    Object retValue = pjp.proceed();
    long endTime = System.currentTimeMillis();
    long executionTime = endTime - startTime;

    log.debug("Method '{}' execution time {} ms.", signature, executionTime);

    return retValue;
  }
}
