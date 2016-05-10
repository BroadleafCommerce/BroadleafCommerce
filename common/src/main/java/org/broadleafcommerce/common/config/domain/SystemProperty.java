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
package org.broadleafcommerce.common.config.domain;

import org.broadleafcommerce.common.config.service.type.SystemPropertyFieldType;
import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;

/**
 * This interface represents a System Property (name/value pair) stored in the database.  
 * <p/>
 * User: Kelly Tisdell
 * Date: 6/20/12
 */
public interface SystemProperty extends Serializable, MultiTenantCloneable<SystemProperty> {

    /**
     * Unique id of the DB record
     * @return
     */
    public Long getId();

    /**
     * Sets the id of the DB record
     * @param id
     */
    public void setId(Long id);

    /**
     * The name of the property as it exists in property files (for example googleAnalytics.webPropertyId)
     * @return
     */
    public String getName();

    /**
     * Sets the property name.  
     * @param name
     */
    public void setName(String name);

    /**
     * Declares whether or not the property name has been overridden, rather than using the default generated value.
     * @return
     */
    public Boolean getOverrideGeneratedPropertyName();

    /**
     * Sets the overrideGeneratedPropertyName.
     * @param overrideGeneratedPropertyName
     */
    public void setOverrideGeneratedPropertyName(Boolean overrideGeneratedPropertyName);

    /**
     * Returns the property value.  
     * @param name
     */
    public String getValue();

    /**
     * Sets the property value.  
     * @param name
     */
    public void setValue(String value);

    /**
     * Returns the property field type.   If not set, returns STRING
     * @return
     */
    public SystemPropertyFieldType getPropertyType();

    /**
     * Sets the property field type.
     * @param type
     */
    public void setPropertyType(SystemPropertyFieldType type);

    /**
     * @return the friendly name of this property
     */
    public String getFriendlyName();

    /**
     * Sets the friendly name of this property
     * 
     * @param friendlyName
     */
    public void setFriendlyName(String friendlyName);

    /**
     * @return the griendly group name of this property
     */
    public String getFriendlyGroup();

    /**
     * Sets the friendly group name of this property
     * 
     * @param friendlyGroup
     */
    public void setFriendlyGroup(String friendlyGroup);

    /**
     * @return the friendly tab of this property
     */
    public String getFriendlyTab();

    /**
     * Sets the friendly tab of this property
     * 
     * @param friendlyTab
     */
    public void setFriendlyTab(String friendlyTab);

}
