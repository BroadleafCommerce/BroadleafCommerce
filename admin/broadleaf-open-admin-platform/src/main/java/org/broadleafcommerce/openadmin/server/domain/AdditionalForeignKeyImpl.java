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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.openadmin.client.dto.ForeignKeyRestrictionType;
import org.broadleafcommerce.openadmin.server.domain.visitor.PersistencePerspectiveItemVisitor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_SNDBX_ADDL_FRGN_KEY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
public class AdditionalForeignKeyImpl implements ForeignKey {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(generator = "AddlForeignKeyId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "AddlForeignKeyId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "AdditionalForeignKeyImpl", allocationSize = 50)
    @Column(name = "ADDL_FOREIGN_KEY_ID")
    protected Long id;
    
    @Column(name = "MANY_TO_FIELD")
    protected String manyToField;
    
    @Column(name = "FOREIGN_KEY_CLASS")
    protected String foreignKeyClass;
    
    @Column(name = "CURRENT_VALUE")
    protected String currentValue;
    
    @Column(name = "DATA_SOURCE_NAME")
    protected String dataSourceName;
    
    @Column(name = "RESTRICTION_TYPE")
    protected ForeignKeyRestrictionType restrictionType = ForeignKeyRestrictionType.ID_EQ;
    
    @Column(name = "DISPLAY_VALUE_PROP")
    protected String displayValueProperty = "name";
    
    @ManyToOne(targetEntity = PersistencePerspectiveImpl.class)
    @JoinColumn(name = "PERSIST_PERSPECTIVE_ID")
    protected PersistencePerspective persistencePerspective;
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.ForeignKey#getManyToField()
     */
    @Override
    public String getManyToField() {
        return manyToField;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.ForeignKey#setManyToField(java.lang.String)
     */
    @Override
    public void setManyToField(String manyToField) {
        this.manyToField = manyToField;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.ForeignKey#getForeignKeyClass()
     */
    @Override
    public String getForeignKeyClass() {
        return foreignKeyClass;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.ForeignKey#setForeignKeyClass(java.lang.String)
     */
    @Override
    public void setForeignKeyClass(String foreignKeyClass) {
        this.foreignKeyClass = foreignKeyClass;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.ForeignKey#getCurrentValue()
     */
    @Override
    public String getCurrentValue() {
        return currentValue;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.ForeignKey#setCurrentValue(java.lang.String)
     */
    @Override
    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.ForeignKey#getDataSourceName()
     */
    @Override
    public String getDataSourceName() {
        return dataSourceName;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.ForeignKey#setDataSourceName(java.lang.String)
     */
    @Override
    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.ForeignKey#getRestrictionType()
     */
    @Override
    public ForeignKeyRestrictionType getRestrictionType() {
        return restrictionType;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.ForeignKey#setRestrictionType(org.broadleafcommerce.openadmin.client.datasource.relations.ForeignKeyRestrictionType)
     */
    @Override
    public void setRestrictionType(ForeignKeyRestrictionType restrictionType) {
        this.restrictionType = restrictionType;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.ForeignKey#getDisplayValueProperty()
     */
    @Override
    public String getDisplayValueProperty() {
        return displayValueProperty;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.ForeignKey#setDisplayValueProperty(java.lang.String)
     */
    @Override
    public void setDisplayValueProperty(String displayValueProperty) {
        this.displayValueProperty = displayValueProperty;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PersistencePerspective getPersistencePerspective() {
        return persistencePerspective;
    }

    public void setPersistencePerspective(PersistencePerspective persistencePerspective) {
        this.persistencePerspective = persistencePerspective;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((currentValue == null) ? 0 : currentValue.hashCode());
        result = prime * result
                + ((dataSourceName == null) ? 0 : dataSourceName.hashCode());
        result = prime
                * result
                + ((displayValueProperty == null) ? 0 : displayValueProperty
                        .hashCode());
        result = prime * result
                + ((foreignKeyClass == null) ? 0 : foreignKeyClass.hashCode());
        result = prime * result
                + ((manyToField == null) ? 0 : manyToField.hashCode());
        result = prime * result
                + ((restrictionType == null) ? 0 : restrictionType.hashCode());
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
        ForeignKeyImpl other = (ForeignKeyImpl) obj;
        
        if (id != null && other.id != null) {
            return id.equals(other.id);
        }
        
        if (currentValue == null) {
            if (other.currentValue != null)
                return false;
        } else if (!currentValue.equals(other.currentValue))
            return false;
        if (dataSourceName == null) {
            if (other.dataSourceName != null)
                return false;
        } else if (!dataSourceName.equals(other.dataSourceName))
            return false;
        if (displayValueProperty == null) {
            if (other.displayValueProperty != null)
                return false;
        } else if (!displayValueProperty.equals(other.displayValueProperty))
            return false;
        if (foreignKeyClass == null) {
            if (other.foreignKeyClass != null)
                return false;
        } else if (!foreignKeyClass.equals(other.foreignKeyClass))
            return false;
        if (manyToField == null) {
            if (other.manyToField != null)
                return false;
        } else if (!manyToField.equals(other.manyToField))
            return false;
        if (restrictionType != other.restrictionType)
            return false;
        return true;
    }

    @Override
    public void accept(PersistencePerspectiveItemVisitor visitor) {
        visitor.visit(this);
    }
}
