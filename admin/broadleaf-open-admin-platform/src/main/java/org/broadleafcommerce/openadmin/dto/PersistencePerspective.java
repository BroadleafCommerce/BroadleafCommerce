/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.dto;

import org.apache.commons.lang.ArrayUtils;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @author jfischer
 *
 */
public class PersistencePerspective implements Serializable {
     
    private static final long serialVersionUID = 1L;
    
    protected String[] additionalNonPersistentProperties = new String[]{};
    protected ForeignKey[] additionalForeignKeys = new ForeignKey[]{};
    protected Map<PersistencePerspectiveItemType, PersistencePerspectiveItem> persistencePerspectiveItems = new HashMap<PersistencePerspectiveItemType, PersistencePerspectiveItem>();
    protected OperationTypes operationTypes = new OperationTypes();
    protected Boolean populateToOneFields = false;
    protected String[] excludeFields = new String[]{};
    protected String[] includeFields = new String[]{};
    protected String configurationKey;
    protected Boolean showArchivedFields = false;
    protected Boolean useServerSideInspectionCache = true;
    
    public PersistencePerspective() {
    }
    
    public PersistencePerspective(OperationTypes operationTypes, String[] additionalNonPersistentProperties, ForeignKey[] additionalForeignKeys) {
        setAdditionalNonPersistentProperties(additionalNonPersistentProperties);
        setAdditionalForeignKeys(additionalForeignKeys);
        this.operationTypes = operationTypes;
    }

    public String[] getAdditionalNonPersistentProperties() {
        return additionalNonPersistentProperties;
    }

    public void setAdditionalNonPersistentProperties(String[] additionalNonPersistentProperties) {
        this.additionalNonPersistentProperties = additionalNonPersistentProperties;
        if (!ArrayUtils.isEmpty(this.additionalNonPersistentProperties)) {
            Arrays.sort(this.additionalNonPersistentProperties);
        }
    }

    public ForeignKey[] getAdditionalForeignKeys() {
        return additionalForeignKeys;
    }

    public void setAdditionalForeignKeys(ForeignKey[] additionalForeignKeys) {
        this.additionalForeignKeys = additionalForeignKeys;
        if (!ArrayUtils.isEmpty(this.additionalForeignKeys)) {
            Arrays.sort(this.additionalForeignKeys, new Comparator<ForeignKey>() {
                public int compare(ForeignKey o1, ForeignKey o2) {
                    return o1.getManyToField().compareTo(o2.getManyToField());
                }
            });
        }
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
        if (!ArrayUtils.isEmpty(this.excludeFields)) {
            Arrays.sort(this.excludeFields);
        }
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
        if (!ArrayUtils.isEmpty(this.includeFields)) {
            Arrays.sort(this.includeFields);
        }
    }

    public String getConfigurationKey() {
        return configurationKey;
    }

    public void setConfigurationKey(String configurationKey) {
        this.configurationKey = configurationKey;
    }

    public Boolean getShowArchivedFields() {
        return showArchivedFields;
    }

    public void setShowArchivedFields(Boolean showArchivedFields) {
        this.showArchivedFields = showArchivedFields;
    }

    public Boolean getUseServerSideInspectionCache() {
        return useServerSideInspectionCache;
    }

    public void setUseServerSideInspectionCache(Boolean useServerSideInspectionCache) {
        this.useServerSideInspectionCache = useServerSideInspectionCache;
    }

    public PersistencePerspective clonePersistencePerspective() {
        PersistencePerspective persistencePerspective = new PersistencePerspective();
        persistencePerspective.operationTypes = operationTypes.cloneOperationTypes();

        if (additionalNonPersistentProperties != null) {
            persistencePerspective.additionalNonPersistentProperties = new String[additionalNonPersistentProperties.length];
            System.arraycopy(additionalNonPersistentProperties, 0, persistencePerspective.additionalNonPersistentProperties, 0, additionalNonPersistentProperties.length);
        }

        if (additionalForeignKeys != null) {
            persistencePerspective.additionalForeignKeys = new ForeignKey[additionalForeignKeys.length];
            for (int j=0; j<additionalForeignKeys.length;j++){
                persistencePerspective.additionalForeignKeys[j] = additionalForeignKeys[j].cloneForeignKey();
            }
        }

        if (this.persistencePerspectiveItems != null) {
            Map<PersistencePerspectiveItemType, PersistencePerspectiveItem> persistencePerspectiveItems = new HashMap<PersistencePerspectiveItemType, PersistencePerspectiveItem>(this.persistencePerspectiveItems.size());
            for (Map.Entry<PersistencePerspectiveItemType, PersistencePerspectiveItem> entry : this.persistencePerspectiveItems.entrySet()) {
                persistencePerspectiveItems.put(entry.getKey(), entry.getValue().clonePersistencePerspectiveItem());
            }
            persistencePerspective.persistencePerspectiveItems = persistencePerspectiveItems;
        }

        persistencePerspective.populateToOneFields = populateToOneFields;
        persistencePerspective.configurationKey = configurationKey;
        persistencePerspective.showArchivedFields = showArchivedFields;
        persistencePerspective.useServerSideInspectionCache = useServerSideInspectionCache;

        if (excludeFields != null) {
            persistencePerspective.excludeFields = new String[excludeFields.length];
            System.arraycopy(excludeFields, 0, persistencePerspective.excludeFields, 0, excludeFields.length);
        }

        if (includeFields != null) {
            persistencePerspective.includeFields = new String[includeFields.length];
            System.arraycopy(includeFields, 0, persistencePerspective.includeFields, 0, includeFields.length);
        }

        return persistencePerspective;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PersistencePerspective{");
        sb.append("persistencePerspectiveItems=").append(persistencePerspectiveItems);
        sb.append(", configurationKey='").append(configurationKey).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!getClass().isAssignableFrom(o.getClass())) return false;

        PersistencePerspective that = (PersistencePerspective) o;

        if (!Arrays.equals(additionalForeignKeys, that.additionalForeignKeys)) return false;
        if (!Arrays.equals(additionalNonPersistentProperties, that.additionalNonPersistentProperties))
            return false;
        if (configurationKey != null ? !configurationKey.equals(that.configurationKey) : that.configurationKey != null)
            return false;
        if (!Arrays.equals(excludeFields, that.excludeFields)) return false;
        if (!Arrays.equals(includeFields, that.includeFields)) return false;
        if (operationTypes != null ? !operationTypes.equals(that.operationTypes) : that.operationTypes != null)
            return false;
        if (persistencePerspectiveItems != null ? !persistencePerspectiveItems.equals(that.persistencePerspectiveItems) : that.persistencePerspectiveItems != null)
            return false;
        if (populateToOneFields != null ? !populateToOneFields.equals(that.populateToOneFields) : that.populateToOneFields != null)
            return false;
        if (showArchivedFields != null ? !showArchivedFields.equals(that.showArchivedFields) : that.showArchivedFields != null)
            return false;
        if (useServerSideInspectionCache != null ? !useServerSideInspectionCache.equals(that.useServerSideInspectionCache) : that.useServerSideInspectionCache != null)
                    return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = additionalNonPersistentProperties != null ? Arrays.hashCode(additionalNonPersistentProperties) : 0;
        result = 31 * result + (additionalForeignKeys != null ? Arrays.hashCode(additionalForeignKeys) : 0);
        result = 31 * result + (persistencePerspectiveItems != null ? persistencePerspectiveItems.hashCode() : 0);
        result = 31 * result + (operationTypes != null ? operationTypes.hashCode() : 0);
        result = 31 * result + (populateToOneFields != null ? populateToOneFields.hashCode() : 0);
        result = 31 * result + (excludeFields != null ? Arrays.hashCode(excludeFields) : 0);
        result = 31 * result + (includeFields != null ? Arrays.hashCode(includeFields) : 0);
        result = 31 * result + (configurationKey != null ? configurationKey.hashCode() : 0);
        result = 31 * result + (showArchivedFields != null ? showArchivedFields.hashCode() : 0);
        result = 31 * result + (useServerSideInspectionCache != null ? useServerSideInspectionCache.hashCode() : 0);
        return result;
    }
}
