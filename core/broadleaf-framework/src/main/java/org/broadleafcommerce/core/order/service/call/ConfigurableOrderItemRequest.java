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


import org.broadleafcommerce.core.catalog.domain.Product;

import java.util.ArrayList;
import java.util.List;

public class ConfigurableOrderItemRequest {

    protected Product product;

    protected Integer quantity;
    protected Integer minQuantity;
    protected Integer maxQuantity;

    protected List<ConfigurableOrderItemRequest> childOrderItems = new ArrayList<ConfigurableOrderItemRequest>();

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        if (quantity == null) {
            return minQuantity;
        }
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public List<ConfigurableOrderItemRequest> getChildOrderItems() {
        return childOrderItems;
    }

    public void setChildOrderItems(List<ConfigurableOrderItemRequest> childOrderItems) {
        this.childOrderItems = childOrderItems;
    }
}
