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

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import java.util.ArrayList;
import java.util.List;

public class AddToCartItems {

    @SuppressWarnings("unchecked")

    //TOOD: this should probably be refactored to be called "rows" like in other model objects
    private List<ConfigurableOrderItemRequest> addToCartItems =   LazyList.decorate(
            new ArrayList<ConfigurableOrderItemRequest>(),
            FactoryUtils.instantiateFactory(ConfigurableOrderItemRequest.class));

    private long productId;
    private long categoryId;

    public void setProductId(long productId) {
        this.productId = productId;
        for(ConfigurableOrderItemRequest addToCartItem : addToCartItems) {
            addToCartItem.setProductId(productId);
        }
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
        for(ConfigurableOrderItemRequest addToCartItem : addToCartItems) {
            addToCartItem.setCategoryId(categoryId);
        }
    }

    public List<ConfigurableOrderItemRequest> getAddToCartItems() {
        return addToCartItems;
    }

    public void setAddToCartItem(List<ConfigurableOrderItemRequest> addToCartItems) {
        this.addToCartItems = addToCartItems;
    }

    public long getProductId() {
        return productId;
    }
    public long getCategoryId() {
        return categoryId;
    }

}
