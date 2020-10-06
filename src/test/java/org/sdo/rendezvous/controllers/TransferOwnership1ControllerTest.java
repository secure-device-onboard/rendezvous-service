// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.controllers;

import static org.mockito.ArgumentMatchers.any;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.controllers.transferownership.TransferOwnership1Controller;
import org.sdo.rendezvous.crypto.To1JwTokenFactory;
import org.sdo.rendezvous.enums.AttributeName;
import org.sdo.rendezvous.exceptions.GuidDenylistException;
import org.sdo.rendezvous.exceptions.InvalidSigInfoException;
import org.sdo.rendezvous.exceptions.SdoException;
import org.sdo.rendezvous.model.beans.DeviceInfo;
import org.sdo.rendezvous.model.requests.to1.HelloSdoRequest;
import org.sdo.rendezvous.model.requests.to1.ProveToSdoRequest;
import org.sdo.rendezvous.model.responses.to1.HelloSdoAckResponse;
import org.sdo.rendezvous.model.types.AppId;
import org.sdo.rendezvous.model.types.Device;
import org.sdo.rendezvous.model.types.OwnerSignTo1Data;
import org.sdo.rendezvous.model.types.OwnerSignTo1DataBody;
import org.sdo.rendezvous.model.types.PkEcdsaEnc;
import org.sdo.rendezvous.model.types.PkEpidEnc;
import org.sdo.rendezvous.model.types.PkNull;
import org.sdo.rendezvous.model.types.ProveToSdoBody;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.sdo.rendezvous.model.types.SigInfo;
import org.sdo.rendezvous.model.types.Signature;
import org.sdo.rendezvous.services.TokenParserService;
import org.sdo.rendezvous.services.TransferOwnership1Service;
import org.sdo.rendezvous.utils.ResponseUtils;
import org.sdo.rendezvous.utils.SessionAttributeHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@PrepareForTest({RequestContextHolder.class, SessionAttributeHolder.class})
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*"})
public class TransferOwnership1ControllerTest extends PowerMockTestCase {

  private static final byte[] GUID =
      DatatypeConverter.parseHexBinary("00000000000000000000000000000001");
  private static final byte[] INVALID_GUID =
      DatatypeConverter.parseHexBinary("000000000000000000000000000000FF");
  private static final byte[] EPID_KEY_BINARY =
      DatatypeConverter.parseHexBinary("00000005AF2121201321100000000005");
  private static final byte[] ECDSA_KEY_BINARY =
      DatatypeConverter.parseHexBinary("212dA2121F212121C21C5FA2121C121B2121212E121FF2");
  private static final SigInfo SIG_INFO_EPID = new SigInfo(PublicKeyType.EPID_1_1, EPID_KEY_BINARY);
  private static final SigInfo SIG_INFO_ECDSA =
      new SigInfo(PublicKeyType.ECDSA_P_256, ECDSA_KEY_BINARY);
  private static final SigInfo INVALID_SIG_INFO =
      new SigInfo(PublicKeyType.RSA2048RESTR, ECDSA_KEY_BINARY);
  private static final HelloSdoRequest HELLO_SDO_REQUEST_EPID =
      new HelloSdoRequest(GUID, SIG_INFO_EPID);
  private static final HelloSdoRequest HELLO_SDO_REQUEST_ECDSA =
      new HelloSdoRequest(GUID, SIG_INFO_ECDSA);
  private static final int AI_TYPE = 2;
  private static final byte[] AI_BYTES = DatatypeConverter.parseHexBinary("0000");
  private static final byte[] NONCE = DatatypeConverter.parseHexBinary("1234");
  private static final ProveToSdoBody PROVE_BODY =
      new ProveToSdoBody(new AppId(AI_TYPE, AI_BYTES), NONCE, GUID);
  private static final PkEpidEnc PUBLIC_KEY_EPID =
      new PkEpidEnc(PublicKeyType.EPID_1_1, EPID_KEY_BINARY);
  private static final PkEcdsaEnc PUBLIC_KEY_ECDSA =
      new PkEcdsaEnc(PublicKeyType.ECDSA_P_256, ECDSA_KEY_BINARY);
  private static final byte[] SIGNATURE_BINARY = DatatypeConverter.parseHexBinary("0000");
  private static final Signature SIGNATURE = new Signature(SIGNATURE_BINARY);
  private static final ProveToSdoRequest PROVE_REQUEST_EPID =
      new ProveToSdoRequest(PROVE_BODY, PUBLIC_KEY_EPID, SIGNATURE);
  private static final ProveToSdoRequest PROVE_REQUEST_ECDSA =
      new ProveToSdoRequest(PROVE_BODY, PUBLIC_KEY_ECDSA, SIGNATURE);
  private static final OwnerSignTo1Data OWNER_DATA =
      new OwnerSignTo1Data(new OwnerSignTo1DataBody(), new PkNull(), SIGNATURE);
  private static final Device DEVICE = new Device(GUID, NONCE);
  private static HelloSdoAckResponse HELLO_RESPONSE_EPID;
  private static HelloSdoAckResponse HELLO_RESPONSE_ECDSA;
  @Mock private DeviceInfo deviceInfo;

  @Mock private HttpServletResponse httpServletResponse;

  @Mock private RendezvousConfig rendezvousConfig;

  @Mock private TransferOwnership1Service transferOwnership1Service;

  @Mock private TokenParserService tokenParserService;

  @Mock private To1JwTokenFactory to1JwtokenFactory;

  private TransferOwnership1Controller controller;

  /**
   * Variable initialization.
   * @throws Exception for exceptions: InvalidGuidException, InvalidSigInfoException,
   *                    GuidDenylistException, InvalidGroupIdException
   */
  @BeforeMethod
  public void setUp() throws Exception {
    PowerMockito.mockStatic(RequestContextHolder.class);
    PowerMockito.mockStatic(SessionAttributeHolder.class);
    PowerMockito.when(SessionAttributeHolder.getAttributeValue(AttributeName.SESSION_ID))
        .thenReturn(DatatypeConverter.printHexBinary(NONCE));
    MockitoAnnotations.initMocks(this);
    controller =
        new TransferOwnership1Controller(
            deviceInfo,
            httpServletResponse,
            rendezvousConfig,
            transferOwnership1Service,
            tokenParserService,
            to1JwtokenFactory);
    HELLO_RESPONSE_EPID = HelloSdoAckResponse.generateInstance(SIG_INFO_EPID);
    HELLO_RESPONSE_ECDSA = HelloSdoAckResponse.generateInstance(SIG_INFO_ECDSA);
    Mockito.when(deviceInfo.getGuid()).thenReturn(GUID);
    Mockito.when(deviceInfo.getNonce()).thenReturn(NONCE);
    Mockito.when(transferOwnership1Service.getHelloSdoAckResponse(HELLO_SDO_REQUEST_EPID))
        .thenReturn(HELLO_RESPONSE_EPID);
    Mockito.when(transferOwnership1Service.getHelloSdoAckResponse(HELLO_SDO_REQUEST_ECDSA))
        .thenReturn(HELLO_RESPONSE_ECDSA);
    Mockito.when(transferOwnership1Service.getProveToSdoResponse(DEVICE, PROVE_REQUEST_ECDSA))
        .thenReturn(OWNER_DATA);
    Mockito.when(transferOwnership1Service.getProveToSdoResponse(DEVICE, PROVE_REQUEST_EPID))
        .thenReturn(OWNER_DATA);
  }

  @Test
  public void testhelloSdoEpid() throws Exception {
    ResponseEntity<String> response = controller.helloSdo(HELLO_SDO_REQUEST_EPID);
    Assert.assertEquals(
        response.getBody(),
        ResponseUtils.createJsonResponse(HELLO_RESPONSE_EPID, HttpStatus.OK).getBody());
  }

  @Test
  public void testhelloSdoEcdsa() throws Exception {
    ResponseEntity<String> response = controller.helloSdo(HELLO_SDO_REQUEST_ECDSA);
    Assert.assertEquals(
        response.getBody(),
        ResponseUtils.createJsonResponse(HELLO_RESPONSE_ECDSA, HttpStatus.OK).getBody());
  }

  @Test(expectedExceptions = GuidDenylistException.class)
  public void testhelloSdoInvalidGuid() throws Exception {
    HelloSdoRequest helloRequestWithInvalidGuid = new HelloSdoRequest(INVALID_GUID, SIG_INFO_ECDSA);
    Mockito.doThrow(GuidDenylistException.class)
        .when(transferOwnership1Service)
        .getHelloSdoAckResponse(helloRequestWithInvalidGuid);

    controller.helloSdo(helloRequestWithInvalidGuid);
  }

  @Test(expectedExceptions = InvalidSigInfoException.class)
  public void testhelloSdoInvalidSigInfo() throws Exception {
    HelloSdoRequest helloRequestWithInvalidSigInfo = new HelloSdoRequest(GUID, INVALID_SIG_INFO);
    Mockito.doThrow(InvalidSigInfoException.class)
        .when(transferOwnership1Service)
        .getHelloSdoAckResponse(helloRequestWithInvalidSigInfo);

    controller.helloSdo(helloRequestWithInvalidSigInfo);
  }

  @Test
  public void testProveToSdoEpid() throws Exception {
    ResponseEntity<String> response = controller.proveToSdo(PROVE_REQUEST_EPID);
    Assert.assertEquals(
        response.getBody(), ResponseUtils.createJsonResponse(OWNER_DATA, HttpStatus.OK).getBody());
  }

  @Test
  public void testProveToSdoEcdsa() throws Exception {
    ResponseEntity<String> response = controller.proveToSdo(PROVE_REQUEST_ECDSA);
    Assert.assertEquals(
        response.getBody(), ResponseUtils.createJsonResponse(OWNER_DATA, HttpStatus.OK).getBody());
  }

  @Test(expectedExceptions = SdoException.class)
  public void testProveToSdoServiceThrowsSdoException() throws Exception {
    Mockito.doThrow(SdoException.class)
        .when(transferOwnership1Service)
        .getProveToSdoResponse(any(Device.class), any(ProveToSdoRequest.class));
    controller.proveToSdo(PROVE_REQUEST_ECDSA);
  }
}
