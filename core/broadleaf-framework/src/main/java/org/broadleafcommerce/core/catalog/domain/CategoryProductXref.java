/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
 * #L%
 */
package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Implementations of this interface are used to hold data about the many-to-many relationship between
 * the Category table and the Product table.  This entity is only used for executing a named
 * query.
 * <br>
 * <br>
 * You should implement this class if you want to make significant changes to the
 * relationship between Category and Product.  If you just want to add additional fields
 * then you should extend {@link CategoryProductXrefImpl}.
 * 
 *  @see {@link CategoryProductXrefImpl},{@link Category}, {@link Product}
 *  @author btaylor
 * 
 */
public interface CategoryProductXref extends Serializable,MultiTenantCloneable<CategoryProductXref> {

    /**
     * Gets the category.
     * 
     * @return the category
     */
    Category getCategory();

    /**
     * Sets the category.
     * 
     * @param category the new category
     */
    void setCategory(Category category);

    /**
     * Gets the product.
     * 
     * @return the product
     */
    Product getProduct();

    /**
     * Sets the product.
     * 
     * @param product the new product
     */
    void setProduct(Product product);

    /**
     * Gets the display order.
     * 
     * @return the display order
     */
    BigDecimal getDisplayOrder();

    /**
     * Sets the display order.
     * 
     * @param displayOrder the new display order
     */
    void setDisplayOrder(BigDecimal displayOrder);

    void setId(Long id);

    Long getId();
    
    /**
     * Specifies the default reference between a category and a product. The default reference is used
     * to drive cononical urls and also drives inheritance of fulfillment types and inventory types from the category to
     * the product. This replaces the concept of {@link ProductImpl#getDefaultCategory()}
     *
     * @see ProductImpl#getCategory()
     * @return the default reference between a product and a category
     */
    Boolean getDefaultReference();

    void setDefaultReference(Boolean defaultReference);

}
