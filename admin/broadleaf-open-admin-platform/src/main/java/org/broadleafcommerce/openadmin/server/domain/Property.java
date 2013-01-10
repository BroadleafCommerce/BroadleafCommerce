/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.domain;

import org.broadleafcommerce.openadmin.client.presentation.SupportedFieldType;

import java.io.Serializable;

public interface Property extends Serializable {

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getValue();

    public abstract void setValue(String value);

    public abstract String getDisplayValue();

    public abstract void setDisplayValue(String displayValue);

    public org.broadleafcommerce.openadmin.server.domain.Entity getEntity();

    /**
     * @param entity the entity to set
     */
    public void setEntity(org.broadleafcommerce.openadmin.server.domain.Entity entity);

    /**
     * @return the id
     */
    public Long getId();

    /**
     * @param id the id to set
     */
    public void setId(Long id);
    
    public Boolean getIsDirty();

    public void setIsDirty(Boolean isDirty);

    public SupportedFieldType getSecondaryType();

    public void setSecondaryType(SupportedFieldType secondaryType);
}