package org.broadleafcommerce.common.logging;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;


/**
 * @author Jeff Fischer
 */
public class SupportLogManager extends LogManager {

    public static SupportLogger getLogger(final String moduleName, String name) {
        return (SupportLogger) getLoggerRepository().getLogger(name, new LoggerFactory() {
            @Override
            public Logger makeNewLoggerInstance(String s) {
                return new SupportLogger(moduleName, s);
            }
        });
    }

    public static SupportLogger getLogger(final String moduleName, Class<?> clazz) {
        return (SupportLogger) getLoggerRepository().getLogger(clazz.getName(), new LoggerFactory() {
            @Override
            public Logger makeNewLoggerInstance(String s) {
                return new SupportLogger(moduleName, s);
            }
        });
    }

}
