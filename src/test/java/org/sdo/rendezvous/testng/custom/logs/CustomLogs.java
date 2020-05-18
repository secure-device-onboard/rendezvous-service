// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.testng.custom.logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class CustomLogs extends TestListenerAdapter {

  static final Logger logger = LoggerFactory.getLogger(CustomLogs.class);
  private String testMethodName = null;
  private long executionTime = 0;

  @Override
  public void onTestFailure(ITestResult tr) {

    testMethodName = tr.getMethod().getMethodName().toString();
    executionTime = tr.getEndMillis() - tr.getStartMillis();

    logger.info(
        " [Test Method="
            + testMethodName
            + "] [Execution Time="
            + executionTime
            + " ms] -- FAILED");
  }

  @Override
  public void onTestSkipped(ITestResult tr) {

    testMethodName = tr.getMethod().getMethodName().toString();
    executionTime = tr.getEndMillis() - tr.getStartMillis();

    logger.info(
        " [Test Method="
            + testMethodName
            + "] [Execution Time="
            + executionTime
            + " ms] -- SKIPPED");
  }

  @Override
  public void onTestSuccess(ITestResult tr) {

    testMethodName = tr.getMethod().getMethodName().toString();
    executionTime = tr.getEndMillis() - tr.getStartMillis();

    logger.info(
        " [Test Method="
            + testMethodName
            + "] [Execution Time="
            + executionTime
            + " ms] -- SUCCESS");
  }
}
