// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.config;

import java.util.Arrays;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.ServerConnector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;

/** This is a class which customizes usage of Jetty server. */
public class CustomJettyContainerConfiguration {

  /**
   * Create a new WebServerFactoryCustomizer with specified WebServerFactory as a bean.
   *
   * @return a new WebServerFactoryCustomizer with JettyServletWebServerFactory
   */
  @Bean
  public WebServerFactoryCustomizer<JettyServletWebServerFactory> customizer() {
    return containerCustomizer -> {
      if (containerCustomizer != null) {
        containerCustomizer.addServerCustomizers(
            server ->
                Arrays.stream(server.getConnectors())
                    .filter(connector -> connector instanceof ServerConnector)
                    .forEach(
                        connector -> {
                          HttpConnectionFactory connectionFactory =
                              connector.getConnectionFactory(HttpConnectionFactory.class);
                          connectionFactory.getHttpConfiguration().setSendServerVersion(false);
                        }));
      }
    };
  }

  /**
   * Create a new JettyServletWebServerFactory as a bean.
   *
   * @param httpPort i a specified port taken from configuration
   * @return a new JettyServletWebServerFactory with the specified port
   */
  @Bean
  public JettyServletWebServerFactory jettyServletWebServerFactory(
      @Value("${server.http.port}") int httpPort) {
    JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
    factory.addServerCustomizers((JettyServerCustomizer)
        server -> {
        NetworkTrafficServerConnector connector = new NetworkTrafficServerConnector(server);
        connector.setPort(httpPort);
        server.addConnector(connector);
      });
    return factory;
  }
}
