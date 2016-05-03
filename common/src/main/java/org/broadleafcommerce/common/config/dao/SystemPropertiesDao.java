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
package org.broadleafcommerce.common.config.dao;

import org.broadleafcommerce.common.config.domain.SystemProperty;

import java.util.List;

/**
 * This DAO enables access to manage system properties that can be stored in the database.
 * <p/>
 * User: Kelly Tisdell
 * Date: 6/25/12
 */
public interface SystemPropertiesDao {

    public SystemProperty saveSystemProperty(SystemProperty systemProperty);

    public void deleteSystemProperty(SystemProperty systemProperty);

    public List<SystemProperty> readAllSystemProperties();

    public SystemProperty readSystemPropertyByName(String name);

    public SystemProperty createNewSystemProperty();

    /**
     * Reads a SystemProperty by its internal database id
     * 
     * @param id
     * @return the {@link SystemProperty}
     */
    public SystemProperty readById(Long id);

    /**
     * Removes the SystemProperty from the null-capable cache.
     *
     * @param systemProperty the property instance
     */
    public void removeFromCache(SystemProperty systemProperty);
}
