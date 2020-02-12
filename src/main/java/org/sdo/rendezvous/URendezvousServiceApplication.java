// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous;

import org.sdo.rendezvous.config.CustomJettyContainerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication(
    exclude = {
      DataSourceAutoConfiguration.class,
      HibernateJpaAutoConfiguration.class,
      CacheAutoConfiguration.class,
      RepositoryRestMvcAutoConfiguration.class
    })
@Import(CustomJettyContainerConfiguration.class)
public class URendezvousServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(URendezvousServiceApplication.class, args);
  }
}
