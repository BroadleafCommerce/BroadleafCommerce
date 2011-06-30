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

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductAttribute;
import org.broadleafcommerce.core.catalog.domain.common.EmbeddedSandBoxItem;
import org.broadleafcommerce.core.catalog.domain.common.ProductAttributeMappedSuperclass;
import org.broadleafcommerce.core.catalog.domain.common.SandBoxItem;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;

/**
 * The Class ProductAttributeImpl.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(appliesTo="BLC_PRODUCT_ATTR_SNDBX", indexes={
		@Index(name="PRDCT_ATTR_SNDBX_VER_INDX", columnNames={"VERSION"}),
		@Index(name="PRDCT_ATTR_SNDBX_NAME_INDX", columnNames={"NAME"}),
		@Index(name="PRDCT_ATTR_SNDBX_INDEX", columnNames={"PRODUCT_ID"})
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
public class SandBoxProductAttributeImpl extends ProductAttributeMappedSuperclass implements ProductAttribute, SandBoxItem {

	/** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The product. */
    @ManyToOne(targetEntity = SandBoxProductImpl.class, optional=false)
    @JoinColumn(name = "PRODUCT_ID")
    protected Product product;
    
    @Embedded
    protected SandBoxItem sandBoxItem = new EmbeddedSandBoxItem();

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.ProductAttribute#getProduct()
     */
    public Product getProduct() {
        return product;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.ProductAttribute#setProduct(org.broadleafcommerce.core.catalog.domain.Product)
     */
    public void setProduct(Product product) {
        this.product = product;
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
        result = prime * result + ((product == null) ? 0 : product.hashCode());
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
        SandBoxProductAttributeImpl other = (SandBoxProductAttributeImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (product == null) {
            if (other.product != null)
                return false;
        } else if (!product.equals(other.product))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }
}
