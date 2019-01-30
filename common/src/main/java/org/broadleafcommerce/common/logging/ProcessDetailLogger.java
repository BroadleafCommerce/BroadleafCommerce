/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.logging;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collection;

/**
 * Extra logging facility whose design intent is to handle detailed production logging for complex interactions.
 * Generally, the target of this logging is a rolling log file. This is intentionally separate from the standard system
 * logging, since this logging would likely be noisy in that context. Review of this log is useful to recreate complex
 * user scenarios, replicate error conditions and fix otherwise difficult to find bugs.
 * <p/>
 * Configuration should be made in your implementation's logback.xml file (or other logging system config file,
 * if applicable). A sample logback configuration would be the following, which sets up a daily rolling log.
 * <p/>
 * {@code
 * <?xml version="1.0" encoding="UTF-8"?>
 * <configuration>
 *  <include resource="org/springframework/boot/logging/logback/defaults.xml" />
 *  <property name="LOG_FILE" value="${LOG_FILE:-${LOG_PATH:-${LOG_TEMP:-${java.io.tmpdir:-/tmp}}/}spring.log}"/>
 *  <property name="WORKFLOW_LOG_FILE" value="${WORKFLOW_LOG_FILE:-${java.io.tmpdir:-/tmp}/blc-logs/workflow.log}"/>
 *  <include resource="org/springframework/boot/logging/logback/console-appender.xml" />
 *  <include resource="org/springframework/boot/logging/logback/file-appender.xml" />
 *  <root level="INFO">
 *      <appender-ref ref="CONSOLE" />
 *      <appender-ref ref="FILE" />
 *  </root>
 *  <appender name="rollingDailyEnterpriseWorkflow" class="ch.qos.logback.core.rolling.RollingFileAppender">
 *      <file>${WORKFLOW_LOG_FILE}</file>
 *      <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
 *          <fileNamePattern>${WORKFLOW_LOG_FILE}.%d{yyyy-MM-dd-HH-mm}.log</fileNamePattern>
 *          <maxHistory>30</maxHistory>
 *      </rollingPolicy>
 *      <encoder>
 *          <pattern>[%-5level] %d{MM-dd-yyyy HH:mm:ss} %logger{35} - %message%n</pattern>
 *      </encoder>
 *  </appender>
 *  <logger name="com.broadleafcommerce.enterprise.workflow.process.detail" level="DEBUG">
 *      <appender-ref ref="rollingDailyEnterpriseWorkflow"/>
 *  </logger>
 * </configuration>
 * }
 *
 * If you duplicated the sample configuration exactly, you would provide the logger name "com.broadleafcommerce.enterprise.workflow.process.detail"
 * to the {@link #ProcessDetailLogger(String)} constructor.
 *
 * @author Jeff Fischer
 */
public class ProcessDetailLogger {

    private static final SupportLogger LOGGER = SupportLogManager.getLogger("ProcessLogging", ProcessDetailLogger.class);

    private Log processDetailLog;

    /**
     * Max number of members that will output in the log for a collection or array member passed as a template variable
     */
    protected int listTemplateVariableMaxMemberCount = 30;

    /**
     * Max length of any String passed as a template variable
     */
    protected int stringTemplateVariableMaxLength = 200;

    @Value("${ignore.no.process.detail.logger.configuration:false}")
    protected boolean ignoreNoProcessDetailLoggerConfiguration = false;

    @Value("${disable.all.process.detail.logging:false}")
    protected boolean disableAllProcessDetailLogging = false;

    /**
     * Construct a logger
     *
     * @param logIdentifier the logger name that should be used from the backing logging system configuration
     */
    public ProcessDetailLogger(String logIdentifier) {
        if (!disableAllProcessDetailLogging) {
            processDetailLog = LogFactory.getLog(logIdentifier);
            if (!ignoreNoProcessDetailLoggerConfiguration && !processDetailLog.isDebugEnabled()) {
                LOGGER.support("The system has detected that a ProcessDetailLogger instance was requested without " +
                        "backing " +
                        "logger configuration at the debug level. In this case, process detail logs may not be sent " +
                        "to the " +
                        "appropriate logging file, or may appear in an unwanted location, " +
                        "like the standard system log. You" +
                        "can disable this log message by setting the ignore.no.process.detail.logger.configuration " +
                        "property to true. A" +
                        "sample configuration for log4j (log4j.xml) that creates a rolling daily log looks like:\n\n" +
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                        "<!DOCTYPE log4j:configuration SYSTEM \"log4j.dtd\">\n" +
                        "<log4j:configuration xmlns:log4j=\"http://jakarta.apache.org/log4j/\">\n" +
                        "<appender name=\"console\" class=\"org.apache.log4j.ConsoleAppender\">\n" +
                        "<param name=\"Target\" value=\"System.out\" />\n" +
                        "<layout class=\"org.apache.log4j.PatternLayout\">\n" +
                        "<param name=\"ConversionPattern\" value=\"[%5p] %d${HH:mm:ss$} %c${1$} - " +
                        "%m%n\" />\n" +
                        "</layout>\n" +
                        "</appender>\n" +
                        "<appender name=\"rollingDailyEnterpriseWorkflow\" class=\"org.apache.log4j" +
                        ".DailyRollingFileAppender\">\n" +
                        "<param name=\"file\" value=\"workflow.log\" />\n" +
                        "<param name=\"DatePattern\" value=\"'.'yyyy-MM-dd\" />\n" +
                        "<layout class=\"org.apache.log4j.PatternLayout\">\n" +
                        "<param name=\"ConversionPattern\" value=\"[%5p] %d${HH:mm:ss$} %c${1$} - " +
                        "%m%n\" />\n" +
                        "</layout>\n" +
                        "</appender>\n" +
                        "<logger name=\"com.broadleafcommerce.enterprise.workflow.process.detail\" " +
                        "additivity=\"false\">\n" +
                        "<level value=\"debug\"/>\n" +
                        "<appender-ref ref=\"rollingDailyEnterpriseWorkflow\"/>\n" +
                        "</logger>\n" +
                        "<root>\n" +
                        "<priority value=\"warn\" />\n" +
                        "<appender-ref ref=\"console\" />\n" +
                        "</root>\n" +
                        "</log4j:configuration>\n\n" +
                        "A sample logback configuration looks like:\n\n" +
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<configuration>\n" +
                        "<include resource=\"org/springframework/boot/logging/logback/defaults.xml\" />\n" +
                        "<property name=\"LOG_FILE\" value=\"${LOG_FILE:-${LOG_PATH:-${LOG_TEMP:-${java.io.tmpdir:-/tmp}}/}spring.log}\"/>\n" +
                        "<include resource=\"org/springframework/boot/logging/logback/console-appender.xml\" />\n" +
                        "<include resource=\"org/springframework/boot/logging/logback/file-appender.xml\" />\n" +
                        "<root level=\"INFO\">\n" +
                        "<appender-ref ref=\"CONSOLE\" />\n" +
                        "<appender-ref ref=\"FILE\" />\n" +
                        "</root>\n" +
                        "<appender name=\"rollingDailyEnterpriseWorkflow\" class=\"ch.qos.logback.core.rolling.RollingFileAppender\">\n" +
                        " <file>${WORKFLOW_LOG_FILE}</file>\n" +
                        "<rollingPolicy class=\"ch.qos.logback.core.rolling.TimeBasedRollingPolicy\">\n" +
                        "<fileNamePattern>${WORKFLOW_LOG_FILE}.%d{yyyy-MM-dd-HH-mm}.log</fileNamePattern>\n" +
                        "<maxHistory>30</maxHistory>\n" +
                        "</rollingPolicy>\n" +
                        "<encoder>\n" +
                        "<pattern>[%-5level] %d{MM-dd-yyyy HH:mm:ss} %logger{35} - %message%n</pattern>\n" +
                        "</encoder>\n" +
                        "</appender>\n" +
                        "<logger name=\"com.broadleafcommerce.enterprise.workflow.process.detail\" level=\"DEBUG\">\n" +
                        "<appender-ref ref=\"rollingDailyEnterpriseWorkflow\"/>\n" +
                        "</logger>\n" +
                        "</configuration>\n\n");
            }
        }
    }

    /**
     * Log a message to the configured log file
     *
     * @param logContext a fragment describing the context of this log message - will be prepended in the log. Can be null.
     * @param messageTemplate A template string using the same approach employed by {@link String#format(String, Object...)}
     * @param templateVariables the variable used to replace the %s values in the template string
     */
    public void logProcessDetail(String logContext, String messageTemplate, Object... templateVariables) {
        if (!disableAllProcessDetailLogging && processDetailLog.isDebugEnabled()) {
            String message = String.format(messageTemplate, processVariables(templateVariables));
            logProcessDetail(logContext, message);
        }
    }

    /**
     * Log a message to the configured log file
     *
     * @param logContext a fragment describing the context of this log message - will be prepended in the log. Can be null.
     * @param message a message to log
     */
    public void logProcessDetail(String logContext, String message) {
        logProcessDetail(logContext, null, message);
    }

    /**
     * Log a message to the configured log file
     *
     * @param logContext a fragment describing the context of this log message - will be prepended in the log. Can be null.
     * @param e an exception to include with the log message as a stack trace
     * @param messageTemplate A template string using the same approach employed by {@link String#format(String, Object...)}
     * @param templateVariables the variable used to replace the %s values in the template string
     */
    public void logProcessDetail(String logContext, Throwable e, String messageTemplate, Object... templateVariables) {
        if (!disableAllProcessDetailLogging && processDetailLog.isDebugEnabled()) {
            String message = String.format(messageTemplate, processVariables(templateVariables));
            logProcessDetail(logContext, e, message);
        }
    }

    /**
     * Log a message to the configured log file
     *
     * @param logContext a fragment describing the context of this log message - will be prepended in the log. Can be null.
     * @param e an exception to include with the log message as a stack trace
     * @param message a message to log
     */
    public void logProcessDetail(String logContext, Throwable e, String message) {
        if (!disableAllProcessDetailLogging && processDetailLog.isDebugEnabled()) {
            if (e == null) {
                processDetailLog.debug(logContext == null ? message : logContext + " " + message);
            } else {
                processDetailLog.debug(logContext == null ? message : logContext + " " + message, e);
            }
        }
    }

    /**
     * If an array or collection is passed in as part of the template variables, shorten the output if the length
     * exceeds a threshold. Also shorten long strings.
     *
     * @param variables the template variables to process for the log
     * @return the processed list
     */
    protected Object[] processVariables(Object[] variables) {
        for (int j=0;j<variables.length;j++) {
            Object[] temp = null;
            if (variables[j] != null) {
                if (variables[j].getClass().isArray()) {
                    temp = (Object[]) variables[j];
                } else if (variables[j] instanceof Collection) {
                    temp = ((Collection) variables[j]).toArray(new Object[((Collection) variables[j]).size()]);
                }
            }
            if (temp != null) {
                String joined;
                if (temp.length > listTemplateVariableMaxMemberCount) {
                    Object[] shorten = new Object[listTemplateVariableMaxMemberCount];
                    System.arraycopy(temp, 0, shorten, 0, listTemplateVariableMaxMemberCount);
                    joined = StringUtils.join(shorten, ",");
                    joined += "...";
                } else {
                    joined = StringUtils.join(temp, ",");
                }
                variables[j] = joined;
            }
            if (variables[j] instanceof String && ((String) variables[j]).length() > stringTemplateVariableMaxLength) {
                variables[j] = ((String) variables[j]).substring(0, stringTemplateVariableMaxLength-1) + "...";
            }
        }
        return variables;
    }

    public int getListTemplateVariableMaxMemberCount() {
        return listTemplateVariableMaxMemberCount;
    }

    public void setListTemplateVariableMaxMemberCount(int listTemplateVariableMaxMemberCount) {
        this.listTemplateVariableMaxMemberCount = listTemplateVariableMaxMemberCount;
    }

    public int getStringTemplateVariableMaxLength() {
        return stringTemplateVariableMaxLength;
    }

    public void setStringTemplateVariableMaxLength(int stringTemplateVariableMaxLength) {
        this.stringTemplateVariableMaxLength = stringTemplateVariableMaxLength;
    }
}
