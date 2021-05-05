#!/bin/sh
# Copyright 2019 Intel Corporation
# SPDX-License-Identifier: Apache 2.0

SSL_KEY_STORE="rendezvous-keystore.jks"
JAVA_SSL_PARAMS="-Dserver.ssl.key-store=/home/sdouser/certs/$SSL_KEY_STORE -Dserver.ssl.key-store-password=$SSL_KEY_STORE_PASSWORD"
JAVA_REDIS_PARAMS="-Dredis.host=$REDIS_HOST -Dredis.password=$REDIS_PASSWORD -Dredis.port=$REDIS_PORT"
TRUST_STORE_SSL_PARAM="-Djavax.net.ssl.trustStore=/home/sdouser/certs/rendezvous-trusterRootCA.jks -Djavax.net.ssl.trustStorePassword=$SSL_TRUST_STORE_PASSWORD"
HMAC_SECRET="-Drendezvous.hmacSecret=$(tr -cd 'a-f0-9' < /dev/urandom | head -c128)"

PROXY_SETTINGS=""
if [ "" != "${http_proxy}" ]
then
    HTTP_PROXY_URL=$(echo $http_proxy | awk -F':' {'print $2'} | tr -d '/')
    HTTP_PROXY_PORT=$(echo $http_proxy | awk -F':' {'print $3'} | tr -d '/')
    PROXY_SETTINGS="-Dhttp.proxyHost=${HTTP_PROXY_URL} -Dhttp.proxyPort=${HTTP_PROXY_PORT}"
fi
if [ "" != "${https_proxy}" ]
then
    HTTPS_PROXY_URL=$(echo $https_proxy | awk -F':' {'print $2'} | tr -d '/')
    HTTPS_PROXY_PORT=$(echo $https_proxy | awk -F':' {'print $3'} | tr -d '/')
    PROXY_SETTINGS="${PROXY_SETTINGS} -Dhttps.proxyHost=${HTTPS_PROXY_URL} -Dhttps.proxyPort=${HTTPS_PROXY_PORT}"
fi

java ${PROXY_SETTINGS} ${JAVA_REDIS_PARAMS} ${TRUST_STORE_SSL_PARAM} ${JAVA_SSL_PARAMS} ${HMAC_SECRET} -jar /home/sdouser/rendezvous-service-*.war
