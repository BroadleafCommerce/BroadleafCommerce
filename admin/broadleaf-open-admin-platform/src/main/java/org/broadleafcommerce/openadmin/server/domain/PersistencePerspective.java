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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.openadmin.client.dto.PersistencePerspectiveItemType;

public interface PersistencePerspective extends Serializable {

    /**
     * @return the id
     */
    public abstract Long getId();

    /**
     * @param id the id to set
     */
    public abstract void setId(Long id);

    /**
     * @return the additionalNonPersistentProperties
     */
    public abstract String getAdditionalNonPersistentProperties();

    /**
     * @param additionalNonPersistentProperties the additionalNonPersistentProperties to set
     */
    public abstract void setAdditionalNonPersistentProperties(
            String additionalNonPersistentProperties);

    public List<ForeignKey> getAdditionalForeignKeys();

    public void setAdditionalForeignKeys(List<ForeignKey> additionalForeignKeys);

    /**
     * @return the persistencePerspectiveItems
     */
    public abstract Map<PersistencePerspectiveItemType, PersistencePerspectiveItem> getPersistencePerspectiveItems();

    /**
     * @param persistencePerspectiveItems the persistencePerspectiveItems to set
     */
    public abstract void setPersistencePerspectiveItems(
            Map<PersistencePerspectiveItemType, PersistencePerspectiveItem> persistencePerspectiveItems);

    /**
     * @return the operationTypes
     */
    public abstract OperationTypes getOperationTypes();

    /**
     * @param operationTypes the operationTypes to set
     */
    public abstract void setOperationTypes(OperationTypes operationTypes);

    /**
     * @return the populateToOneFields
     */
    public abstract Boolean getPopulateToOneFields();

    /**
     * @param populateToOneFields the populateToOneFields to set
     */
    public abstract void setPopulateToOneFields(Boolean populateToOneFields);

    /**
     * @return the excludeFields
     */
    public abstract String getExcludeFields();

    /**
     * @param excludeFields the excludeFields to set
     */
    public abstract void setExcludeFields(String excludeFields);

    /**
     * @return the includeFields
     */
    public abstract String getIncludeFields();

    /**
     * @param includeFields the includeFields to set
     */
    public abstract void setIncludeFields(String includeFields);
    
}