// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.utils.validators;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdo.rendezvous.config.RendezvousConfig;
import org.sdo.rendezvous.exceptions.InvalidWaitSecondsException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaitSecondsValidator {

  private final RendezvousConfig rendezvousConfig;

  /**
   * Validates if the waitSeconds is positive value.
   *
   * @param waitSeconds the wait seconds
   * @throws InvalidWaitSecondsException if the waitSeconds is a negative value
   */
  public void validateCorrectness(int waitSeconds) throws InvalidWaitSecondsException {
    if (waitSeconds < 0) {
      throw new InvalidWaitSecondsException("Wait seconds value is invalid.");
    }
  }

  public boolean isRemovalRequired(int waitSeconds) {
    return waitSeconds == 0;
  }

  /**
   * Verifies that the specified waitSeconds are above configuration waitSeconds limit.
   *
   * @param waitSeconds the wait seconds
   * @return true if specified waitSeconds is above the limit of waitSeconds configuration
   */
  public boolean isAboveLimit(int waitSeconds) {
    return waitSeconds > rendezvousConfig.getWaitSecondsLimit();
  }
}
