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
package org.broadleafcommerce.core.search.dao;

import java.math.BigDecimal;


/**
 * Container object for the results of a lightweight query that retrieves the product ids
 * for a category along with their display order.
 *
 * @author Andre Azzolini (apazzolini)
 */
public class ProductsByCategoryWithOrder {

    protected Long productId;
    protected BigDecimal displayOrder;

    public ProductsByCategoryWithOrder(Long productId, BigDecimal displayOrder) {
        this.productId = productId;
        this.displayOrder = displayOrder;
    }
    
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public BigDecimal getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(BigDecimal displayOrder) {
        this.displayOrder = displayOrder;
    }
    
}
