// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.authenticators;

/** The implementation of abtract class Authenticator specified for SHA384withECDSA. */
public class AuthenticatorEcdsaWithSha384 extends Authenticator {

  private static final String SIGNATURE_ALGORITHM = "SHA384withECDSA";

  @Override
  protected String getSignatureAlgorithm() {
    return SIGNATURE_ALGORITHM;
  }
}
