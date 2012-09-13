/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.catalog.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The Class CategoryXrefImpl is for testing purposes only.  It helps autogenerate the cross reference table
 * properly with the DISPLY_ORDER column

 * @author krosenberg
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CATEGORY_XREF")
public class CategoryXrefImpl implements CategoryXref {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The category id. */
    @EmbeddedId
    CategoryXrefPK categoryXrefPK = new CategoryXrefPK();

    public CategoryXrefPK getCategoryXrefPK() {
        return categoryXrefPK;
    }

    public void setCategoryXrefPK(final CategoryXrefPK categoryXrefPK) {
        this.categoryXrefPK = categoryXrefPK;
    }

    @Column(name = "DISPLAY_ORDER")
    private Long displayOrder;

    public Long getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(final Long displayOrder) {
        this.displayOrder = displayOrder;
    }

    /**
	 * @return
	 * @see org.broadleafcommerce.core.catalog.domain.CategoryXrefImpl.CategoryXrefPK#getCategory()
	 */
	public Category getCategory() {
		return categoryXrefPK.getCategory();
	}

	/**
	 * @param category
	 * @see org.broadleafcommerce.core.catalog.domain.CategoryXrefImpl.CategoryXrefPK#setCategory(org.broadleafcommerce.core.catalog.domain.Category)
	 */
	public void setCategory(Category category) {
		categoryXrefPK.setCategory(category);
	}

	/**
	 * @return
	 * @see org.broadleafcommerce.core.catalog.domain.CategoryXrefImpl.CategoryXrefPK#getSubCategory()
	 */
	public Category getSubCategory() {
		return categoryXrefPK.getSubCategory();
	}

	/**
	 * @param subCategory
	 * @see org.broadleafcommerce.core.catalog.domain.CategoryXrefImpl.CategoryXrefPK#setSubCategory(org.broadleafcommerce.core.catalog.domain.Category)
	 */
	public void setSubCategory(Category subCategory) {
		categoryXrefPK.setSubCategory(subCategory);
	}

	public static class CategoryXrefPK implements Serializable {
    	
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        @ManyToOne(targetEntity = CategoryImpl.class, optional=false)
        @JoinColumn(name = "CATEGORY_ID")
        protected Category category = new CategoryImpl();
        
        @ManyToOne(targetEntity = CategoryImpl.class, optional=false)
        @JoinColumn(name = "SUB_CATEGORY_ID")
        protected Category subCategory = new CategoryImpl();

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

            return category.getId().equals(((CategoryXrefPK) obj).getCategory().getId())
            && subCategory.getId().equals(((CategoryXrefPK) obj).getSubCategory().getId());
        }
		

        @Override
        public int hashCode() {
        	return category.hashCode() + subCategory.hashCode();
        }


    }

}
