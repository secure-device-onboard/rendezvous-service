// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.config;

import lombok.RequiredArgsConstructor;
import org.sdo.rendezvous.interceptors.SessionIdGeneratingInterceptor;
import org.sdo.rendezvous.interceptors.TO0JwtParsingInterceptor;
import org.sdo.rendezvous.interceptors.TO1JwtParsingInterceptor;
import org.sdo.rendezvous.model.SdoURLMapping;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class RendezvousInterceptorsConfig implements WebMvcConfigurer {

  private final TO0JwtParsingInterceptor to0JwtParsingInterceptor;
  private final TO1JwtParsingInterceptor to1JwtParsingInterceptor;
  private final SessionIdGeneratingInterceptor sessionIdGeneratingInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(sessionIdGeneratingInterceptor)
        .addPathPatterns(
            SdoURLMapping.TO0_110_HELLO,
            SdoURLMapping.TO0_112_HELLO,
            SdoURLMapping.TO0_113_HELLO,
            SdoURLMapping.TO1_110_HELLO_SDO,
            SdoURLMapping.TO1_112_HELLO_SDO,
            SdoURLMapping.TO1_113_HELLO_SDO);

    registry
        .addInterceptor(to0JwtParsingInterceptor)
        .addPathPatterns(
            SdoURLMapping.TO0_110_OWNER_SIGN,
            SdoURLMapping.TO0_112_OWNER_SIGN,
            SdoURLMapping.TO0_113_OWNER_SIGN);

    registry
        .addInterceptor(to1JwtParsingInterceptor)
        .addPathPatterns(
            SdoURLMapping.TO1_110_PROVE_TO_SDO,
            SdoURLMapping.TO1_112_PROVE_TO_SDO,
            SdoURLMapping.TO1_113_PROVE_TO_SDO,
            SdoURLMapping.ERROR_ENDPOINT_110,
            SdoURLMapping.ERROR_ENDPOINT_112,
            SdoURLMapping.ERROR_ENDPOINT_113);
  }
}
