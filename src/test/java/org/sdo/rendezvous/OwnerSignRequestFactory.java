// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous;

import javax.xml.bind.DatatypeConverter;
import org.sdo.rendezvous.model.requests.to0.OwnerSignRequest;
import org.sdo.rendezvous.model.types.Hash;
import org.sdo.rendezvous.model.types.HashType;
import org.sdo.rendezvous.model.types.Hmac;
import org.sdo.rendezvous.model.types.IpAddress;
import org.sdo.rendezvous.model.types.OwnerSignTo0Data;
import org.sdo.rendezvous.model.types.OwnerSignTo1Data;
import org.sdo.rendezvous.model.types.OwnerSignTo1DataBody;
import org.sdo.rendezvous.model.types.OwnershipVoucher;
import org.sdo.rendezvous.model.types.OwnershipVoucherEntry;
import org.sdo.rendezvous.model.types.OwnershipVoucherEntryBody;
import org.sdo.rendezvous.model.types.OwnershipVoucherHeader;
import org.sdo.rendezvous.model.types.PkNull;
import org.sdo.rendezvous.model.types.PkRmeEnc;
import org.sdo.rendezvous.model.types.PublicKeyType;
import org.sdo.rendezvous.model.types.RendezvousInfo;
import org.sdo.rendezvous.model.types.RendezvousInstr;
import org.sdo.rendezvous.model.types.Signature;

public class OwnerSignRequestFactory {

  public static final byte[] NONCE =
      DatatypeConverter.parseHexBinary("AABBCCDDAABBCCDDAABBCCDDAABBCCDD");

  private OwnerSignRequestFactory() {

  }

  /**
   * Creating OwnerSignRequest message.
   * @return OwnerSignRequest
   * @throws Exception for unhandled exceptions
   */
  public static OwnerSignRequest createOwnerSignRequest() throws Exception {
    OwnerSignTo0Data to0Data = new OwnerSignTo0Data();
    to0Data.setOwnershipVoucher(createValidOwnershipVoucher());
    to0Data.setWaitSeconds(123);
    to0Data.setNonce(NONCE);

    OwnerSignTo1Data to1Data = new OwnerSignTo1Data();
    to1Data.setPubKey(new PkNull());
    to1Data.setBody(new OwnerSignTo1DataBody());
    to1Data.getBody().setDns("du.da.com");
    to1Data.getBody().setIpAddress(new IpAddress("127.0.0.1"));
    to1Data
        .getBody()
        .setTo0DataHash(
            new Hash(
                HashType.SHA256,
                DatatypeConverter.parseHexBinary(
                    "21EA3FB639F51DA65A7A628B52783BE0653D54C9ECAE16441CCB8C2E1035E138")));

    byte[] bodySignature =
        DatatypeConverter.parseHexBinary(
            "12537FD5B73CDA24C66B06FEA39F8DF5D79BB7333F66B50769D9AE3C34CF16DC0020489FE5F6269"
                + "BFACF2834BD9D78D3D2AC75D75D169830C252B1968B55C20E9C189A6F82E78B838AB518520C"
                + "ED3FFB65609E137615578F08265BC3C8A3A7EC15CD84705B03360ACF638664A6CE69682ABDD"
                + "685A2DD43D5011198A5169267F736E178E04A1CC0ED32F34697FE82E745F25E4C2839D6531C"
                + "8887704211AE86A0AB8C995D6C39AE7427D56E1881053741602A4513AECA0E4FB81CA4D3776"
                + "1B680E3C43D8B40ECB6CE7A5E689248F7AB16BD68E1B182B6F3A7740A13535F9335C0CA9CE2"
                + "FDE9D13E75B83C635D8EF32CEF55C4236D3E1E3152113BD621C948593A");

    to1Data.setSignature(new Signature(bodySignature));

    OwnerSignRequest request = new OwnerSignRequest();
    request.setTo0Data(to0Data);
    request.setTo1Data(to1Data);

    return request;
  }

  /**
   * Creating ownership voucher.
   * @return OwnershipVoucher
   */
  public static OwnershipVoucher createValidOwnershipVoucher() {

    RendezvousInstr rendezvousInstr = new RendezvousInstr();
    rendezvousInstr.setIpAddress(new IpAddress("127.0.0.1"));
    rendezvousInstr.setPortDevice(8080);
    rendezvousInstr.setDns("DNSNAME");
    rendezvousInstr.setMedium("eth0");
    RendezvousInfo rendezvousInfo = new RendezvousInfo();
    rendezvousInfo.add(rendezvousInstr);

    OwnershipVoucherHeader ownershipVoucherHeader = new OwnershipVoucherHeader();
    ownershipVoucherHeader.setProtocolVersion(1);
    ownershipVoucherHeader.setKeyEncoding((short) 3);

    String deviceInfo = "mp-widget25";
    ownershipVoucherHeader.setDeviceInfo(deviceInfo);

    byte[] guid = DatatypeConverter.parseHexBinary("fb59d91b95ac43feecf1c105c713d16f");
    ownershipVoucherHeader.setGuid(guid);

    PkRmeEnc manufacturerPubKey = getManufacturerPubKey();
    ownershipVoucherHeader.setManufacturerPublicKey(manufacturerPubKey);
    ownershipVoucherHeader.setRendezvousInfo(rendezvousInfo);

    // Manufacturer To Distributor entry
    OwnershipVoucherEntryBody manufacturerToDistributorEntryBody = new OwnershipVoucherEntryBody();

    PkRmeEnc distributorPubKey = getDistributorPubKey();
    manufacturerToDistributorEntryBody.setPublicKey(distributorPubKey);
    manufacturerToDistributorEntryBody.setGuidDeviceInfoHash(
        new Hash(HashType.SHA256, new byte[0]));
    manufacturerToDistributorEntryBody.setPreviousEntryHash(
        new Hash(
            HashType.SHA256,
            DatatypeConverter.parseHexBinary(
                "8C1B19C1AD703A6352F69751BBF786D75A3DDB6A1F76C206B48AC9751B6CDA89")));

    OwnershipVoucherEntry manufacturerToDistributorEntry = new OwnershipVoucherEntry();
    manufacturerToDistributorEntry.setOwnershipVoucherEntryBody(manufacturerToDistributorEntryBody);
    manufacturerToDistributorEntry.setPubKey(new PkNull());
    byte[] manufacturerToDistributorEntrySignature =
        DatatypeConverter.parseHexBinary(
            "80117F8435773BA553CB9B9CD37BC014917C31A2C59C84A60AB3352F7BD57CEDD1873D580F48670"
                + "A1FDA72C16F9360BEC261E4092A01E01139F7A658DC693E187187240B4BC24B6A1810365B92"
                + "F16DE80744D3AECFBD87E5B6053415805D62D8E6644F1C34D8B9AA1883CC64BA50759163D41"
                + "3348183B6B21BDC20AFC61A53B7B5B6AFF9652E7740B482E9771F9A00B09516F457C5138022"
                + "08CA3E512E2BA381E607D9EFE59AA36986A885C7F06E0E2ED8F784CCDB470E5CA2840B764AD"
                + "CDF8EE1218DC978BDE2DD8C347C4F4E0AD9E94DC64A1A963DBE6AF9AFB9D95E2886283EA77B"
                + "A632CB7DDFA3FCCF25EBC7D8B6E62CB06C3775DFF4686687E48B179FCB");

    manufacturerToDistributorEntry.setSignature(
        new Signature(manufacturerToDistributorEntrySignature));

    // Distributor to Owner entry

    OwnershipVoucherEntryBody distributorToOwnerEntryBody = new OwnershipVoucherEntryBody();

    PkRmeEnc ownerPubKey = getOwnerPubKey();
    distributorToOwnerEntryBody.setPublicKey(ownerPubKey);
    distributorToOwnerEntryBody.setGuidDeviceInfoHash(new Hash(HashType.SHA256, new byte[0]));
    distributorToOwnerEntryBody.setPreviousEntryHash(
        new Hash(
            HashType.SHA256,
            DatatypeConverter.parseHexBinary(
                "518695C686178628CC23D9DD6347577BAEA6B79D64756534ECEBDB12B2894370")));

    OwnershipVoucherEntry distributorToOwnerEntry = new OwnershipVoucherEntry();
    distributorToOwnerEntry.setOwnershipVoucherEntryBody(distributorToOwnerEntryBody);
    distributorToOwnerEntry.setPubKey(new PkNull());
    byte[] distributorToOwnerEntrySignature =
        DatatypeConverter.parseHexBinary(
            "08FF275BBA084C14C11F9ADD381DACFE49E1A6D35A7DF86C45CF9E27ADFEC90E74E9256D51C954F42D"
                + "5AF5516B13F5143B540B8A5A74D81119EB6C5B7ADE3B7AAE81BCBE6E516DA2808CE3AB436D9194"
                + "0B4D450D184D80188EC94E5A2F4C23E315993D3612BBBE1438115343AACA7C5983E790C29DEF31"
                + "28041C6998A3552B525B569A13002732CA0D9408255F69B63CE58BA406205694A5EC551A79D5F5"
                + "3C94F905BE674D1FB9CA998B30E3F2591927B2B3B1D2517C05E9DE26B4F05D027D66185968257E"
                + "0BF795E4C0997F75D1B26BA4E6BE561A41F4B6AF3BA0B4BC24E2C781A299377B808764CBAF790C"
                + "30A87BEDBD32A9769A02ED6626D4981CA21DE6FF");

    distributorToOwnerEntry.setSignature(new Signature(distributorToOwnerEntrySignature));

    // Set entries
    OwnershipVoucherEntry[] ownershipVoucherEntries =
        new OwnershipVoucherEntry[] {manufacturerToDistributorEntry, distributorToOwnerEntry};

    OwnershipVoucher ownershipVoucher = new OwnershipVoucher();
    ownershipVoucher.setHmac(new Hmac(HashType.SHA256, new byte[0]));
    ownershipVoucher.setOwnershipVoucherEntries(ownershipVoucherEntries);
    ownershipVoucher.setNumberOfEntries((short) ownershipVoucherEntries.length);
    ownershipVoucher.setOwnershipVoucherHeader(ownershipVoucherHeader);

    return ownershipVoucher;
  }

  private static PkRmeEnc getManufacturerPubKey() {
    byte[] manufacturerPkModulus =
        DatatypeConverter.parseHexBinary(
            "0096eb0862670544f9fde47c8e4f7651dc58bddd04155b89c85caae7527138fabd3231c3e0736175cad"
                + "b4fae6ed892b2e8af1ac7cee4838d5ce416dd33043b3133612948278f216c1104b02926048bffae"
                + "14b2ef2f5a4712ad2d8b1826a59d84f700f056818650610fd4f31b0e317e28f0c88ca4d40fa289d"
                + "13d2d2dcb9217b141e3dc340b237723e57312452e48fa675373b7a6bfef7167c1ba29c99c06a123"
                + "99fe66b260470262364df74ad15de34b34b147cc6fac7cabcc9a746ba037ef145af772373198479"
                + "c9c94e160d06cb2c75fe62ceaf1cec1205c8248140b3f138fe5ab4a334bc973a501e6821a63cde5"
                + "e5879ba16908e7bdcc1b1e6cf2afab4adb61");

    byte[] manufacturerPkExp = DatatypeConverter.parseHexBinary("010001");
    return new PkRmeEnc(PublicKeyType.RSA2048RESTR, manufacturerPkModulus, manufacturerPkExp);
  }

  private static PkRmeEnc getDistributorPubKey() {
    byte[] distributorPkModulus =
        DatatypeConverter.parseHexBinary(
            "00878fe4722b44dd12c89a4cd1fe29cf17d0fc60da9052fd19131f9e39b9855cf2eae60411e446ad2d57"
                + "34c9179564beb27d5b85c808caedea55e891ba3f2043b14341eec85eb44e6af317f2c095b71b8cfd"
                + "d9c01a8b0818bc5906876e100b8ac4edaa028ae7e15f302d23e8da981f7083760f380b43721f7db9"
                + "467e9c2ff5ef688a6af02bf38f99d0930f89ff41428411d9452e1df8f01e9b687d5221a98ba67945"
                + "f604265d9411d609d4873b487e8abfe528defa1d6854e08ead26d7bdc26b3c05af9e1fdd90f263d8"
                + "011c1f440e06deb0509a4bfe229294e89cad2038b85891dda39f976fbacb26bb1d8921c94ccdfea0"
                + "cafc0b40d6089c670dd9a762404c77");

    byte[] distributorPkExp = DatatypeConverter.parseHexBinary("010001");
    return new PkRmeEnc(PublicKeyType.RSA2048RESTR, distributorPkModulus, distributorPkExp);
  }

  private static PkRmeEnc getOwnerPubKey() {
    byte[] ownerPkModulus =
        DatatypeConverter.parseHexBinary(
            "00a90768d4c217e06f088cef0576b2ba4eecc2e993f24b6025d2aa189ae00bd954c3e614786bd4c4c64"
                + "7e1a0c31991a5b4a7954224c138cc2c0dcef26c9d1d832f09d2aa25148e203fb3e3287128b56b81"
                + "633618aaeb693a58929dd18c8feae57772d9089bf5a50f0b26b3437d2bfcf00889ded7a30c58526"
                + "8614162582cbc4402d8217220633d3882ce7bcaae66fa5277ed18c177a05b8bd362b086f2e6bce1"
                + "cdcc047be5edffee55a6d8296580bed68adecb804f54aa0559574a49c7eb4a763be51d11e028c17"
                + "a2412d7df344a714a7ed0b804e6f7c476b12acfd589f9346b6ff141ed41b3d62b05aee99094f0eb"
                + "bb4b74e08f361adc30dd930fc2f6eecbc011");

    byte[] ownerPkExp = DatatypeConverter.parseHexBinary("010001");
    return new PkRmeEnc(PublicKeyType.RSA2048RESTR, ownerPkModulus, ownerPkExp);
  }
}
