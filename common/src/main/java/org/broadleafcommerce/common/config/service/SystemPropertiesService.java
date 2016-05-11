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
package org.broadleafcommerce.common.config.service;

import org.broadleafcommerce.common.config.domain.SystemProperty;
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

    String resolveSystemProperty(String name, String defaultValue);
    
    /**
     * Resolves an int system property.  Returns 0 when no matching property
     * is found.
     * 
     * @param name
     * @return
     */
    int resolveIntSystemProperty(String name);
    
    int resolveIntSystemProperty(String name, int defaultValue);

    /**
     * Resolves a boolean system property.   Returns false when no matching
     * system property is found. 
     * 
     * @param name
     * @return
     */
    boolean resolveBooleanSystemProperty(String name);
    
    /**
     * 
     */
    boolean resolveBooleanSystemProperty(String name, boolean defaultValue);

    /**
     * Resolves an long system property. Returns 0 when no matching property
     * is found.
     * @param name
     * @return
     */
    long resolveLongSystemProperty(String name);
    
    long resolveLongSystemProperty(String name, long defaultValue);

    /**
     * Determines if the given value is valid for the specified type
     * 
     * @param sp
     * @return whether or not the SystemProperty is in a valid state
     */
    public boolean isValueValidForType(String value, SystemPropertyFieldType type);

    /**
     * Evicts the given SystemProperty from the cache
     * 
     * @param systemProperty
     */
    public void removeFromCache(SystemProperty systemProperty);

    /**
     * Finds a SystemProperty by its internal id
     * 
     * @param id
     * @return the {@link SystemProperty}
     */
    public SystemProperty findById(Long id);


}
