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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>Default Implementation of SupportLoggerAdapter that sends all support log messages to the console ({@code System.out}).
 * The following System Properties are supported to configure the behavior of this logger:</p>
 * </p>
 *
 * <ul>
 * <li><code>SystemSupportLoggerAdapter.showDateTime</code> - Set to <code>true</code> if you want the current date and
 * time to be included in output messages. Default is <code>true</code></li>
 *
 * <li><code>SystemSupportLoggerAdapter.dateTimeFormat</code> - The date and time format to be used in the output messages.
 * The pattern describing the date and time format is defined by
 * <a href="http://docs.oracle.com/javase/1.5.0/docs/api/java/text/SimpleDateFormat.html"><code>SimpleDateFormat</code></a>.
 * If the format is not specified or is invalid, the number of milliseconds since start up will be output. </li>
 *
 * <li><code>SystemSupportLoggerAdapter.showThreadName</code> - Set to <code>true</code> if you want to output the current
 * thread name. Defaults to <code>false</code>.</li>
 *
 * <li><code>SystemSupportLoggerAdapter.showLogName</code> - Set to <code>true</code> if you want the Logger instance name
 * to be included in output messages. Defaults to <code>true</code>.</li>
 *
 * <li><code>SystemSupportLoggerAdapter.levelInBrackets</code> - Should the level string be output in brackets? Defaults
 * to <code>true</code>.</li>
 *
 * </ul>
 *
 * <p>This implementation is heavily inspired by
 * <a href="http://www.slf4j.org/">SLF4J</a>'s SimpleLogger.</p>
 *
 * @author Elbert Bautista (elbertbautista)
 */
public class SystemSupportLoggerAdapter extends AbstractSupportLoggerAdapter implements SupportLoggerAdapter {

    public static final String SHOW_DATE_TIME_KEY = "SystemSupportLoggerAdapter.showDateTime";
    public static final String DATE_TIME_FORMAT_KEY = "SystemSupportLoggerAdapter.dateTimeFormat";
    public static final String SHOW_THREAD_NAME_KEY = "SystemSupportLoggerAdapter.showThreadName";
    public static final String SHOW_LOG_NAME_KEY = "SystemSupportLoggerAdapter.showLogName";
    public static final String LEVEL_IN_BRACKETS_KEY = "SystemSupportLoggerAdapter.levelInBrackets";

    private String name;

    /**
     * Generate a SUPPORT level log message
     *
     * @param message the log message
     */
    @Override
    public void support(String message) {
        log(LOG_LEVEL_SUPPORT, message, null);
    }

    /**
     * Generate a SUPPORT level log message with an accompanying Throwable
     *
     * @param message the log message
     * @param t the exception to accompany the log message - will result in a stack track in the log
     */
    @Override
    public void support(String message, Throwable t) {
        log(LOG_LEVEL_SUPPORT, message, t);
    }

    /**
     * Generate a specialized SUPPORT level log message that includes a LifeCycleEvent
     * in the message.
     *
     * @param lifeCycleEvent The module life cycle type for this log message
     * @param message the log message
     */
    @Override
    public void lifecycle(LifeCycleEvent lifeCycleEvent, String message) {
        log(LOG_LEVEL_SUPPORT, message, null);
    }

    @Override
    public void debug(String message) {
        log(LOG_LEVEL_DEBUG, message, null);
    }

    @Override
    public void debug(String message, Throwable t) {
        log(LOG_LEVEL_DEBUG, message, t);
    }

    @Override
    public void error(String message) {
        log(LOG_LEVEL_ERROR, message, null);
    }

    @Override
    public void error(String message, Throwable t) {
        log(LOG_LEVEL_ERROR, message, t);
    }

    @Override
    public void fatal(String message) {
        log(LOG_LEVEL_FATAL, message, null);
    }

    @Override
    public void fatal(String message, Throwable t) {
        log(LOG_LEVEL_FATAL, message, t);
    }

    @Override
    public void info(String message) {
        log(LOG_LEVEL_INFO, message, null);
    }

    @Override
    public void info(String message, Throwable t) {
        log(LOG_LEVEL_INFO, message, t);
    }

    @Override
    public void warn(String message) {
        log(LOG_LEVEL_WARN, message, null);
    }

    @Override
    public void warn(String message, Throwable t) {
        log(LOG_LEVEL_WARN, message, t);
    }

    protected void log(int level, String message, Throwable t) {

        StringBuffer buf = new StringBuffer(32);

        if (getLevelInBrackets()) {
            buf.append('[');
        }

        // Append a readable representation of the log level
        switch (level) {
            case LOG_LEVEL_TRACE:
                buf.append(TRACE);
                break;
            case LOG_LEVEL_DEBUG:
                buf.append(DEBUG);
                break;
            case LOG_LEVEL_INFO:
                buf.append(INFO);
                break;
            case LOG_LEVEL_WARN:
                buf.append(WARN);
                break;
            case LOG_LEVEL_ERROR:
                buf.append(ERROR);
                break;
            case LOG_LEVEL_FATAL:
                buf.append(FATAL);
                break;
            default:
                buf.append(SUPPORT);
        }

        if (getLevelInBrackets()) {
            buf.append(']');
            buf.append(' ');
        }

        // Append date-time if so configured
        if (getShowDateTime() && getDateFormatter() != null) {
                buf.append(getFormattedDate());
                buf.append(' ');
        }

        // Append current thread name if so configured
        if (getShowThreadName()) {
            buf.append('[');
            buf.append(Thread.currentThread().getName());
            buf.append("] ");
        }

        // Append the name of the log instance if so configured
        if (getShowLogName()) {
            buf.append(String.valueOf(name)).append(" - ");
        }

        // Append the message
        buf.append(message);

        write(buf, t);

    }

    protected void write(StringBuffer buf, Throwable t) {
        System.out.println(buf.toString());
        if (t != null) {
            t.printStackTrace(System.out);
        }
        System.out.flush();
    }

    protected String getFormattedDate() {
        Date now = new Date();
        String dateText;
        synchronized (getDateFormatter()) {
            dateText = getDateFormatter().format(now);
        }
        return dateText;
    }

    protected boolean getShowDateTime() {
        String property = System.getProperty(SHOW_DATE_TIME_KEY, "true");
        return Boolean.valueOf(property);
    }

    protected DateFormat getDateFormatter() {
        String property = System.getProperty(DATE_TIME_FORMAT_KEY, "HH:mm:ss");
        return new SimpleDateFormat(property);
    }

    protected boolean getShowThreadName() {
        String property = System.getProperty(SHOW_THREAD_NAME_KEY, "false");
        return Boolean.valueOf(property);
    }

    protected boolean getShowLogName() {
        String property = System.getProperty(SHOW_LOG_NAME_KEY, "true");
        return Boolean.valueOf(property);
    }

    protected boolean getLevelInBrackets() {
        String property = System.getProperty(LEVEL_IN_BRACKETS_KEY, "true");
        return Boolean.valueOf(property);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
