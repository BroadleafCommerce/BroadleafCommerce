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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


/**
 * The Class CategoryXref is for testing purposes only.  It helps autogenerate the cross reference table
 * properly with the DISPLY_ORDER column

 * @author krosenberg
 *
 */
@Entity
@Table(name = "BLC_CATEGORY_XREF")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CategoryXref implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The category id. */
    @Id
    @Column(name = "ID")
    protected String id;

    @Column(name = "DISPLAY_ORDER")
    private Long displayOrder;

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

    public Long getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(final Long displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass())) return false;

        CategoryXref that = (CategoryXref) o;

        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        if (subCategory != null ? !subCategory.equals(that.subCategory) : that.subCategory != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = category != null ? category.hashCode() : 0;
        result = 31 * result + (subCategory != null ? subCategory.hashCode() : 0);
        return result;
    }
}
