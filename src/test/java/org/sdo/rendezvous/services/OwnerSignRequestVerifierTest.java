// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services;

import static org.testng.Assert.assertEquals;

import java.util.Collections;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sdo.rendezvous.OwnerSignRequestFactory;
import org.sdo.rendezvous.crypto.HashGenerator;
import org.sdo.rendezvous.enums.DeviceAttestationMethod;
import org.sdo.rendezvous.exceptions.InvalidCertChainHashException;
import org.sdo.rendezvous.model.requests.to0.OwnerSignRequest;
import org.sdo.rendezvous.model.types.CertChain;
import org.sdo.rendezvous.model.types.CertType;
import org.sdo.rendezvous.model.types.Hash;
import org.sdo.rendezvous.model.types.HashType;
import org.sdo.rendezvous.services.op.IOwnershipVoucherVerifier;
import org.sdo.rendezvous.utils.JsonUtils;
import org.sdo.rendezvous.utils.validators.CertChainValidator;
import org.sdo.rendezvous.utils.validators.GuidValidator;
import org.sdo.rendezvous.utils.validators.WaitSecondsValidator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class OwnerSignRequestVerifierTest {

  private static final byte[] NONCE = OwnerSignRequestFactory.NONCE;

  @Mock private GuidValidator guidValidator;

  @Mock private CertChainValidator certChainValidator;

  @Mock private DeviceAttestationService deviceAttestationService;

  @Mock private IOwnershipVoucherVerifier ownershipVoucherVerifier;

  @Mock private OwnerSignDataVerifier ownerSignDataVerifier;

  @Mock private WaitSecondsValidator wsValidator;

  private HashGenerator hashGenerator;
  private OwnerSignRequestVerifier ownerSignRequestVerifier;
  private OwnerSignRequest ownerSignRequest;

  /**
   * Variable initialization.
   * @throws Exception for unhandled exception.
   */
  @BeforeMethod
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    hashGenerator = new HashGenerator();
    ownerSignRequestVerifier =
        new OwnerSignRequestVerifier(
            guidValidator,
            certChainValidator,
            deviceAttestationService,
            ownershipVoucherVerifier,
            ownerSignDataVerifier,
            wsValidator,
            hashGenerator);
    ownerSignRequest = OwnerSignRequestFactory.createOwnerSignRequest();
  }

  @Test
  public void testVerifyPositiveEpid() throws Exception {
    ownerSignRequestVerifier.verify(ownerSignRequest, NONCE);
  }

  @Test
  public void testVerifyPositiveEcdsa() throws Exception {
    Mockito.when(
            deviceAttestationService.getDeviceAttestationMethod(
                ownerSignRequest.getTo0Data().getOwnershipVoucher()))
        .thenReturn(DeviceAttestationMethod.ECDSA);
    ownerSignRequest
        .getTo0Data()
        .getOwnershipVoucher()
        .setCertificateChain(new CertChain(CertType.X509, (short) 0, Collections.emptyList()));

    byte[] deviceCertChainHash =
        hashGenerator.hashSha256(
            JsonUtils.mapObjectToJson(
                    ownerSignRequest.getTo0Data().getOwnershipVoucher().getCertificateChain())
                .getBytes());
    ownerSignRequest
        .getTo0Data()
        .getOwnershipVoucher()
        .getOwnershipVoucherHeader()
        .setDeviceCertChainHash(new Hash(HashType.SHA256, deviceCertChainHash));
    ownerSignRequestVerifier.verify(ownerSignRequest, NONCE);
  }

  @Test(expectedExceptions = InvalidCertChainHashException.class)
  public void testVerifyEcdsaInvalidHash() throws Exception {
    Mockito.when(
            deviceAttestationService.getDeviceAttestationMethod(
                ownerSignRequest.getTo0Data().getOwnershipVoucher()))
        .thenReturn(DeviceAttestationMethod.ECDSA);
    ownerSignRequest
        .getTo0Data()
        .getOwnershipVoucher()
        .setCertificateChain(new CertChain(CertType.X509, (short) 0, Collections.emptyList()));
    ownerSignRequest
        .getTo0Data()
        .getOwnershipVoucher()
        .getOwnershipVoucherHeader()
        .setDeviceCertChainHash(new Hash(HashType.SHA256, new byte[] {0x01}));
    ownerSignRequestVerifier.verify(ownerSignRequest, NONCE);
  }

  @Test
  public void testGetAttestationMethod() throws Exception {
    Mockito.when(
            deviceAttestationService.getDeviceAttestationMethod(
                ownerSignRequest.getTo0Data().getOwnershipVoucher()))
        .thenReturn(DeviceAttestationMethod.ECDSA);
    DeviceAttestationMethod deviceAttestationMethod =
        ownerSignRequestVerifier.getDeviceAttestationMethod(ownerSignRequest);
    assertEquals(deviceAttestationMethod, DeviceAttestationMethod.ECDSA);
  }
}
