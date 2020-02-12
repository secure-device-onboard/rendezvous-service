// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services;

import static org.mockito.ArgumentMatchers.any;

import javax.xml.bind.DatatypeConverter;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sdo.rendezvous.OwnerSignRequestFactory;
import org.sdo.rendezvous.authenticators.Authenticator;
import org.sdo.rendezvous.authenticators.AuthenticatorFactory;
import org.sdo.rendezvous.crypto.HashGenerator;
import org.sdo.rendezvous.exceptions.InvalidHashException;
import org.sdo.rendezvous.exceptions.InvalidNonceException;
import org.sdo.rendezvous.exceptions.InvalidOwnerSignBodyException;
import org.sdo.rendezvous.exceptions.InvalidSignatureException;
import org.sdo.rendezvous.model.requests.to0.OwnerSignRequest;
import org.sdo.rendezvous.model.types.Hash;
import org.sdo.rendezvous.model.types.HashType;
import org.sdo.rendezvous.model.types.Signature;
import org.sdo.rendezvous.utils.JsonUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class OwnerSignDataVerifierTest {

  private static final byte[] NONCE = OwnerSignRequestFactory.NONCE;

  private OwnerSignRequest ownerSignRequest;
  private OwnerSignDataVerifier ownerSignDataVerifier;

  @Mock private Authenticator authenticator;

  @Mock private AuthenticatorFactory authenticatorFactory;

  private HashGenerator hashGenerator;

  @BeforeMethod
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    Mockito.when(authenticatorFactory.getAuthenticator(any())).thenReturn(authenticator);
    ownerSignRequest = OwnerSignRequestFactory.createOwnerSignRequest();
    hashGenerator = new HashGenerator();
    ownerSignDataVerifier = new OwnerSignDataVerifier(authenticatorFactory, hashGenerator);
  }

  @Test
  public void testVerifyOwnerSignBodyPositive() throws Exception {
    ownerSignDataVerifier.verify(ownerSignRequest, NONCE);
  }

  @Test(expectedExceptions = InvalidOwnerSignBodyException.class)
  public void testVerifyOwnerSignBodySignatureInvalid() throws Exception {
    byte[] bodySignature =
        DatatypeConverter.parseHexBinary(
            "BAAD9DF399ECE89820293704BA27BCFA5110ED2B1DE3F0173AA851829FD8CB6A3B9CEA35C28D484987D5"
                + "3665711951E502559A919EE3503877FE97A687915B2A786A52C2D9D575770385D6D0962927FDF683"
                + "8D3473B54BDCF4E4578CDBC8CCB2C6EBC7ED322B6A0E4F134BF5E92879888D6F31D9251E8D8705F5"
                + "688F99983B5F5F6D2F739949EDE973A453DADA57CBE4A8F3B58665A14FC586B12761B3CB6C8E9F04"
                + "A331B6B36990A24C9EFFCB6AA52C7D79357FD553D4154DE866AB8F3024C7EE50A69795EDBF2EEDD0"
                + "C484F73C32BE5F7A574DFD4B367FA114FF56E449999BD36E37A230C9985489AB1E9D712353B1B336"
                + "0263525F26D6F768FC653A7CF5CB");

    byte[] payload = JsonUtils.mapObjectToJson(ownerSignRequest.getTo1Data().getBody()).getBytes();

    ownerSignRequest.getTo1Data().setSignature(new Signature(bodySignature));
    Mockito.doThrow(InvalidSignatureException.class)
        .when(authenticator)
        .authenticate(
            bodySignature,
            payload,
            ownerSignRequest
                .getTo0Data()
                .getOwnershipVoucher()
                .getOwnershipVoucherEntries()[1]
                .getOwnershipVoucherEntryBody()
                .getPublicKey()
                .asJavaPublicKey());

    ownerSignDataVerifier.verify(ownerSignRequest, NONCE);
  }

  @Test(expectedExceptions = InvalidHashException.class)
  public void testVerifyOwnerSignBodyTO0HashInvalid() throws Exception {
    Hash invalidHash =
        new Hash(
            HashType.SHA256,
            DatatypeConverter.parseHexBinary(
                "87A10A832E2443F859D74A7873CDD77232D6F1360D0F14899BB53C500079B1F7"));
    ownerSignRequest.getTo1Data().getBody().setTo0DataHash(invalidHash);
    ownerSignDataVerifier.verify(ownerSignRequest, NONCE);
  }

  @Test(expectedExceptions = InvalidNonceException.class)
  public void testVerifyOwnerSignBodyNonceMismatch() throws Exception {
    ownerSignDataVerifier.verify(
        ownerSignRequest, DatatypeConverter.parseHexBinary("DEADCCDDAABBCCDDAABBCCDDAABBCCDD"));
  }
}
