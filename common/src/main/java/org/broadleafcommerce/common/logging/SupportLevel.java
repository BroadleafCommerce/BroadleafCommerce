package org.broadleafcommerce.common.logging;

import org.apache.log4j.Level;

/**
 * Extend Log4J standard level implementation to add support
 * for the SUPPORT log level. This level is used in support logging
 * in modules.
 *
 * @author Jeff Fischer
 */
public class SupportLevel extends Level {

    public SupportLevel(int level, String levelStr, int syslogEquivalent) {
        super(level, levelStr, syslogEquivalent);
    }

    public static final int SUPPORT_INT = ERROR_INT + 10;

    public static final Level SUPPORT = new SupportLevel(SUPPORT_INT, "SUPPORT", 6);

    public static Level toLevel(String sArg) {
        if (sArg != null && sArg.toUpperCase().equals("SUPPORT")) {
            return SUPPORT;
        }
        return toLevel(sArg, Level.DEBUG);
    }

    public static Level toLevel(int val) {
        if (val == SUPPORT_INT) {
            return SUPPORT;
        }
        return toLevel(val, Level.DEBUG);
    }

    public static Level toLevel(int val, Level defaultLevel) {
        if (val == SUPPORT_INT) {
            return SUPPORT;
        }
        return Level.toLevel(val, defaultLevel);
    }

    public static Level toLevel(String sArg, Level defaultLevel) {
        if (sArg != null && sArg.toUpperCase().equals("SUPPORT")) {
            return SUPPORT;
        }
        return Level.toLevel(sArg, defaultLevel);
    }
}
