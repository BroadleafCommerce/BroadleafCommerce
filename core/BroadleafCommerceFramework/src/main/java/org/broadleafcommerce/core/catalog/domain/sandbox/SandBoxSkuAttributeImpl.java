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
package org.broadleafcommerce.core.catalog.domain.sandbox;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuAttribute;
import org.broadleafcommerce.core.catalog.domain.common.EmbeddedSandBoxItem;
import org.broadleafcommerce.core.catalog.domain.common.SandBoxItem;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;

/*
 * TODO emit these java files and compile at runtime based on an annotation
 * present in the normal entity. This will be part of special persistence
 * class handling that will be introduced into MergePersistenceUnitManager.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(appliesTo="BLC_SKU_ATTR_SNDBX", indexes={
		@Index(name="SKU_ATTR_SNDBX_VER_INDX", columnNames={"VERSION"}),
		@Index(name="SKU_ATTR_SNDBX_NAME_INDEX", columnNames={"NAME"}),
		@Index(name="SKU_ATTR_SNDBX_SKU_INDEX", columnNames={"SKU_ID"})
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
public class SandBoxSkuAttributeImpl implements SkuAttribute, SandBoxItem {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The id. */
    @Id
    @GeneratedValue(generator = "SkuAttributeId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "SkuAttributeId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "SkuAttributeImpl", allocationSize = 50)
    @Column(name = "SKU_ATTR_ID")
    protected Long id;
    
    /** The name. */
    @Column(name = "NAME", nullable=false)
    protected String name;

    /** The value. */
    @Column(name = "VALUE", nullable=false)
    protected String value;

    /** The searchable. */
    @Column(name = "SEARCHABLE")
    protected Boolean searchable;
    
    /** The sku. */
    @ManyToOne(targetEntity = SandBoxSkuImpl.class, optional=false)
    @JoinColumn(name = "SKU_ID")
    protected Sku sku;
    
    @Embedded
    protected SandBoxItem sandBoxItem = new EmbeddedSandBoxItem();
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.SkuAttribute#getId()
     */
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.SkuAttribute#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.SkuAttribute#getValue()
     */
    public String getValue() {
        return value;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.SkuAttribute#setValue(java.lang.String)
     */
    public void setValue(String value) {
        this.value = value;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.SkuAttribute#getSearchable()
     */
    public Boolean getSearchable() {
        return searchable;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.SkuAttribute#setSearchable(java.lang.Boolean)
     */
    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.SkuAttribute#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.SkuAttribute#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return value;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.SkuAttribute#getSku()
     */
    public Sku getSku() {
        return sku;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.SkuAttribute#setSku(org.broadleafcommerce.core.catalog.domain.Sku)
     */
    public void setSku(Sku sku) {
        this.sku = sku;
    }

    /**
	 * @return
	 * @see org.broadleafcommerce.core.catalog.domain.common.SandBoxItem#getVersion()
	 */
	public long getVersion() {
		return sandBoxItem.getVersion();
	}

	/**
	 * @param version
	 * @see org.broadleafcommerce.core.catalog.domain.common.SandBoxItem#setVersion(long)
	 */
	public void setVersion(long version) {
		sandBoxItem.setVersion(version);
	}

	/**
	 * @return
	 * @see org.broadleafcommerce.core.catalog.domain.common.SandBoxItem#isDirty()
	 */
	public boolean isDirty() {
		return sandBoxItem.isDirty();
	}

	/**
	 * @param dirty
	 * @see org.broadleafcommerce.core.catalog.domain.common.SandBoxItem#setDirty(boolean)
	 */
	public void setDirty(boolean dirty) {
		sandBoxItem.setDirty(dirty);
	}

	/**
	 * @return
	 * @see org.broadleafcommerce.core.catalog.domain.common.SandBoxItem#getCommaDelimitedDirtyFields()
	 */
	public String getCommaDelimitedDirtyFields() {
		return sandBoxItem.getCommaDelimitedDirtyFields();
	}

	/**
	 * @param commaDelimitedDirtyFields
	 * @see org.broadleafcommerce.core.catalog.domain.common.SandBoxItem#setCommaDelimitedDirtyFields(java.lang.String)
	 */
	public void setCommaDelimitedDirtyFields(String commaDelimitedDirtyFields) {
		sandBoxItem.setCommaDelimitedDirtyFields(commaDelimitedDirtyFields);
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((sku == null) ? 0 : sku.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        SandBoxSkuAttributeImpl other = (SandBoxSkuAttributeImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (sku == null) {
            if (other.sku != null)
                return false;
        } else if (!sku.equals(other.sku))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

}
