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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.broadleafcommerce.openadmin.server.domain.visitor.PersistencePerspectiveItemVisitor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 
 * @author jfischer
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_SNDBX_JOIN_STRCTR")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
public class JoinStructureImpl extends PersistencePerspectiveItemImpl implements JoinStructure {

    private static final long serialVersionUID = 1L;

    @Column(name = "NAME")
    protected String name;
    
    @Column(name = "LINKED_OBJ_PATH")
    protected String linkedObjectPath;
    
    @Column(name = "TARGET_OBJ_PATH")
    protected String targetObjectPath;
    
    @Column(name = "JOIN_STRCTR_ENTITY_CLSNM")
    protected String joinStructureEntityClassname;
    
    @Column(name = "SORT_FIELD")
    protected String sortField;
    
    @Column(name = "SORT_ASC")
    protected Boolean sortAscending;
    
    @Column(name = "LINKED_ID_PROP")
    protected String linkedIdProperty;
    
    @Column(name = "TARGET_ID_PROP")
    protected String targetIdProperty;
    
    @Column(name = "INVERSE")
    protected Boolean inverse = Boolean.FALSE;
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.JoinStructure#getName()
     */
    @Override
    public String getName() {
        return name;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.JoinStructure#setName(java.lang.String)
     */
    @Override
    public void setName(String manyToField) {
        this.name = manyToField;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.JoinStructure#getLinkedObjectPath()
     */
    @Override
    public String getLinkedObjectPath() {
        return linkedObjectPath;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.JoinStructure#setLinkedObjectPath(java.lang.String)
     */
    @Override
    public void setLinkedObjectPath(String linkedPropertyPath) {
        this.linkedObjectPath = linkedPropertyPath;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.JoinStructure#getTargetObjectPath()
     */
    @Override
    public String getTargetObjectPath() {
        return targetObjectPath;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.JoinStructure#setTargetObjectPath(java.lang.String)
     */
    @Override
    public void setTargetObjectPath(String targetObjectPath) {
        this.targetObjectPath = targetObjectPath;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.JoinStructure#getJoinStructureEntityClassname()
     */
    @Override
    public String getJoinStructureEntityClassname() {
        return joinStructureEntityClassname;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.JoinStructure#setJoinStructureEntityClassname(java.lang.String)
     */
    @Override
    public void setJoinStructureEntityClassname(String joinStructureEntityClassname) {
        this.joinStructureEntityClassname = joinStructureEntityClassname;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.JoinStructure#getSortField()
     */
    @Override
    public String getSortField() {
        return sortField;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.JoinStructure#setSortField(java.lang.String)
     */
    @Override
    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.JoinStructure#getSortAscending()
     */
    @Override
    public Boolean getSortAscending() {
        return sortAscending;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.JoinStructure#setSortAscending(java.lang.Boolean)
     */
    @Override
    public void setSortAscending(Boolean sortAscending) {
        this.sortAscending = sortAscending;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.JoinStructure#getLinkedIdProperty()
     */
    @Override
    public String getLinkedIdProperty() {
        return linkedIdProperty;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.JoinStructure#setLinkedIdProperty(java.lang.String)
     */
    @Override
    public void setLinkedIdProperty(String linkedIdProperty) {
        this.linkedIdProperty = linkedIdProperty;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.JoinStructure#getTargetIdProperty()
     */
    @Override
    public String getTargetIdProperty() {
        return targetIdProperty;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.JoinStructure#setTargetIdProperty(java.lang.String)
     */
    @Override
    public void setTargetIdProperty(String targetIdProperty) {
        this.targetIdProperty = targetIdProperty;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.JoinStructure#getInverse()
     */
    @Override
    public Boolean getInverse() {
        return inverse;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.JoinStructure#setInverse(java.lang.Boolean)
     */
    @Override
    public void setInverse(Boolean inverse) {
        this.inverse = inverse;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((inverse == null) ? 0 : inverse.hashCode());
        result = prime
                * result
                + ((joinStructureEntityClassname == null) ? 0
                        : joinStructureEntityClassname.hashCode());
        result = prime
                * result
                + ((linkedIdProperty == null) ? 0 : linkedIdProperty.hashCode());
        result = prime
                * result
                + ((linkedObjectPath == null) ? 0 : linkedObjectPath.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result
                + ((sortAscending == null) ? 0 : sortAscending.hashCode());
        result = prime * result
                + ((sortField == null) ? 0 : sortField.hashCode());
        result = prime
                * result
                + ((targetIdProperty == null) ? 0 : targetIdProperty.hashCode());
        result = prime
                * result
                + ((targetObjectPath == null) ? 0 : targetObjectPath.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        JoinStructureImpl other = (JoinStructureImpl) obj;
        if (inverse == null) {
            if (other.inverse != null)
                return false;
        } else if (!inverse.equals(other.inverse))
            return false;
        if (joinStructureEntityClassname == null) {
            if (other.joinStructureEntityClassname != null)
                return false;
        } else if (!joinStructureEntityClassname
                .equals(other.joinStructureEntityClassname))
            return false;
        if (linkedIdProperty == null) {
            if (other.linkedIdProperty != null)
                return false;
        } else if (!linkedIdProperty.equals(other.linkedIdProperty))
            return false;
        if (linkedObjectPath == null) {
            if (other.linkedObjectPath != null)
                return false;
        } else if (!linkedObjectPath.equals(other.linkedObjectPath))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (sortAscending == null) {
            if (other.sortAscending != null)
                return false;
        } else if (!sortAscending.equals(other.sortAscending))
            return false;
        if (sortField == null) {
            if (other.sortField != null)
                return false;
        } else if (!sortField.equals(other.sortField))
            return false;
        if (targetIdProperty == null) {
            if (other.targetIdProperty != null)
                return false;
        } else if (!targetIdProperty.equals(other.targetIdProperty))
            return false;
        if (targetObjectPath == null) {
            if (other.targetObjectPath != null)
                return false;
        } else if (!targetObjectPath.equals(other.targetObjectPath))
            return false;
        return true;
    }

    @Override
    public void accept(PersistencePerspectiveItemVisitor visitor) {
        visitor.visit(this);
    }
    
}
