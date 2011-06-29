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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryXref.CategoryXrefPK;

/**
 * The Class SandBoxCategoryXref is for testing purposes only.  It helps autogenerate the cross reference table
 * properly with the DISPLY_ORDER column

 * @author krosenberg
 *
 */
@Entity
@Table(name = "BLC_CATEGORY_SNDBX_XREF")
public class SandBoxCategoryXref implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The category id. */
    @EmbeddedId
    SandBoxCategoryXrefPK categoryXrefPK = new SandBoxCategoryXrefPK();

    public SandBoxCategoryXrefPK getCategoryXrefPK() {
        return categoryXrefPK;
    }

    public void setCategoryXrefPK(final SandBoxCategoryXrefPK categoryXrefPK) {
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
