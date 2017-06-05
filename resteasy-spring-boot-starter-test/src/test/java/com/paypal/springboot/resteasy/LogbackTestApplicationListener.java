package com.paypal.springboot.resteasy;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.Ordered;
import org.testng.Assert;

/**
 * The Spring application listener registers a Logback appender
 * which allows inspecting every log statement looking for warning
 * or error messages. If any is found, the test will fail.
 *
 * @author facarvalho
 */
public class LogbackTestApplicationListener implements SmartApplicationListener {

    private boolean warningOrErrorFound = false;

    private Appender<ILoggingEvent> appender = new AppenderBase<ILoggingEvent>() {

        // TODO
        // Remove this after implementing https://github.com/paypal/resteasy-spring-boot/issues/69
        private static final java.lang.String SCANNING_WARNING = "\n-------------\nStarting on version 3.0.0, the behavior of the `scanning`";

        @Override
        protected void append(ILoggingEvent event) {
            if (event == null || warningOrErrorFound) {
                return;
            }
            Level level = event.getLevel();
            if ((level.equals(Level.WARN) || level.equals(Level.ERROR)) && !event.getMessage().startsWith(SCANNING_WARNING)) {
                warningOrErrorFound = true;
                Assert.fail(event.getFormattedMessage());
            }
        }
    };

    private void addTestAppender() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        appender.setContext(loggerContext);
        appender.start();
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(appender);
    }

    private void detachTestAppender() {
        if (appender != null) {
            appender.stop();
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.detachAppender(appender);
        }
    }

    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return ApplicationEnvironmentPreparedEvent.class.isAssignableFrom(eventType);
    }

    public boolean supportsSourceType(Class<?> sourceType) {
        return true;
    }

    public void onApplicationEvent(ApplicationEvent event) {
        if(event instanceof ApplicationEnvironmentPreparedEvent) {
            addTestAppender();
        } else if(event instanceof ContextClosedEvent && ((ContextClosedEvent)event).getApplicationContext().getParent() == null) {
            detachTestAppender();
        }
    }

    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE - 12;
    }

}
