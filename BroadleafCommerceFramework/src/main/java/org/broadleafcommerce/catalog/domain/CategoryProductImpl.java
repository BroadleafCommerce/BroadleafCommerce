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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * The Class CategoryProductImpl is the default implmentation of {@link Category}.
 * This entity is only used for executing a named query.
 * 
 * If you want to add fields specific to your implementation of BroadLeafCommerce you should extend
 * this class and add your fields.  If you need to make significant changes to the class then you
 * should implement your own version of {@link Category}.
 * <br>
 * <br>
 * This implementation uses a Hibernate implementation of JPA configured through annotations.
 * The Entity references the following tables:
 * BLC_CATEGORY_PRODUCT_XREF,
 * 
 * @see {@link Category}, {@link ProductImpl}
 * @author btaylor
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CATEGORY_PRODUCT_XREF")
public class CategoryProductImpl implements CategoryProduct {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The id. */
    @Id
    @GeneratedValue(generator = "CategoryProductId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "CategoryProductId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "CategoryProductImpl", allocationSize = 50)
    @Column(name = "ID")
    protected Long id;

    /** The category. */
    @ManyToOne(targetEntity = CategoryImpl.class, optional=false)
    @JoinColumn(name = "CATEGORY_ID")
    protected Category category;

    /** The product. */
    @ManyToOne(targetEntity = ProductImpl.class, optional=false)
    @JoinColumn(name = "PRODUCT_ID")
    protected Product product;

    /** The display order. */
    @Column(name = "DISPLAY_ORDER")
    protected Integer displayOrder;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.CategoryProduct#getId()
     */
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.CategoryProduct#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.CategoryProduct#getCategory()
     */
    public Category getCategory() {
        return category;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.CategoryProduct#setCategory(org.broadleafcommerce.catalog.domain.Category)
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.CategoryProduct#getProduct()
     */
    public Product getProduct() {
        return product;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.CategoryProduct#setProduct(org.broadleafcommerce.catalog.domain.Product)
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.CategoryProduct#getDisplayOrder()
     */
    public Integer getDisplayOrder() {
        return displayOrder;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.CategoryProduct#setDisplayOrder(java.lang.Integer)
     */
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((displayOrder == null) ? 0 : displayOrder.hashCode());
        result = prime * result + ((product == null) ? 0 : product.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CategoryProductImpl other = (CategoryProductImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (category == null) {
            if (other.category != null)
                return false;
        } else if (!category.equals(other.category))
            return false;
        if (displayOrder == null) {
            if (other.displayOrder != null)
                return false;
        } else if (!displayOrder.equals(other.displayOrder))
            return false;
        if (product == null) {
            if (other.product != null)
                return false;
        } else if (!product.equals(other.product))
            return false;
        return true;
    }
}
