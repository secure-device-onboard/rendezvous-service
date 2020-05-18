// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.config;

import lombok.RequiredArgsConstructor;
import org.sdo.rendezvous.interceptors.SessionIdGeneratingInterceptor;
import org.sdo.rendezvous.interceptors.To0JwtParsingInterceptor;
import org.sdo.rendezvous.interceptors.To1JwtParsingInterceptor;
import org.sdo.rendezvous.model.SdoUrlMapping;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class RendezvousInterceptorsConfig implements WebMvcConfigurer {

  private final To0JwtParsingInterceptor to0JwtParsingInterceptor;
  private final To1JwtParsingInterceptor to1JwtParsingInterceptor;
  private final SessionIdGeneratingInterceptor sessionIdGeneratingInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(sessionIdGeneratingInterceptor)
        .addPathPatterns(
            SdoUrlMapping.TO0_110_HELLO,
            SdoUrlMapping.TO0_112_HELLO,
            SdoUrlMapping.TO0_113_HELLO,
            SdoUrlMapping.TO1_110_HELLO_SDO,
            SdoUrlMapping.TO1_112_HELLO_SDO,
            SdoUrlMapping.TO1_113_HELLO_SDO);

    registry
        .addInterceptor(to0JwtParsingInterceptor)
        .addPathPatterns(
            SdoUrlMapping.TO0_110_OWNER_SIGN,
            SdoUrlMapping.TO0_112_OWNER_SIGN,
            SdoUrlMapping.TO0_113_OWNER_SIGN);

    registry
        .addInterceptor(to1JwtParsingInterceptor)
        .addPathPatterns(
            SdoUrlMapping.TO1_110_PROVE_TO_SDO,
            SdoUrlMapping.TO1_112_PROVE_TO_SDO,
            SdoUrlMapping.TO1_113_PROVE_TO_SDO,
            SdoUrlMapping.ERROR_ENDPOINT_110,
            SdoUrlMapping.ERROR_ENDPOINT_112,
            SdoUrlMapping.ERROR_ENDPOINT_113);
  }
}
