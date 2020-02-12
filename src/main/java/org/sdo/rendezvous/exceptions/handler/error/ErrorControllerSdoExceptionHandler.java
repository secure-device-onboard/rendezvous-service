// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.exceptions.handler.error;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@NoArgsConstructor
@Order(1)
@RestControllerAdvice(basePackages = "org.sdo.rendezvous.controllers.error")
public class ErrorControllerSdoExceptionHandler {

  @ResponseBody
  @ExceptionHandler({Exception.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public void handleAllExceptions(Exception exception, HttpServletResponse response)
      throws IOException {
    log.error(exception.getMessage());
  }
}
