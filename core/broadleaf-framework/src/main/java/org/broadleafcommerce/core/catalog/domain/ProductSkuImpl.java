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

package org.broadleafcommerce.core.catalog.domain;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 
 * ProductImpl should be used for extension which contains defaultSku
 * 
 * @author jfischer
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PRODUCT_SKU")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@AdminPresentationClass(friendlyName = "ProductSkuImpl_skuProduct")
public class ProductSkuImpl extends ProductImpl implements ProductSku {

	private static final Log LOG = LogFactory.getLog(ProductSkuImpl.class);
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    @OneToOne(optional = true, targetEntity = SkuImpl.class)
    @JoinColumn(name = "SKU_ID")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    protected Sku sku;

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
	
	public Sku getDefaultSku() {
		return sku;
	}
	
	public void setDefaultSku(Sku defaultSku){
		this.sku = defaultSku;
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
		
		ProductSkuImpl other = (ProductSkuImpl) obj;
		
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
