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
package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The Class CategoryXref is for testing purposes only.  It helps autogenerate the cross reference table
 * properly with the DISPLY_ORDER column

 * @author krosenberg
 *
 */
@Entity
@Table(name = "BLC_CATEGORY_XREF")
public class CategoryXref implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The category id. */
    @EmbeddedId
    CategoryXrefPK categoryXrefPK;

    public CategoryXrefPK getCategoryXrefPK() {
        return categoryXrefPK;
    }

    public void setCategoryXrefPK(CategoryXrefPK categoryXrefPK) {
        this.categoryXrefPK = categoryXrefPK;
    }

    @Column(name = "DISPLAY_ORDER")
    private int displayOrder;

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public static class CategoryXrefPK implements Serializable {
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        @Column(name = "CATEGORY_ID", nullable = false)
        private Long categoryId;

        /** The sub-category id. */
        @Column(name = "SUB_CATEGORY_ID", nullable = false)
        private Long subCategoryId;

        public Long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }

        public Long getSubCategoryId() {
            return subCategoryId;
        }

        public void setSubCategoryId(Long subCategoryId) {
            this.subCategoryId = subCategoryId;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            else if (!(obj instanceof CategoryXrefPK)) return false;

            return categoryId.equals(((CategoryXrefPK) obj).getCategoryId())
            && subCategoryId.equals(((CategoryXrefPK) obj).getSubCategoryId());
        }

        @Override
        public int hashCode() {
            return categoryId.hashCode() + subCategoryId.hashCode();
        }


    }

}
