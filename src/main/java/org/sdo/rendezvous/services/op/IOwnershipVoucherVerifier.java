// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services.op;

import org.sdo.rendezvous.exceptions.InvalidOwnershipVoucherException;
import org.sdo.rendezvous.model.types.OwnershipVoucher;
import org.springframework.stereotype.Component;

@Component
public interface IOwnershipVoucherVerifier {
  void verify(OwnershipVoucher ownershipVoucher) throws InvalidOwnershipVoucherException;
}
