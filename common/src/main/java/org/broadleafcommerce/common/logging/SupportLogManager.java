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
