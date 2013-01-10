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

package org.broadleafcommerce.openadmin.client.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * 
 * @author jfischer
 *
 */
public class PersistencePerspective implements IsSerializable, Serializable {
     
    private static final long serialVersionUID = 1L;
    
    protected String[] additionalNonPersistentProperties = new String[]{};
    protected ForeignKey[] additionalForeignKeys = new ForeignKey[]{};
    protected Map<PersistencePerspectiveItemType, PersistencePerspectiveItem> persistencePerspectiveItems = new HashMap<PersistencePerspectiveItemType, PersistencePerspectiveItem>();
    protected OperationTypes operationTypes = new OperationTypes();
    protected Boolean populateToOneFields = false;
    protected String[] excludeFields = new String[]{};
    protected String[] includeFields = new String[]{};
    protected String configurationKey;
    
    public PersistencePerspective() {
    }
    
    public PersistencePerspective(OperationTypes operationTypes, String[] additionalNonPersistentProperties, ForeignKey[] additionalNonPersistentForeignKeys) {
        setAdditionalNonPersistentProperties(additionalNonPersistentProperties);
        setAdditionalForeignKeys(additionalNonPersistentForeignKeys);
        this.operationTypes = operationTypes;
    }

    public String[] getAdditionalNonPersistentProperties() {
        return additionalNonPersistentProperties;
    }

    public void setAdditionalNonPersistentProperties(String[] additionalNonPersistentProperties) {
        this.additionalNonPersistentProperties = additionalNonPersistentProperties;
        Arrays.sort(this.additionalNonPersistentProperties);
    }

    public ForeignKey[] getAdditionalForeignKeys() {
        return additionalForeignKeys;
    }

    public void setAdditionalForeignKeys(ForeignKey[] additionalNonPersistentForeignKeys) {
        this.additionalForeignKeys = additionalNonPersistentForeignKeys;
        Arrays.sort(this.additionalForeignKeys, new Comparator<ForeignKey>() {
            public int compare(ForeignKey o1, ForeignKey o2) {
                return o1.getManyToField().compareTo(o2.getManyToField());
            }
        });
    }

    public OperationTypes getOperationTypes() {
        return operationTypes;
    }

    public void setOperationTypes(OperationTypes operationTypes) {
        this.operationTypes = operationTypes;
    }
    
    public void addPersistencePerspectiveItem(PersistencePerspectiveItemType type, PersistencePerspectiveItem item) {
        persistencePerspectiveItems.put(type, item);
    }

    public Map<PersistencePerspectiveItemType, PersistencePerspectiveItem> getPersistencePerspectiveItems() {
        return persistencePerspectiveItems;
    }

    public void setPersistencePerspectiveItems(Map<PersistencePerspectiveItemType, PersistencePerspectiveItem> persistencePerspectiveItems) {
        this.persistencePerspectiveItems = persistencePerspectiveItems;
    }

    /**
     * Retrieves whether or not ManyToOne and OneToOne field boundaries
     * will be traversed when retrieving and populating entity fields.
     * Implementation should use the @AdminPresentationClass annotation
     * instead.
     *
     * @return Whether or not ManyToOne and OneToOne field boundaries will be crossed.
     */
    @Deprecated
    public Boolean getPopulateToOneFields() {
        return populateToOneFields;
    }

    /**
     * Sets whether or not ManyToOne and OneToOne field boundaries
     * will be traversed when retrieving and populating entity fields.
     * Implementation should use the @AdminPresentationClass annotation
     * instead.
     *
     * @return Whether or not ManyToOne and OneToOne field boundaries will be crossed.
     */
    @Deprecated
    public void setPopulateToOneFields(Boolean populateToOneFields) {
        this.populateToOneFields = populateToOneFields;
    }

    /**
     * Retrieve the list of fields to exclude from the admin presentation.
     * Implementations should use the excluded property of the AdminPresentation
     * annotation instead, or use an AdminPresentationOverride if re-enabling a
     * Broadleaf field is desired. If multiple datasources point to the same
     * entity, but different exclusion behavior is required, a custom persistence
     * handler may be employed with different inspect method implementations to
     * account for the variations.
     *
     * @return list of fields to exclude from the admin
     */
    @Deprecated
    public String[] getExcludeFields() {
        return excludeFields;
    }

    /**
     * Set the list of fields to exclude from the admin presentation.
     * Implementations should use the excluded property of the AdminPresentation
     * annotation instead, or use an AdminPresentationOverride if re-enabling a
     * Broadleaf field is desired. If multiple datasources point to the same
     * entity, but different exclusion behavior is required, a custom persistence
     * handler may be employed with different inspect method implementations to
     * account for the variations.
     *
     * @param excludeManyToOneFields
     */
    @Deprecated
    public void setExcludeFields(String[] excludeManyToOneFields) {
        this.excludeFields = excludeManyToOneFields;
        Arrays.sort(this.excludeFields);
    }

    /**
     * Get the list of fields to include in the admin presentation.
     * Implementations should use excludeFields instead.
     *
     * @return list of fields to include in the admin
     */
    @Deprecated
    public String[] getIncludeFields() {
        return includeFields;
    }

    /**
     * Set the list of fields to include in the admin presentation.
     * Implementations should use excludeFields instead.
     *
     * @param includeManyToOneFields
     */
    @Deprecated
    public void setIncludeFields(String[] includeManyToOneFields) {
        this.includeFields = includeManyToOneFields;
        Arrays.sort(this.includeFields);
    }

    public String getConfigurationKey() {
        return configurationKey;
    }

    public void setConfigurationKey(String configurationKey) {
        this.configurationKey = configurationKey;
    }
}
