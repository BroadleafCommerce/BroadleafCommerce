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

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.FeaturedProduct;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.common.EmbeddedSandBoxItem;
import org.broadleafcommerce.core.catalog.domain.common.SandBoxItem;
import org.broadleafcommerce.presentation.AdminPresentation;
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
@Table(appliesTo="BLC_PRDCT_FTRD_SNDBX", indexes = {
		@Index(name="FP_SNDBX_VER_INDX", columnNames={"VERSION"})
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
public class SandBoxFeaturedProductImpl implements FeaturedProduct, SandBoxItem {

	private static final long serialVersionUID = 1L;

	/** The id. */
    @Id
    @GeneratedValue(generator = "FeaturedProductId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "FeaturedProductId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "SandBoxFeaturedProductImpl", allocationSize = 50)
    @Column(name = "FEATURED_PRODUCT_ID")
    protected Long id;
    
    @Column(name = "SEQUENCE")
    protected Long sequence;

    @Column(name = "PROMOTION_MESSAGE")
    @AdminPresentation(friendlyName="Featured Product Promotion Message", largeEntry=true)
    protected String promotionMessage;
    
	@ManyToOne(targetEntity = SandBoxCategoryImpl.class)
    @JoinColumn(name = "CATEGORY_ID")
    @Index(name="PRD_FTRD_CTGRY_SNDBX_INDEX", columnNames={"CATEGORY_ID"})
    protected Category category = new SandBoxCategoryImpl();

    @ManyToOne(targetEntity = SandBoxProductImpl.class)
    @JoinColumn(name = "PRODUCT_ID")
    @Index(name="PRD_FTRD_PRDCT_SNDBX_INDEX", columnNames={"PRODUCT_ID"})
    protected Product product = new SandBoxProductImpl();

    @Embedded
    protected SandBoxItem sandBoxItem = new EmbeddedSandBoxItem();
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }
    
    public Long getSequence() {
    	return this.sequence;
    }

    public String getPromotionMessage() {
        return promotionMessage;
    }

    public void setPromotionMessage(String promotionMessage) {
        this.promotionMessage = promotionMessage;
    }
    
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