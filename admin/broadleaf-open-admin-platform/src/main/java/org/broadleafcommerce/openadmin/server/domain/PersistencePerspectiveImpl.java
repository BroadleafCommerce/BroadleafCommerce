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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.openadmin.client.dto.PersistencePerspectiveItemType;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.MapKey;

/**
 * 
 * @author jfischer
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_SNDBX_PRSPCTV")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
public class PersistencePerspectiveImpl implements PersistencePerspective {
     
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(generator = "PersistencePerspectiveId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PersistencePerspectiveId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "PersistencePerspectiveImpl", allocationSize = 50)
    @Column(name = "PERSIST_PERSPECTIVE_ID")
    protected Long id;
    
    @Column(name = "ADDL_NON_PERSIST_PROPS")
    protected String additionalNonPersistentProperties;
    
    @OneToMany(mappedBy = "persistencePerspective", targetEntity = AdditionalForeignKeyImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})   
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
    @BatchSize(size = 50)
    protected List<ForeignKey> additionalForeignKeys = new ArrayList<ForeignKey>();
    
    @ManyToMany(targetEntity = PersistencePerspectiveItemImpl.class)
    @JoinTable(name = "BLC_SNDBX_PRSPCTV_ITEM_MAP", inverseJoinColumns = @JoinColumn(name = "PERSIST_PERSPECT_ITEM_ID", referencedColumnName = "PERSIST_PERSPECT_ITEM_ID"))
    @MapKey(columns = {@Column(name = "PRSPCTV_ITEM_TYPE_KEY", nullable = false)})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
    @BatchSize(size = 50)
    protected Map<PersistencePerspectiveItemType, PersistencePerspectiveItem> persistencePerspectiveItems = new HashMap<PersistencePerspectiveItemType, PersistencePerspectiveItem>();
    
    @ManyToOne(targetEntity = OperationTypesImpl.class, cascade = {CascadeType.ALL})
    @JoinColumn(name = "OPERATION_TYPES_ID")
    protected OperationTypes operationTypes;
    
    @Column(name = "POPULATE_TO_ONE_FIELDS")
    protected Boolean populateToOneFields = false;
    
    @Column(name = "EXCLUDE_FIELD_NAMES")
    protected String excludeFields;
    
    @Column(name = "INCLUDE_FIELD_NAMES")
    protected String includeFields;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.PersistencePerspective#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.PersistencePerspective#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.PersistencePerspective#getAdditionalNonPersistentProperties()
     */
    @Override
    public String getAdditionalNonPersistentProperties() {
        return additionalNonPersistentProperties;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.PersistencePerspective#setAdditionalNonPersistentProperties(java.lang.String)
     */
    @Override
    public void setAdditionalNonPersistentProperties(
            String additionalNonPersistentProperties) {
        this.additionalNonPersistentProperties = additionalNonPersistentProperties;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.PersistencePerspective#getAdditionalForeignKeys()
     */
    @Override
    public List<ForeignKey> getAdditionalForeignKeys() {
        return additionalForeignKeys;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.PersistencePerspective#setAdditionalForeignKeys(java.lang.String)
     */
    @Override
    public void setAdditionalForeignKeys(List<ForeignKey> additionalForeignKeys) {
        this.additionalForeignKeys = additionalForeignKeys;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.PersistencePerspective#getPersistencePerspectiveItems()
     */
    @Override
    public Map<PersistencePerspectiveItemType, PersistencePerspectiveItem> getPersistencePerspectiveItems() {
        return persistencePerspectiveItems;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.PersistencePerspective#setPersistencePerspectiveItems(java.util.Map)
     */
    @Override
    public void setPersistencePerspectiveItems(
            Map<PersistencePerspectiveItemType, PersistencePerspectiveItem> persistencePerspectiveItems) {
        this.persistencePerspectiveItems = persistencePerspectiveItems;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.PersistencePerspective#getOperationTypes()
     */
    @Override
    public OperationTypes getOperationTypes() {
        return operationTypes;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.PersistencePerspective#setOperationTypes(org.broadleafcommerce.openadmin.client.datasource.relations.operations.OperationTypes)
     */
    @Override
    public void setOperationTypes(OperationTypes operationTypes) {
        this.operationTypes = operationTypes;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.PersistencePerspective#getPopulateToOneFields()
     */
    @Override
    public Boolean getPopulateToOneFields() {
        return populateToOneFields;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.PersistencePerspective#setPopulateToOneFields(java.lang.Boolean)
     */
    @Override
    public void setPopulateToOneFields(Boolean populateToOneFields) {
        this.populateToOneFields = populateToOneFields;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.PersistencePerspective#getExcludeFields()
     */
    @Override
    public String getExcludeFields() {
        return excludeFields;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.PersistencePerspective#setExcludeFields(java.lang.String)
     */
    @Override
    public void setExcludeFields(String excludeFields) {
        this.excludeFields = excludeFields;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.PersistencePerspective#getIncludeFields()
     */
    @Override
    public String getIncludeFields() {
        return includeFields;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.PersistencePerspective#setIncludeFields(java.lang.String)
     */
    @Override
    public void setIncludeFields(String includeFields) {
        this.includeFields = includeFields;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((additionalForeignKeys == null) ? 0 : additionalForeignKeys
                        .hashCode());
        result = prime
                * result
                + ((additionalNonPersistentProperties == null) ? 0
                        : additionalNonPersistentProperties.hashCode());
        result = prime * result
                + ((excludeFields == null) ? 0 : excludeFields.hashCode());
        result = prime * result
                + ((includeFields == null) ? 0 : includeFields.hashCode());
        result = prime * result
                + ((operationTypes == null) ? 0 : operationTypes.hashCode());
        result = prime
                * result
                + ((persistencePerspectiveItems == null) ? 0
                        : persistencePerspectiveItems.hashCode());
        result = prime
                * result
                + ((populateToOneFields == null) ? 0 : populateToOneFields
                        .hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PersistencePerspectiveImpl other = (PersistencePerspectiveImpl) obj;
        
        if (id != null && other.id != null) {
            return id.equals(other.id);
        }
        
        if (additionalForeignKeys == null) {
            if (other.additionalForeignKeys != null)
                return false;
        } else if (!additionalForeignKeys.equals(other.additionalForeignKeys))
            return false;
        if (additionalNonPersistentProperties == null) {
            if (other.additionalNonPersistentProperties != null)
                return false;
        } else if (!additionalNonPersistentProperties
                .equals(other.additionalNonPersistentProperties))
            return false;
        if (excludeFields == null) {
            if (other.excludeFields != null)
                return false;
        } else if (!excludeFields.equals(other.excludeFields))
            return false;
        if (includeFields == null) {
            if (other.includeFields != null)
                return false;
        } else if (!includeFields.equals(other.includeFields))
            return false;
        if (operationTypes == null) {
            if (other.operationTypes != null)
                return false;
        } else if (!operationTypes.equals(other.operationTypes))
            return false;
        if (persistencePerspectiveItems == null) {
            if (other.persistencePerspectiveItems != null)
                return false;
        } else if (!persistencePerspectiveItems
                .equals(other.persistencePerspectiveItems))
            return false;
        if (populateToOneFields == null) {
            if (other.populateToOneFields != null)
                return false;
        } else if (!populateToOneFields.equals(other.populateToOneFields))
            return false;
        return true;
    }

    
}
