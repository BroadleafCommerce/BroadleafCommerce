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
package org.broadleafcommerce.common.config.service;

import org.broadleafcommerce.common.config.service.type.SystemPropertyFieldType;


/**
 * To change this template use File | Settings | File Templates.
 * <p/>
 * User: Kelly Tisdell
 * Date: 6/25/12
 */
public interface SystemPropertiesService {

    /**
     * Preferred method for looking up properties.   The method will return the configured value or 
     * if no override value is found, it will return the value passed in to the method as the default value.
     * 
     * @param name
     * @return
     */
    String resolveSystemProperty(String name);

    /**
     * Resolves an int system property.  Returns 0 when no matching property
     * is found.
     * 
     * @param name
     * @return
     */
    int resolveIntSystemProperty(String name);

    /**
     * Resolves a boolean system property.   Returns false when no matching
     * system property is found. 
     * 
     * @param name
     * @return
     */
    boolean resolveBooleanSystemProperty(String name);

    /**
     * Resolves an long system property. Returns 0 when no matching property
     * is found.
     * @param name
     * @return
     */
    long resolveLongSystemProperty(String name);

    /**
     * Determines if the given value is valid for the specified type
     * 
     * @param sp
     * @return whether or not the SystemProperty is in a valid state
     */
    public boolean isValueValidForType(String value, SystemPropertyFieldType type);


}
