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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryXref;
import org.broadleafcommerce.core.catalog.domain.CategoryXrefImpl.CategoryXrefPK;
import org.broadleafcommerce.core.catalog.domain.common.EmbeddedSandBoxItem;
import org.broadleafcommerce.core.catalog.domain.common.SandBoxItem;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;

@Entity
@Table(appliesTo="BLC_CATEGORY_SNDBX_XREF", indexes = {
		@Index(name="CAT_XREF_SNDBX_VER_INDX", columnNames={"VERSION"})
})
public class SandBoxCategoryXrefImpl implements CategoryXref, SandBoxItem {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The category id. */
    @EmbeddedId
    SandBoxCategoryXrefPK categoryXrefPK = new SandBoxCategoryXrefPK();
    
    @Column(name = "DISPLAY_ORDER")
    private Long displayOrder;
    
    @Embedded
    protected SandBoxItem sandBoxItem = new EmbeddedSandBoxItem();

    public SandBoxCategoryXrefPK getCategoryXrefPK() {
        return categoryXrefPK;
    }

    public void setCategoryXrefPK(final SandBoxCategoryXrefPK categoryXrefPK) {
        this.categoryXrefPK = categoryXrefPK;
    }

    public Long getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(final Long displayOrder) {
        this.displayOrder = displayOrder;
    }

    /**
	 * @return
	 * @see org.broadleafcommerce.core.catalog.domain.sandbox.SandBoxCategoryXrefImpl.SandBoxCategoryXrefPK#getCategory()
	 */
	public Category getCategory() {
		return categoryXrefPK.getCategory();
	}

	/**
	 * @param category
	 * @see org.broadleafcommerce.core.catalog.domain.sandbox.SandBoxCategoryXrefImpl.SandBoxCategoryXrefPK#setCategory(org.broadleafcommerce.core.catalog.domain.Category)
	 */
	public void setCategory(Category category) {
		categoryXrefPK.setCategory(category);
	}

	/**
	 * @return
	 * @see org.broadleafcommerce.core.catalog.domain.sandbox.SandBoxCategoryXrefImpl.SandBoxCategoryXrefPK#getSubCategory()
	 */
	public Category getSubCategory() {
		return categoryXrefPK.getSubCategory();
	}

	/**
	 * @param subCategory
	 * @see org.broadleafcommerce.core.catalog.domain.sandbox.SandBoxCategoryXrefImpl.SandBoxCategoryXrefPK#setSubCategory(org.broadleafcommerce.core.catalog.domain.Category)
	 */
	public void setSubCategory(Category subCategory) {
		categoryXrefPK.setSubCategory(subCategory);
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

	public static class SandBoxCategoryXrefPK implements Serializable {
    	
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        @ManyToOne(targetEntity = SandBoxCategoryImpl.class, optional=false)
        @JoinColumn(name = "CATEGORY_ID")
        protected Category category = new SandBoxCategoryImpl();
        
        @ManyToOne(targetEntity = SandBoxCategoryImpl.class, optional=false)
        @JoinColumn(name = "SUB_CATEGORY_ID")
        protected Category subCategory = new SandBoxCategoryImpl();

        public Category getCategory() {
			return category;
		}

		public void setCategory(final Category category) {
			this.category = category;
		}

		public Category getSubCategory() {
			return subCategory;
		}

		public void setSubCategory(final Category subCategory) {
			this.subCategory = subCategory;
		}

		@Override
        public boolean equals(final Object obj) {
            if (obj == null) return false;
            else if (!(obj instanceof CategoryXrefPK)) return false;

            return category.getId().equals(((SandBoxCategoryXrefPK) obj).getCategory().getId())
            && subCategory.getId().equals(((SandBoxCategoryXrefPK) obj).getSubCategory().getId());
        }
		

        @Override
        public int hashCode() {
        	return category.hashCode() + subCategory.hashCode();
        }


    }

}
