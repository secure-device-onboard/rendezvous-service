// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services;

import static org.mockito.ArgumentMatchers.any;

import javax.xml.bind.DatatypeConverter;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.sdo.rendezvous.exceptions.GuidBlacklistedException;
import org.sdo.rendezvous.exceptions.InvalidNonceException;
import org.sdo.rendezvous.exceptions.InvalidProveRequestException;
import org.sdo.rendezvous.exceptions.InvalidSigInfoException;
import org.sdo.rendezvous.exceptions.SdoException;
import org.sdo.rendezvous.model.database.VersionedTO1Data;
import org.sdo.rendezvous.model.requests.to1.HelloSdoRequest;
import org.sdo.rendezvous.model.requests.to1.ProveToSdoRequest;
import org.sdo.rendezvous.model.responses.to1.HelloSdoAckResponse;
import org.sdo.rendezvous.model.types.AppId;
import org.sdo.rendezvous.model.types.Device;
import org.sdo.rendezvous.model.types.Hash;
import org.sdo.rendezvous.model.types.HashType;
import org.sdo.rendezvous.model.types.IpAddress;
import org.sdo.rendezvous.model.types.OwnerSignTO1Data;
import org.sdo.rendezvous.model.types.OwnerSignTO1DataBody;
import org.sdo.rendezvous.model.types.PKECDSAEnc;
import org.sdo.rendezvous.model.types.PKEPIDEnc;
import org.sdo.rendezvous.model.types.PKNull;
import org.sdo.rendezvous.model.types.ProveToSdoBody;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.sdo.rendezvous.model.types.SigInfo;
import org.sdo.rendezvous.model.types.Signature;
import org.sdo.rendezvous.repositories.JedisRepository;
import org.sdo.rendezvous.services.signature.SignatureVerifier;
import org.sdo.rendezvous.utils.ByteConverter;
import org.sdo.rendezvous.utils.validators.GuidValidator;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@PrepareForTest({HelloSdoAckResponse.class})
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*"})
public class TransferOwnership1ServiceTest extends PowerMockTestCase {

  private static final int AI_TYPE = 2;
  private static final byte[] AI_BYTES = DatatypeConverter.parseHexBinary("0000");
  private static final byte[] NONCE = DatatypeConverter.parseHexBinary("1234");
  private static final byte[] GUID =
      DatatypeConverter.parseHexBinary("00000000000000000000000000000001");
  private static final byte[] BLACKLISTED_GUID =
      DatatypeConverter.parseHexBinary("000000000000000000000000000000FF");
  private static final byte[] PK = DatatypeConverter.parseHexBinary("0000");
  private static final byte[] SIGNATURE_BINARY = DatatypeConverter.parseHexBinary("0000");
  private static final byte[] EPID_KEY_BINARY =
      DatatypeConverter.parseHexBinary("00000005AF2121201321100000000005");
  private static final byte[] ECDSA_KEY_BINARY =
      DatatypeConverter.parseHexBinary("212dA2121F212121C21C5FA2121C121B2121212E121FF2");
  private static final String IP_ADDRESS = "127.0.0.1";
  private static final String DNS = "sdo.org";
  private static final int PORT = 8082;
  private static final byte[] HASH_BINARY = DatatypeConverter.parseHexBinary("21F2120005451212");
  private static final SigInfo SIG_INFO_EPID = new SigInfo(PublicKeyType.EPID_1_1, EPID_KEY_BINARY);
  private static final SigInfo SIG_INFO_ECDSA =
      new SigInfo(PublicKeyType.ECDSA_P_256, ECDSA_KEY_BINARY);
  private static final HelloSdoRequest HELLO_REQUEST_EPID =
      new HelloSdoRequest(GUID, SIG_INFO_EPID);
  private static final HelloSdoRequest HELLO_REQUEST_ECDSA =
      new HelloSdoRequest(GUID, SIG_INFO_ECDSA);
  private static final ProveToSdoBody PROVE_BODY =
      new ProveToSdoBody(new AppId(AI_TYPE, AI_BYTES), NONCE, GUID);
  private static final PKEPIDEnc PUBLIC_KEY_EPID =
      new PKEPIDEnc(PublicKeyType.EPID_1_1, EPID_KEY_BINARY);
  private static final PKECDSAEnc PUBLIC_KEY_ECDSA =
      new PKECDSAEnc(PublicKeyType.NONE, ECDSA_KEY_BINARY);
  private static final Signature SIGNATURE = new Signature(SIGNATURE_BINARY);
  private static final ProveToSdoRequest PROVE_REQUEST_EPID =
      new ProveToSdoRequest(PROVE_BODY, PUBLIC_KEY_EPID, SIGNATURE);
  private static final ProveToSdoRequest PROVE_REQUEST_ECDSA =
      new ProveToSdoRequest(PROVE_BODY, PUBLIC_KEY_ECDSA, SIGNATURE);
  private static final PKNull PK_NULL = new PKNull();
  private static final Device DEVICE = new Device(GUID, NONCE);
  private static OwnerSignTO1DataBody OWNER_SIGN_TO_1_DATA_BODY;
  private static OwnerSignTO1Data OWNER_SIGN_TO_1_DATA;
  private static VersionedTO1Data VERSIONED_TO1_DATA;

  private TransferOwnership1Service transferOwnership1Service;

  @Mock private GuidValidator guidValidator;

  @Mock private EpidMaterialService epidMaterialService;

  @Mock private JedisRepository jedisRepository;

  @Mock private SignatureVerifier signatureVerifier;

  @Mock private PKNull wrongPk;

  @Mock private HelloSdoAckResponse helloSdoAckResponse;

  @BeforeMethod
  public void beforeMethod() throws Exception {
    MockitoAnnotations.initMocks(this);
    PowerMockito.mockStatic(HelloSdoAckResponse.class);
    PowerMockito.when(HelloSdoAckResponse.generateInstance(any())).thenReturn(helloSdoAckResponse);
    transferOwnership1Service =
        new TransferOwnership1Service(
            guidValidator, epidMaterialService, jedisRepository, signatureVerifier);
    OWNER_SIGN_TO_1_DATA_BODY =
        new OwnerSignTO1DataBody(
            new IpAddress(IP_ADDRESS), DNS, PORT, new Hash(HashType.SHA256, HASH_BINARY));
    OWNER_SIGN_TO_1_DATA = new OwnerSignTO1Data(OWNER_SIGN_TO_1_DATA_BODY, PK_NULL, SIGNATURE);
    VERSIONED_TO1_DATA = new VersionedTO1Data(OWNER_SIGN_TO_1_DATA);

    Mockito.when(epidMaterialService.getSigInfo(HELLO_REQUEST_EPID.getSigInfo()))
        .thenReturn(SIG_INFO_EPID);
    Mockito.when(jedisRepository.getVersionedTO1Data(ByteConverter.getGuidFromByteArray(GUID)))
        .thenReturn(VERSIONED_TO1_DATA);
    Mockito.when(wrongPk.getPkType()).thenReturn(PublicKeyType.DH);
  }

  @Test
  public void testGetHelloSdoAckResponseEpid() throws Exception {
    HelloSdoAckResponse response =
        transferOwnership1Service.getHelloSdoAckResponse(HELLO_REQUEST_EPID);
    Assert.assertEquals(response, helloSdoAckResponse);
  }

  @Test
  public void testGetHelloSdoAckResponseEcdsa() throws Exception {
    HelloSdoAckResponse response =
        transferOwnership1Service.getHelloSdoAckResponse(HELLO_REQUEST_ECDSA);
    Assert.assertEquals(response, helloSdoAckResponse);
  }

  @Test(expectedExceptions = GuidBlacklistedException.class)
  public void testGetHelloSdoAckResponseGuidBlacklisted() throws Exception {
    Mockito.doThrow(GuidBlacklistedException.class)
        .when(guidValidator)
        .verifyAgainstBlackList(BLACKLISTED_GUID);
    HelloSdoRequest requestWithBlacklistedGuid =
        new HelloSdoRequest(BLACKLISTED_GUID, SIG_INFO_EPID);
    transferOwnership1Service.getHelloSdoAckResponse(requestWithBlacklistedGuid);
  }

  @Test(expectedExceptions = InvalidSigInfoException.class)
  public void testGetHelloSdoAckResponseInvalidSigInfo() throws Exception {
    SigInfo invalidSigInfo = new SigInfo(PublicKeyType.RSA2048RESTR, ECDSA_KEY_BINARY);
    Mockito.doThrow(InvalidSigInfoException.class)
        .when(epidMaterialService)
        .getSigInfo(invalidSigInfo);
    HelloSdoRequest requestWithInvalidSigInfo = new HelloSdoRequest(GUID, invalidSigInfo);
    transferOwnership1Service.getHelloSdoAckResponse(requestWithInvalidSigInfo);
  }

  @Test
  public void testGetProveToSdoResponseEpid() throws Exception {
    OwnerSignTO1Data response =
        transferOwnership1Service.getProveToSdoResponse(DEVICE, PROVE_REQUEST_EPID);
    Assert.assertEquals(response.getBody(), OWNER_SIGN_TO_1_DATA_BODY);
    Assert.assertEquals(response.getPubKey(), PK_NULL);
    Assert.assertEquals(response.getSignature(), SIGNATURE);
  }

  @Test
  public void testGetProveToSdoResponseEcdsa() throws Exception {
    OwnerSignTO1Data response =
        transferOwnership1Service.getProveToSdoResponse(DEVICE, PROVE_REQUEST_ECDSA);
    Assert.assertEquals(response.getBody(), OWNER_SIGN_TO_1_DATA_BODY);
    Assert.assertEquals(response.getPubKey(), PK_NULL);
    Assert.assertEquals(response.getSignature(), SIGNATURE);
  }

  @Test(expectedExceptions = InvalidProveRequestException.class)
  public void testGetProveToSdoResponseGuidInJwtAndRequestDifferent() throws Exception {
    Device deviceDifferentGuid =
        new Device(NONCE, DatatypeConverter.parseHexBinary("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"));
    transferOwnership1Service.getProveToSdoResponse(deviceDifferentGuid, PROVE_REQUEST_ECDSA);
  }

  @Test(expectedExceptions = InvalidNonceException.class)
  public void testGetProveToSdoResponseNonceInJwtAndRequestDifferent() throws Exception {
    Device deviceDifferentNonce = new Device(GUID, DatatypeConverter.parseHexBinary("9999"));
    transferOwnership1Service.getProveToSdoResponse(deviceDifferentNonce, PROVE_REQUEST_ECDSA);
  }

  @Test(expectedExceptions = SdoException.class)
  public void testGetProveToSdoResponseInvalidPubKeyType() throws Exception {
    ProveToSdoRequest requestWithInvalidKeyType =
        new ProveToSdoRequest(
            PROVE_REQUEST_ECDSA.getProveToSdoBody(), wrongPk, PROVE_REQUEST_ECDSA.getSignature());
    Mockito.doThrow(SdoException.class)
        .when(signatureVerifier)
        .verify(
            requestWithInvalidKeyType.getProveToSdoBody(),
            requestWithInvalidKeyType.getPublicKey(),
            requestWithInvalidKeyType.getSignature().getBytes());
    transferOwnership1Service.getProveToSdoResponse(DEVICE, requestWithInvalidKeyType);
  }

  @Test(expectedExceptions = SdoException.class)
  public void testGetProveToSdoResponseInvalidSignature() throws Exception {
    ProveToSdoRequest requestWithInvalidSignature =
        new ProveToSdoRequest(
            PROVE_REQUEST_EPID.getProveToSdoBody(),
            PROVE_REQUEST_EPID.getPublicKey(),
            new Signature(PK));
    Mockito.doThrow(SdoException.class)
        .when(signatureVerifier)
        .verify(
            requestWithInvalidSignature.getProveToSdoBody(),
            requestWithInvalidSignature.getPublicKey(),
            requestWithInvalidSignature.getSignature().getBytes());
    transferOwnership1Service.getProveToSdoResponse(DEVICE, requestWithInvalidSignature);
  }
}
