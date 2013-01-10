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

import org.broadleafcommerce.openadmin.server.service.type.ChangeType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import javax.persistence.*;

@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_ENTITY_SNDBX_ITEM")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
public class EntitySandBoxItemImpl extends SandBoxItemImpl implements EntitySandBoxItem {

    private static final long serialVersionUID = 1L;
    
    @ManyToOne(targetEntity = EntityImpl.class, cascade = {CascadeType.ALL})
    @JoinColumn(name = "ENTITY_ID")
    protected Entity entity;

    @Column(name = "SANDBOX_TEMP_ITEM_ID")
    @Index(name="SNDBX_ITM_TMP_ID", columnNames={"SANDBOX_TEMP_ITEM_ID"})
    protected Long temporaryId;
    
    @ManyToOne(targetEntity = PersistencePerspectiveImpl.class, cascade = {CascadeType.ALL})
    @JoinColumn(name = "PERSIST_PERSPECTVE_ID")
    protected PersistencePerspective persistencePerspective;

    @Column(name = "CEILING_ENTITY")
    protected String ceilingEntityFullyQualifiedClassname;

    @Column(name = "CUST_CRITERIA")
    protected String customCriteria;

    @Column(name = "CHANGE_TYPE")
    @Index(name="SNDBX_ITM_CHG_TYPE", columnNames={"CHANGE_TYPE"})
    protected ChangeType changeType;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.SandBoxItem#getEntity()
     */
    @Override
    public Entity getEntity() {
        return entity;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.SandBoxItem#setEntity(org.broadleafcommerce.openadmin.domain.Entity)
     */
    @Override
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.SandBoxItem#getPersistencePerspective()
     */
    @Override
    public PersistencePerspective getPersistencePerspective() {
        return persistencePerspective;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.SandBoxItem#setPersistencePerspective(org.broadleafcommerce.openadmin.domain.PersistencePerspective)
     */
    @Override
    public void setPersistencePerspective(
            PersistencePerspective persistencePerspective) {
        this.persistencePerspective = persistencePerspective;
    }

    @Override
    public String getCeilingEntityFullyQualifiedClassname() {
        return ceilingEntityFullyQualifiedClassname;
    }

    @Override
    public void setCeilingEntityFullyQualifiedClassname(String ceilingEntityFullyQualifiedClassname) {
        this.ceilingEntityFullyQualifiedClassname = ceilingEntityFullyQualifiedClassname;
    }

    @Override
    public String getCustomCriteria() {
        return customCriteria;
    }

    @Override
    public void setCustomCriteria(String customCriteria) {
        this.customCriteria = customCriteria;
    }

    @Override
    public ChangeType getChangeType() {
        return changeType;
    }

    @Override
    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    @Override
    public Long getTemporaryId() {
        return temporaryId;
    }

    @Override
    public void setTemporaryId(Long temporaryId) {
        this.temporaryId = temporaryId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((entity == null) ? 0 : entity.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime
                * result
                + ((persistencePerspective == null) ? 0
                        : persistencePerspective.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EntitySandBoxItemImpl other = (EntitySandBoxItemImpl) obj;
        if (entity == null) {
            if (other.entity != null)
                return false;
        } else if (!entity.equals(other.entity))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (persistencePerspective == null) {
            if (other.persistencePerspective != null)
                return false;
        } else if (!persistencePerspective.equals(other.persistencePerspective))
            return false;
        return true;
    }
    
}
