#!/usr/bin bash

# Copyright 2019 Intel Corporation
# SPDX-License-Identifier: Apache 2.0

# This is an example implementation. This should be updated in production deployment.
# Care must be taken to ensure the cryptographic strength of this key.
HMAC_SECRET="-Drendezvous.hmacSecret=$(openssl rand -hex 64)"

java -Dserver.ssl.key-store=./certs/rendezvous-keystore.jks -Dserver.ssl.key-store-password=123456 \
     -Djavax.net.ssl.trustStore=./certs/rendezvous-trusterRootCA.jks -Djavax.net.ssl.trustStorePassword=123456 \
     ${HMAC_SECRET} \
     -Dserver.port=8000 -Drendezvous.verificationServiceHost=https://verify.epid-sbx.trustedservices.intel.com \
     -Dspring.profiles.active=production \
     -jar ./target/rendezvous-service-*.war
