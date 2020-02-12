// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.authenticators;

/** The implementation of abtract class Authenticator specified for SHA256withRSA. */
public class AuthenticatorRsaWithSha256 extends Authenticator {

  private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

  @Override
  protected String getSignatureAlgorithm() {
    return SIGNATURE_ALGORITHM;
  }
}
