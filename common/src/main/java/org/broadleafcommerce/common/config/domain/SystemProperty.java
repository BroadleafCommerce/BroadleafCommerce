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
package org.broadleafcommerce.common.config.domain;

import org.broadleafcommerce.common.config.service.type.SystemPropertyFieldType;

import java.io.Serializable;

/**
 * This interface represents a System Property (name/value pair) stored in the database.  
 * <p/>
 * User: Kelly Tisdell
 * Date: 6/20/12
 */
public interface SystemProperty extends Serializable {

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
