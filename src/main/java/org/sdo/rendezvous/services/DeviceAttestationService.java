// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services;

import org.sdo.rendezvous.enums.DeviceAttestationMethod;
import org.sdo.rendezvous.exceptions.InvalidAttestationMethodException;
import org.sdo.rendezvous.model.types.OwnershipVoucher;
import org.springframework.stereotype.Service;

@Service
class DeviceAttestationService {

  DeviceAttestationMethod getDeviceAttestationMethod(OwnershipVoucher ownershipVoucher)
      throws InvalidAttestationMethodException {
    if (ownershipVoucher.getCertificateChain() != null
        && ownershipVoucher.getOwnershipVoucherHeader().getDeviceCertChainHash() != null) {
      return DeviceAttestationMethod.ECDSA;
    }
    if (ownershipVoucher.getCertificateChain() == null
        && ownershipVoucher.getOwnershipVoucherHeader().getDeviceCertChainHash() == null) {
      return DeviceAttestationMethod.EPID;
    } else {
      throw new InvalidAttestationMethodException(
          "Cannot determine attestation method: to0d.op.dc or to0d.op.oh.hdc is missing.");
    }
  }
}
