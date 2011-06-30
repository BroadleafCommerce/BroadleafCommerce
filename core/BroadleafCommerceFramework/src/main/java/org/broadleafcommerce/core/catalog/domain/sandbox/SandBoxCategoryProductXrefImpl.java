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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXref;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.common.EmbeddedSandBoxItem;
import org.broadleafcommerce.core.catalog.domain.common.SandBoxItem;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(appliesTo="BLC_CAT_PRDCT_SNDBX_XREF", indexes = {
		@Index(name="CAT_PRDCT_SNDBX_VER_INDX", columnNames={"VERSION"})
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
public class SandBoxCategoryProductXrefImpl implements CategoryProductXref, SandBoxItem {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    SandBoxCategoryProductXrefPk categoryProductXref = new SandBoxCategoryProductXrefPk();
    
    @Embedded
    protected SandBoxItem sandBoxItem = new EmbeddedSandBoxItem();

	/** The display order. */
    @Column(name = "DISPLAY_ORDER")
    protected Long displayOrder;
    
    public SandBoxCategoryProductXrefPk getCategoryProductXref() {
		return categoryProductXref;
	}
    
    public void setCategoryProductXref(SandBoxCategoryProductXrefPk categoryProductXref) {
		this.categoryProductXref = categoryProductXref;
	}

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.CategoryProductXref#getDisplayOrder()
     */
    public Long getDisplayOrder() {
        return displayOrder;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.CategoryProductXref#setDisplayOrder(java.lang.Integer)
     */
    public void setDisplayOrder(Long displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    /**
	 * @return
	 * @see org.broadleafcommerce.core.catalog.domain.sandbox.SandBoxCategoryProductXrefImpl.SandBoxCategoryProductXrefPk#getCategory()
	 */
	public Category getCategory() {
		return categoryProductXref.getCategory();
	}

	/**
	 * @param category
	 * @see org.broadleafcommerce.core.catalog.domain.sandbox.SandBoxCategoryProductXrefImpl.SandBoxCategoryProductXrefPk#setCategory(org.broadleafcommerce.core.catalog.domain.Category)
	 */
	public void setCategory(Category category) {
		categoryProductXref.setCategory(category);
	}

	/**
	 * @return
	 * @see org.broadleafcommerce.core.catalog.domain.sandbox.SandBoxCategoryProductXrefImpl.SandBoxCategoryProductXrefPk#getProduct()
	 */
	public Product getProduct() {
		return categoryProductXref.getProduct();
	}

	/**
	 * @param product
	 * @see org.broadleafcommerce.core.catalog.domain.sandbox.SandBoxCategoryProductXrefImpl.SandBoxCategoryProductXrefPk#setProduct(org.broadleafcommerce.core.catalog.domain.Product)
	 */
	public void setProduct(Product product) {
		categoryProductXref.setProduct(product);
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

	public static class SandBoxCategoryProductXrefPk implements Serializable{
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;
        
        @ManyToOne(targetEntity = SandBoxCategoryImpl.class, optional=false)
        @JoinColumn(name = "CATEGORY_ID")
        protected Category category = new SandBoxCategoryImpl();
        
        /** The product. */
        @ManyToOne(targetEntity = SandBoxProductImpl.class, optional=false)
        @JoinColumn(name = "PRODUCT_ID")
        protected Product product = new SandBoxProductImpl();

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

		@Override
		public int hashCode() {
			return category.hashCode() + product.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
	        if (obj == null) return false;
	        else if (!(obj instanceof SandBoxCategoryProductXrefPk)) return false;

	        return category.getId().equals(((SandBoxCategoryProductXrefPk) obj).category.getId())
	        && product.getId().equals(((SandBoxCategoryProductXrefPk) obj).product.getId());
		}	
        
    }
    
}
