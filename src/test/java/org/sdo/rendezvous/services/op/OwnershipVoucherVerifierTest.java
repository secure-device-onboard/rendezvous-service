// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services.op;

import static org.mockito.ArgumentMatchers.any;
import static org.sdo.rendezvous.utils.JsonUtils.mapObjectToJson;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.xml.bind.DatatypeConverter;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sdo.rendezvous.OwnerSignRequestFactory;
import org.sdo.rendezvous.authenticators.Authenticator;
import org.sdo.rendezvous.authenticators.AuthenticatorFactory;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.crypto.HashGenerator;
import org.sdo.rendezvous.exceptions.InvalidOwnershipVoucherException;
import org.sdo.rendezvous.exceptions.InvalidSignatureException;
import org.sdo.rendezvous.model.beans.WhitelistedHashes;
import org.sdo.rendezvous.model.types.Hash;
import org.sdo.rendezvous.model.types.HashType;
import org.sdo.rendezvous.model.types.Hmac;
import org.sdo.rendezvous.model.types.OwnershipVoucher;
import org.sdo.rendezvous.model.types.PkNull;
import org.sdo.rendezvous.model.types.PkX509Enc;
import org.sdo.rendezvous.model.types.PubKey;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.sdo.rendezvous.model.types.Signature;
import org.sdo.rendezvous.utils.validators.OvPublicKeyTrustValidator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class OwnershipVoucherVerifierTest {

  private static final Hash INVALID_HASH =
      new Hash(
          HashType.SHA256,
          DatatypeConverter.parseHexBinary(
              "FBF6C7A65CB4DDE75319B61E63B04120350FD70A3B4F193E36039747C6ACCE7A"));
  private static final Signature INVALID_SIGNATURE =
      new Signature(
          DatatypeConverter.parseHexBinary(
              "5CE7F22F69B2C1C8B2A20ED9DD8F0E544CBF83529355B6AFD5200282E832D33F"));
  private static final Hmac INVALID_HMAC =
      new Hmac(
          HashType.HMAC_SHA256,
          DatatypeConverter.parseHexBinary(
              "B8EC9DA29F09E714B7511C0B32DEA13D7FC756C994AF9F2FC34BE98C4F71581D"));
  private static final Hash INVALID_GUID_DEVICE_HASH =
      new Hash(
          HashType.SHA384,
          DatatypeConverter.parseHexBinary(
              "4900D4F7AC5DD55CD8E7845371C1BD08E7987784E1607BA10D9E4C962CAF935AE052B34"
                  + "85C4068E49AFF49BABCBB21C317EE7ACF8D47F41C1A48F73488CA56AB"));
  private static final PubKey INVALID_PUBKEY = new PkNull();
  private OwnershipVoucher ownershipVoucher;
  @Mock private RendezvousConfig rendezvousConfig;

  @Mock private OvPublicKeyTrustValidator ovPublicKeyTrustValidator;

  @Mock private AuthenticatorFactory authenticatorFactory;

  @Mock private Authenticator authenticator;

  @Mock private WhitelistedHashes whitelistedHashes;

  private HashGenerator hashGenerator;
  private IOwnershipVoucherVerifier ownershipVoucherVerifier;


  /**
   * Variable initialization.
   * @throws Exception for InvalidOwnershipVoucherException, InvalidPublicKeyTypeException
   */
  @BeforeMethod
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    Mockito.when(rendezvousConfig.isOpKeyVerification()).thenReturn(true);
    Mockito.when(rendezvousConfig.getOwnershipVoucherMaxEntries()).thenReturn(2);
    Mockito.when(ovPublicKeyTrustValidator.verify(any())).thenReturn(true);
    Mockito.when(authenticatorFactory.getAuthenticator(any())).thenReturn(authenticator);

    hashGenerator = new HashGenerator();
    ownershipVoucherVerifier =
        new OwnershipVoucherVerifier(
            rendezvousConfig,
            ovPublicKeyTrustValidator,
            authenticatorFactory,
            whitelistedHashes,
            hashGenerator);
    ownershipVoucher = OwnerSignRequestFactory.createValidOwnershipVoucher();
  }

  @Test
  public void testVerifyPositive() throws Exception {
    ownershipVoucherVerifier.verify(ownershipVoucher);
  }

  @Test
  public void testVerifyOpKeyVerificationDisabled() throws Exception {
    Mockito.when(rendezvousConfig.isOpKeyVerification()).thenReturn(false);
    ownershipVoucher
        .getOwnershipVoucherEntries()[0]
        .getOwnershipVoucherEntryBody()
        .setGuidDeviceInfoHash(INVALID_GUID_DEVICE_HASH);
    ownershipVoucherVerifier.verify(ownershipVoucher);
  }

  @Test(expectedExceptions = InvalidOwnershipVoucherException.class)
  public void testVerifyNegativePublicKeyNotTrusted() throws Exception {
    Mockito.when(ovPublicKeyTrustValidator.verify(any())).thenReturn(false);
    ownershipVoucherVerifier.verify(ownershipVoucher);
  }

  @Test(expectedExceptions = InvalidOwnershipVoucherException.class)
  public void testVerifyExceedingMaxNumberOfEntries() throws Exception {
    Mockito.when(rendezvousConfig.getOwnershipVoucherMaxEntries()).thenReturn(1);
    ownershipVoucherVerifier.verify(ownershipVoucher);
  }

  @Test(expectedExceptions = InvalidOwnershipVoucherException.class)
  public void testVerifyInvalidHash() throws Exception {
    ownershipVoucher
        .getOwnershipVoucherEntries()[0]
        .getOwnershipVoucherEntryBody()
        .setPreviousEntryHash(INVALID_HASH);
    ownershipVoucherVerifier.verify(ownershipVoucher);
  }

  @Test(expectedExceptions = InvalidOwnershipVoucherException.class)
  public void testVerifyInvalidSignatureFirstEntry() throws Exception {
    Mockito.doThrow(InvalidSignatureException.class)
        .when(authenticator)
        .authenticate(
            INVALID_SIGNATURE.getBytes(),
            mapObjectToJson(
                    ownershipVoucher.getOwnershipVoucherEntries()[0].getOwnershipVoucherEntryBody(),
                    JsonInclude.Include.NON_NULL)
                .getBytes(),
            ownershipVoucher
                .getOwnershipVoucherHeader()
                .getManufacturerPublicKey()
                .asJavaPublicKey());
    ownershipVoucher.getOwnershipVoucherEntries()[0].setSignature(INVALID_SIGNATURE);
    ownershipVoucherVerifier.verify(ownershipVoucher);
  }

  @Test(expectedExceptions = InvalidOwnershipVoucherException.class)
  public void testVerifyInvalidSignatureSecondEntry() throws Exception {
    Mockito.doThrow(InvalidSignatureException.class)
        .when(authenticator)
        .authenticate(
            INVALID_SIGNATURE.getBytes(),
            mapObjectToJson(
                    ownershipVoucher.getOwnershipVoucherEntries()[1].getOwnershipVoucherEntryBody(),
                    JsonInclude.Include.NON_NULL)
                .getBytes(),
            ownershipVoucher
                .getOwnershipVoucherEntries()[0]
                .getOwnershipVoucherEntryBody()
                .getPublicKey()
                .asJavaPublicKey());
    ownershipVoucher.getOwnershipVoucherEntries()[1].setSignature(INVALID_SIGNATURE);
    ownershipVoucherVerifier.verify(ownershipVoucher);
  }

  @Test(expectedExceptions = InvalidOwnershipVoucherException.class)
  public void testVerifyInvalidNumberOfEntries() throws Exception {
    ownershipVoucher.setNumberOfEntries((short) 4);
    ownershipVoucherVerifier.verify(ownershipVoucher);
  }

  @Test(expectedExceptions = InvalidOwnershipVoucherException.class)
  public void testVerifyInvalidHmac() throws Exception {
    ownershipVoucher.setHmac(INVALID_HMAC);
    ownershipVoucherVerifier.verify(ownershipVoucher);
  }

  @Test(expectedExceptions = InvalidOwnershipVoucherException.class)
  public void testVerifyInvalidHeader() throws Exception {
    ownershipVoucher.getOwnershipVoucherHeader().setKeyEncoding((short) 7);
    ownershipVoucherVerifier.verify(ownershipVoucher);
  }

  @Test(expectedExceptions = InvalidOwnershipVoucherException.class)
  public void testVerifyInvalidEntryBody() throws Exception {
    ownershipVoucher
        .getOwnershipVoucherEntries()[0]
        .getOwnershipVoucherEntryBody()
        .setGuidDeviceInfoHash(INVALID_GUID_DEVICE_HASH);
    ownershipVoucherVerifier.verify(ownershipVoucher);
  }

  @Test(expectedExceptions = InvalidOwnershipVoucherException.class)
  public void testVerifyInvalidPublicKeyInLastEntry() throws Exception {
    ownershipVoucher
        .getOwnershipVoucherEntries()[1]
        .getOwnershipVoucherEntryBody()
        .setPublicKey(INVALID_PUBKEY);
    ownershipVoucherVerifier.verify(ownershipVoucher);
  }

  @Test(expectedExceptions = InvalidOwnershipVoucherException.class)
  public void testVerifyPubKeyMissmatch() throws Exception {
    ownershipVoucher
        .getOwnershipVoucherEntries()[1]
        .getOwnershipVoucherEntryBody()
        .setPublicKey(new PkX509Enc(PublicKeyType.ECDSA_P_256, new byte[] {}));
    ownershipVoucherVerifier.verify(ownershipVoucher);
  }
}
