/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * Configuration should be made in your implementation's log4j.xml file (or other logging system config file,
 * if applicable). A sample log4j configuration would be the following, which sets up a daily rolling log.
 * <p/>
 * {@code
 * <?xml version="1.0" encoding="UTF-8" ?>
 * <!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
 * <log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/">
 * <appender name="console" class="org.apache.log4j.ConsoleAppender">
 * <param name="Target" value="System.out" />
 * <layout class="org.apache.log4j.PatternLayout">
 * <param name="ConversionPattern" value="[%5p] %d$&#123;HH:mm:ss$&#125; %c$&#123;1$&#125; - %m%n" />
 * </layout>
 * </appender>
 * <appender name="rollingDailyEnterpriseWorkflow" class="org.apache.log4j.DailyRollingFileAppender">
 * <param name="file" value="workflow.log" />
 * <param name="DatePattern" value="'.'yyyy-MM-dd" />
 * <layout class="org.apache.log4j.PatternLayout">
 * <param name="ConversionPattern" value="[%5p] %d$&#123;HH:mm:ss$&#125; %c$&#123;1$&#125; - %m%n" />
 * </layout>
 * </appender>
 * <logger name="com.broadleafcommerce.enterprise.workflow.process.detail" additivity="false">
 * <level value="debug"/>
 * <appender-ref ref="rollingDailyEnterpriseWorkflow"/>
 * </logger>
 * <root>
 * <priority value="warn" />
 * <appender-ref ref="console" />
 * </root>
 * </log4j:configuration>
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
                        "</log4j:configuration>\n" +
                        "");
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
