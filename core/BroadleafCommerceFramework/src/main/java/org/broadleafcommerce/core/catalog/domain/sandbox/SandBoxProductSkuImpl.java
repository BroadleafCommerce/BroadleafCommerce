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
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.ProductSku;
import org.broadleafcommerce.core.catalog.domain.Sku;
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
/**
 * 
 * @author jfischer
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(appliesTo="BLC_PRODUCT_SKU_SNDBX", indexes={
		@Index(name="PRDCT_SKU_SNDBX_VER_INDX", columnNames={"VERSION"})
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
public class SandBoxProductSkuImpl extends SandBoxProductImpl implements ProductSku, SandBoxItem {

	private static final Log LOG = LogFactory.getLog(SandBoxProductSkuImpl.class);
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    @OneToOne(optional = true, targetEntity = SandBoxSkuImpl.class)
    @JoinTable(name = "BLC_PRODUCT_SKU_ONE_XREF", joinColumns = @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "PRODUCT_ID"), inverseJoinColumns = @JoinColumn(name = "SKU_ID", referencedColumnName = "SKU_ID"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    protected Sku sku;
    
    @Embedded
    protected SandBoxItem sandBoxItem = new EmbeddedSandBoxItem();

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.catalog.domain.ProductSku#getSku()
	 */
	public Sku getSku() {
		return sku;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.catalog.domain.ProductSku#setSku(org.broadleafcommerce.core.catalog.domain.Sku)
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
		final int prime = super.hashCode();
		int result = super.hashCode();
		result = prime * result + ((sku == null) ? 0 : sku.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		SandBoxProductSkuImpl other = (SandBoxProductSkuImpl) obj;
		
		if (!super.equals(obj)) {
			return false;
		}
		
		if (id != null && other.id != null) {
            return id.equals(other.id);
        }
		
		if (sku == null) {
			if (other.sku != null)
				return false;
		} else if (!sku.equals(other.sku))
			return false;
		return true;
	}
    
}
