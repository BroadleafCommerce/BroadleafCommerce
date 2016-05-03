/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Implementations of this interface are used to hold data about the many-to-many relationship between
 * the Category table and the Product table.
 * </p>
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
