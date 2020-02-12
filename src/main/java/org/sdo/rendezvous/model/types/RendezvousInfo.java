// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.model.types;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.sdo.rendezvous.model.types.serialization.RendezvousInfoDeserializer;
import org.sdo.rendezvous.model.types.serialization.RendezvousInfoSerializer;

@JsonSerialize(using = RendezvousInfoSerializer.class)
@JsonDeserialize(using = RendezvousInfoDeserializer.class)
@Getter
public class RendezvousInfo {

  private List<RendezvousInstr> rendezvousInstructions = new ArrayList<>();

  public int getLength() {
    return rendezvousInstructions.size();
  }

  public void add(RendezvousInstr rendezvousInstr) {
    rendezvousInstructions.add(rendezvousInstr);
  }

  @Override
  public String toString() {
    return rendezvousInstructions.toString();
  }
}
