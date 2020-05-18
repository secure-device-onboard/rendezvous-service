// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.requests.to1;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sdo.rendezvous.model.types.ProveToSdoBody;
import org.sdo.rendezvous.model.types.PubKey;
import org.sdo.rendezvous.model.types.Signature;
import org.sdo.rendezvous.model.types.serialization.ProveToSdoRequestDeserializer;

@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = ProveToSdoRequestDeserializer.class)
@Data
public class ProveToSdoRequest {

  public static final String PROVE_TO_MP_BODY_TAG = "bo";
  public static final String PUBLIC_KEY_TAG = "pk";
  public static final String SIGNATURE_TAG = "sg";

  private ProveToSdoBody proveToSdoBody;
  private PubKey publicKey;
  private Signature signature;

  @Override
  public String toString() {
    return "["
        + "bo(prove to sdo body)="
        + proveToSdoBody
        + ", pk(public key)="
        + publicKey
        + ", sg(signature)="
        + signature
        + "]";
  }
}
