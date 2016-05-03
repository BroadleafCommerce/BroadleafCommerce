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
package org.broadleafcommerce.core.catalog.domain.dto;

import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.Sku;

/**
 * DTO used to carry back the found {@link org.broadleafcommerce.core.catalog.domain.ProductOptionValue#getId()} and
 * {@link org.broadleafcommerce.core.catalog.domain.ProductOption#getAttributeName()} on a given
 * {@link org.broadleafcommerce.core.catalog.domain.Product}
 *
 * @author Jerry Ocanas (jocanas)
 */
public class AssignedProductOptionDTO {

    private Long productId;
    private String productOptionAttrName;
    private ProductOptionValue productOptionValue;
    private Sku sku;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductOptionAttrName() {
        return productOptionAttrName;
    }

    public void setProductOptionAttrName(String productOptionAttrName) {
        this.productOptionAttrName = productOptionAttrName;
    }

    public ProductOptionValue getProductOptionValue() {
        return productOptionValue;
    }

    public void setProductOptionValue(ProductOptionValue productOptionValue) {
        this.productOptionValue = productOptionValue;
    }

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }
}
