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


/**
 * <p>LogManager class that adds support for retrieving a specialized
 * Logger instance (SupportLogger). SupportLogger provides support for the
 * SUPPORT log level.</p>
 *
 * @author Jeff Fischer
 * @author elbertbautista
 */
public class SupportLogManager {

    /**
     * Retrieve a SupportLogger instance
     *
     * @param moduleName The name of the module - will appear in the log message
     * @param name The name for the logger - will appear in the log message
     * @return the specialized Logger instance supporting the SUPPORT log level
     */
    public static SupportLogger getLogger(final String moduleName, String name) {
        return new SupportLogger(moduleName, name);
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
