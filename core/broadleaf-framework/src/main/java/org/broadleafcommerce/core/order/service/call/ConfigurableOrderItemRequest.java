/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.order.service.call;


import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Product;

public class ConfigurableOrderItemRequest extends AddToCartItem {

    protected Product product;
    protected Integer minQuantity;
    protected Integer maxQuantity;
    protected Money displayPrice;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        super.setProductId(product.getId());
        this.product = product;
    }

    public Integer getQuantity() {
        if (super.getQuantity() == null) {
            return minQuantity;
        }
        return super.getQuantity();
    }

    public Integer getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(Integer minQuantity) {
        this.minQuantity = minQuantity;
    }

    public Integer getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(Integer maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public Money getDisplayPrice() {
        return displayPrice;
    }

    public void setDisplayPrice(Money displayPrice) {
        this.displayPrice = displayPrice;
    }

    public Money getTotalPrice() {
        return getDisplayPrice().multiply(getQuantity());
    }
}
