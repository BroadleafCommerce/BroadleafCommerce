package org.broadleafcommerce.common.logging;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;


/**
 * Log4J LogManager extension that adds support for retrieving a specialized
 * Logger instance (SupportLogger). SupportLogger provides support for the
 * SUPPORT log level.
 *
 * @author Jeff Fischer
 */
public class SupportLogManager extends LogManager {

    /**
     * Retrieve a SupportLogger instance
     *
     * @param moduleName The name of the module - will appear in the log message
     * @param name The name for the logger - will appear in the log message
     * @return the specialized Logger instance supporting the SUPPORT log level
     */
    public static SupportLogger getLogger(final String moduleName, String name) {
        return (SupportLogger) getLoggerRepository().getLogger(name + "(" + moduleName + ")", new LoggerFactory() {
            @Override
            public Logger makeNewLoggerInstance(String s) {
                return new SupportLogger(moduleName, s);
            }
        });
    }

    /**
     * Retrieve a SupportLogger instance
     *
     * @param moduleName The name of the module - will appear in the log message
     * @param clazz The class from which the logging is being called - will appear in the log message
     * @return the specialized Logger instance supporting the SUPPORT log level
     */
    public static SupportLogger getLogger(final String moduleName, Class<?> clazz) {
        return getLogger(moduleName, clazz.getSimpleName());
    }

}
