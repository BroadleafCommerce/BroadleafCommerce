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
import org.broadleafcommerce.core.catalog.domain.RelatedProduct;
import org.broadleafcommerce.core.catalog.domain.common.CrossSaleProductMappedSuperclass;
import org.broadleafcommerce.core.catalog.domain.common.EmbeddedSandBoxItem;
import org.broadleafcommerce.core.catalog.domain.common.SandBoxItem;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(appliesTo="BLC_PRDCT_CROSS_SALE_SNDBX", indexes={
		@Index(name="CROSSSALE_SNDBX_VER_INDX", columnNames={"VERSION"}),
		@Index(name="CROSSSALE_SNDBX_INDEX", columnNames={"PRODUCT_ID"}),
		@Index(name="CROSSSALE_RELATED_SNDBX_INDEX", columnNames={"RELATED_SALE_PRODUCT_ID"})
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
public class SandBoxCrossSaleProductImpl extends CrossSaleProductMappedSuperclass implements RelatedProduct, SandBoxItem {

	private static final long serialVersionUID = 1L;

	@ManyToOne(targetEntity = SandBoxProductImpl.class, optional=false)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product = new SandBoxProductImpl();

    @ManyToOne(targetEntity = SandBoxProductImpl.class, optional=false)
    @JoinColumn(name = "RELATED_SALE_PRODUCT_ID", referencedColumnName = "PRODUCT_ID")
    private Product relatedSaleProduct = new SandBoxProductImpl();
    
    @Embedded
    protected SandBoxItem sandBoxItem = new EmbeddedSandBoxItem();

    public Product getProduct() {
        return product;
    }

    public Product getRelatedProduct() {
        return relatedSaleProduct;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setRelatedProduct(Product relatedSaleProduct) {
        this.relatedSaleProduct = relatedSaleProduct;
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
}
