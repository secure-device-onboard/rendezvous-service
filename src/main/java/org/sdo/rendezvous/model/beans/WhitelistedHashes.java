// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.beans;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class WhitelistedHashes {
  private List<byte[]> hashes = new ArrayList<>();

  public void add(byte[] hash) {
    hashes.add(hash);
  }

  public List<byte[]> getAll() {
    return new ArrayList<>(hashes);
  }
}
