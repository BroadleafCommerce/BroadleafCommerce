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
@Table(name="BLC_SNDBX_MAP_STRCTR")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
public class MapStructureImpl extends PersistencePerspectiveItemImpl implements MapStructure {

    private static final long serialVersionUID = 1L;
    
    @Column(name = "KEY_CLASS_NAME")
    protected String keyClassName;
    
    @Column(name = "KEY_PROPERTY_NAME")
    protected String keyPropertyName;
    
    @Column(name = "KEY_PROP_FRIENDLY_NAME")
    protected String keyPropertyFriendlyName;
    
    @Column(name = "VALUE_CLASS_NAME")
    protected String valueClassName;
    
    @Column(name = "MAP_PROPERTY")
    protected String mapProperty;
    
    @Column(name = "DELETE_VALUE_ENTITY")
    protected Boolean deleteValueEntity = Boolean.FALSE;
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.MapStructure#getKeyClassName()
     */
    @Override
    public String getKeyClassName() {
        return keyClassName;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.MapStructure#setKeyClassName(java.lang.String)
     */
    @Override
    public void setKeyClassName(String keyClassName) {
        if (!keyClassName.equals(String.class.getName())) {
            throw new RuntimeException("keyClass of java.lang.String is currently the only type supported");
        }
        this.keyClassName = keyClassName;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.MapStructure#getValueClassName()
     */
    @Override
    public String getValueClassName() {
        return valueClassName;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.MapStructure#setValueClassName(java.lang.String)
     */
    @Override
    public void setValueClassName(String valueClassName) {
        this.valueClassName = valueClassName;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.MapStructure#getMapProperty()
     */
    @Override
    public String getMapProperty() {
        return mapProperty;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.MapStructure#setMapProperty(java.lang.String)
     */
    @Override
    public void setMapProperty(String mapProperty) {
        this.mapProperty = mapProperty;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.MapStructure#getKeyPropertyName()
     */
    @Override
    public String getKeyPropertyName() {
        return keyPropertyName;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.MapStructure#setKeyPropertyName(java.lang.String)
     */
    @Override
    public void setKeyPropertyName(String keyPropertyName) {
        this.keyPropertyName = keyPropertyName;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.MapStructure#getKeyPropertyFriendlyName()
     */
    @Override
    public String getKeyPropertyFriendlyName() {
        return keyPropertyFriendlyName;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.MapStructure#setKeyPropertyFriendlyName(java.lang.String)
     */
    @Override
    public void setKeyPropertyFriendlyName(String keyPropertyFriendlyName) {
        this.keyPropertyFriendlyName = keyPropertyFriendlyName;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.MapStructure#getDeleteValueEntity()
     */
    @Override
    public Boolean getDeleteValueEntity() {
        return deleteValueEntity;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.MapStructure#setDeleteValueEntity(java.lang.Boolean)
     */
    @Override
    public void setDeleteValueEntity(Boolean deleteValueEntity) {
        this.deleteValueEntity = deleteValueEntity;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime
                * result
                + ((deleteValueEntity == null) ? 0 : deleteValueEntity
                        .hashCode());
        result = prime * result
                + ((keyClassName == null) ? 0 : keyClassName.hashCode());
        result = prime
                * result
                + ((keyPropertyFriendlyName == null) ? 0
                        : keyPropertyFriendlyName.hashCode());
        result = prime * result
                + ((keyPropertyName == null) ? 0 : keyPropertyName.hashCode());
        result = prime * result
                + ((mapProperty == null) ? 0 : mapProperty.hashCode());
        result = prime * result
                + ((valueClassName == null) ? 0 : valueClassName.hashCode());
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
        MapStructureImpl other = (MapStructureImpl) obj;
        if (deleteValueEntity == null) {
            if (other.deleteValueEntity != null)
                return false;
        } else if (!deleteValueEntity.equals(other.deleteValueEntity))
            return false;
        if (keyClassName == null) {
            if (other.keyClassName != null)
                return false;
        } else if (!keyClassName.equals(other.keyClassName))
            return false;
        if (keyPropertyFriendlyName == null) {
            if (other.keyPropertyFriendlyName != null)
                return false;
        } else if (!keyPropertyFriendlyName
                .equals(other.keyPropertyFriendlyName))
            return false;
        if (keyPropertyName == null) {
            if (other.keyPropertyName != null)
                return false;
        } else if (!keyPropertyName.equals(other.keyPropertyName))
            return false;
        if (mapProperty == null) {
            if (other.mapProperty != null)
                return false;
        } else if (!mapProperty.equals(other.mapProperty))
            return false;
        if (valueClassName == null) {
            if (other.valueClassName != null)
                return false;
        } else if (!valueClassName.equals(other.valueClassName))
            return false;
        return true;
    }

    @Override
    public void accept(PersistencePerspectiveItemVisitor visitor) {
        visitor.visit(this);
    }
}
