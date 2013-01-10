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

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 
 * @author jfischer
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_SNDBX_PROPERTY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
public class PropertyImpl implements Property {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(generator = "PropertyId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PropertyId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "PropertyImpl", allocationSize = 50)
    @Column(name = "PROPERTY_ID")
    protected Long id;
    
    @Column(name = "NAME")
    protected String name;
    
    @Column(name = "VALUE")
    private String value;
    
    @Column(name = "DISPLAY_VALUE")
    protected String displayValue;
    
    @Column(name = "IS_DIRTY")
    protected Boolean isDirty = false;
    
    @ManyToOne(targetEntity = EntityImpl.class)
    @JoinColumn(name = "ENTITY_ID")
    protected org.broadleafcommerce.openadmin.server.domain.Entity entity;

    @Column(name = "SECONDARY_TYPE")
    protected SupportedFieldType secondaryType;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.Property#getName()
     */
    @Override
    public String getName() {
        return name;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.Property#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.Property#getValue()
     */
    @Override
    public String getValue() {
        return value;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.Property#setValue(java.lang.String)
     */
    @Override
    public void setValue(String value) {
        this.value = value;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.Property#getDisplayValue()
     */
    @Override
    public String getDisplayValue() {
        return displayValue;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.Property#setDisplayValue(java.lang.String)
     */
    @Override
    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    /**
     * @return the entity
     */
    public org.broadleafcommerce.openadmin.server.domain.Entity getEntity() {
        return entity;
    }

    /**
     * @param entity the entity to set
     */
    public void setEntity(org.broadleafcommerce.openadmin.server.domain.Entity entity) {
        this.entity = entity;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsDirty() {
        return isDirty;
    }

    public void setIsDirty(Boolean isDirty) {
        this.isDirty = isDirty;
    }

    public SupportedFieldType getSecondaryType() {
        return secondaryType;
    }

    public void setSecondaryType(SupportedFieldType secondaryType) {
        this.secondaryType = secondaryType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        PropertyImpl other = (PropertyImpl) obj;
        
        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
