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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.openadmin.client.dto.OperationType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 
 * @author jfischer
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_SNDBX_OPERATION_TYPES")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
public class OperationTypesImpl implements OperationTypes {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(generator = "OperationTypesId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "OperationTypesId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "OperationTypesImpl", allocationSize = 50)
    @Column(name = "OPERATION_TYPES_ID")
    protected Long id;
    
    @Column(name = "FETCH_TYPE")
    protected OperationType fetchType = OperationType.ENTITY;
    
    @Column(name = "REMOVE_TYPE")
    protected OperationType removeType = OperationType.ENTITY;
    
    @Column(name = "ADD_TYPE")
    protected OperationType addType = OperationType.ENTITY;
    
    @Column(name = "UPDATE_TYPE")
    protected OperationType updateType = OperationType.ENTITY;
    
    @Column(name = "INSPECT_TYPE")
    protected OperationType inspectType = OperationType.ENTITY;
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.OperationTypes#getRemoveType()
     */
    @Override
    public OperationType getRemoveType() {
        return removeType;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.OperationTypes#setRemoveType(org.broadleafcommerce.openadmin.client.datasource.relations.operations.OperationType)
     */
    @Override
    public void setRemoveType(OperationType removeType) {
        this.removeType = removeType;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.OperationTypes#getAddType()
     */
    @Override
    public OperationType getAddType() {
        return addType;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.OperationTypes#setAddType(org.broadleafcommerce.openadmin.client.datasource.relations.operations.OperationType)
     */
    @Override
    public void setAddType(OperationType addType) {
        this.addType = addType;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.OperationTypes#getUpdateType()
     */
    @Override
    public OperationType getUpdateType() {
        return updateType;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.OperationTypes#setUpdateType(org.broadleafcommerce.openadmin.client.datasource.relations.operations.OperationType)
     */
    @Override
    public void setUpdateType(OperationType updateType) {
        this.updateType = updateType;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.OperationTypes#getFetchType()
     */
    @Override
    public OperationType getFetchType() {
        return fetchType;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.OperationTypes#setFetchType(org.broadleafcommerce.openadmin.client.datasource.relations.operations.OperationType)
     */
    @Override
    public void setFetchType(OperationType fetchTyper) {
        this.fetchType = fetchTyper;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.OperationTypes#getInspectType()
     */
    @Override
    public OperationType getInspectType() {
        return inspectType;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.OperationTypes#setInspectType(org.broadleafcommerce.openadmin.client.datasource.relations.operations.OperationType)
     */
    @Override
    public void setInspectType(OperationType inspectType) {
        this.inspectType = inspectType;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.OperationTypes#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.OperationTypes#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((addType == null) ? 0 : addType.hashCode());
        result = prime * result
                + ((fetchType == null) ? 0 : fetchType.hashCode());
        result = prime * result
                + ((inspectType == null) ? 0 : inspectType.hashCode());
        result = prime * result
                + ((removeType == null) ? 0 : removeType.hashCode());
        result = prime * result
                + ((updateType == null) ? 0 : updateType.hashCode());
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
        OperationTypesImpl other = (OperationTypesImpl) obj;
        
        if (id != null && other.id != null) {
            return id.equals(other.id);
        }
        
        if (addType != other.addType)
            return false;
        if (fetchType != other.fetchType)
            return false;
        if (inspectType != other.inspectType)
            return false;
        if (removeType != other.removeType)
            return false;
        if (updateType != other.updateType)
            return false;
        return true;
    }

    
}
