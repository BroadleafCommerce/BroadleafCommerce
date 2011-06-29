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
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.FeaturedProduct;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.common.FeaturedProductMappedSuperclass;
import org.broadleafcommerce.core.catalog.domain.common.SandBoxItem;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PRDCT_FTRD_SNDBX")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
public class SandBoxFeaturedProductImpl extends FeaturedProductMappedSuperclass implements FeaturedProduct, SandBoxItem {

	private static final long serialVersionUID = 1L;

	@ManyToOne(targetEntity = SandBoxCategoryImpl.class)
    @JoinColumn(name = "CATEGORY_ID")
    @Index(name="PRD_FTRD_CTGRY_SNDBX_INDEX", columnNames={"CATEGORY_ID"})
    protected Category category = new CategoryImpl();

    @ManyToOne(targetEntity = SandBoxProductImpl.class)
    @JoinColumn(name = "PRODUCT_ID")
    @Index(name="PRD_FTRD_PRDCT_SNDBX_INDEX", columnNames={"PRODUCT_ID"})
    protected Product product = new ProductImpl();

    //SandBoxItem fields
    
    @Column(name = "VERSION", nullable=false)
    @Index(name="CAT_SNDBX_VER_INDX", columnNames={"VERSION"})
    protected long version;
    
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

	/**
	 * @return the version
	 */
	public long getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(long version) {
		this.version = version;
	}
    
}