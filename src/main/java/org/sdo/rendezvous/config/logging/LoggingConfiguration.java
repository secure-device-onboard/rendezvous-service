// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.config.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.net.InetSocketAddress;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.encoder.LogstashEncoder;
import net.logstash.logback.stacktrace.ShortenedThrowableConverter;
import org.sdo.rendezvous.enums.AttributeName;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/** Class configures logger connection to Logstash. */
@Configuration
@Slf4j
@ConditionalOnProperty(value = "logging.logstash.enabled", havingValue = "true")
public class LoggingConfiguration {

  private static final String LOGSTASH_APPENDER_NAME = "LOGSTASH";

  private LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

  private InetSocketAddress inetSocketAddress;
  private LogstashTcpSocketAppender logstashAppender;
  private LogstashEncoder logstashEncoder;
  private ShortenedThrowableConverter throwableConverter;

  /**
   * Constructor for logging configuration class.
   *
   * @param inetSocketAddress the IP Socket Address implementation
   * @param logstashAppender the Abstract Logstash Tcp Socket Appender implementation
   * @param logstashEncoder the logging event
   * @param throwableConverter the shortened throwable converter
   */
  @Autowired
  public LoggingConfiguration(
      InetSocketAddress inetSocketAddress,
      LogstashTcpSocketAppender logstashAppender,
      LogstashEncoder logstashEncoder,
      ShortenedThrowableConverter throwableConverter) {
    this.inetSocketAddress = inetSocketAddress;
    this.logstashAppender = logstashAppender;
    this.logstashEncoder = logstashEncoder;
    this.throwableConverter = throwableConverter;

    addLogstashAppender(context);
    addContextListener(context);
  }

  private void addContextListener(LoggerContext context) {
    LogbackLoggerContextListener loggerContextListener = new LogbackLoggerContextListener();
    loggerContextListener.setContext(context);
    context.addListener(loggerContextListener);
  }

  private void addLogstashAppender(LoggerContext context) {
    log.info("Initializing Logstash logging");

    logstashAppender.setName(LOGSTASH_APPENDER_NAME);

    logstashAppender.addDestinations(inetSocketAddress);
    configureLogstashEncoder();

    logstashAppender.setEncoder(logstashEncoder);
    logstashAppender.start();

    context.getLogger("ROOT").addAppender(logstashAppender);
  }

  private void configureLogstashEncoder() {
    throwableConverter.setRootCauseFirst(true);
    logstashEncoder.setThrowableConverter(throwableConverter);
    addMdcFields(logstashEncoder);
  }

  private void addMdcFields(LogstashEncoder logstashEncoder) {
    logstashEncoder.addIncludeMdcKeyName(AttributeName.GUID.name());
    logstashEncoder.addIncludeMdcKeyName(AttributeName.SESSION_ID.name());
  }

  class LogbackLoggerContextListener extends ContextAwareBase implements LoggerContextListener {

    @Override
    public boolean isResetResistant() {
      return true;
    }

    @Override
    public void onStart(LoggerContext context) {
      addLogstashAppender(context);
    }

    @Override
    public void onReset(LoggerContext context) {
      addLogstashAppender(context);
    }

    @Override
    public void onStop(LoggerContext context) {
      // Nothing to do.
    }

    @Override
    public void onLevelChange(ch.qos.logback.classic.Logger logger, Level level) {
      // Nothing to do.
    }
  }
}
