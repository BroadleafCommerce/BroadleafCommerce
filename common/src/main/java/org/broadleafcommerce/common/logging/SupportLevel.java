/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
