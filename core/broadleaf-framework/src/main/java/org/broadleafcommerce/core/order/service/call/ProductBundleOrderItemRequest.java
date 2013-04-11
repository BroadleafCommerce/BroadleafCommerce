/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.order.service.call;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.order.domain.Order;

import java.util.HashMap;
import java.util.Map;

public class ProductBundleOrderItemRequest {

    protected String name;
    protected Category category;
    protected Sku sku;
    protected Order order;
    protected int quantity;
    protected ProductBundle productBundle;
    private Map<String,String> itemAttributes = new HashMap<String,String>();
    protected Money salePriceOverride;
    protected Money retailPriceOverride;

    public ProductBundleOrderItemRequest() {}
    
    public String getName() {
        return name;
    }

    public ProductBundleOrderItemRequest setName(String name) {
        this.name = name;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public ProductBundleOrderItemRequest setCategory(Category category) {
        this.category = category;
        return this;
    }
    
    public Sku getSku() {
        return sku;
    }

    public ProductBundleOrderItemRequest setSku(Sku sku) {
        this.sku = sku;
        return this;
    }
    
    public ProductBundleOrderItemRequest setOrder(Order order) {
        this.order = order;
        return this;
    }
    
    public Order getOrder() {
        return order;
    }

    public int getQuantity() {
        return quantity;
    }

    public ProductBundleOrderItemRequest setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public ProductBundle getProductBundle() {
        return productBundle;
    }

    public ProductBundleOrderItemRequest setProductBundle(ProductBundle productBundle) {
        this.productBundle = productBundle;
        return this;
    }

    public Map<String, String> getItemAttributes() {
        return itemAttributes;
    }

    public ProductBundleOrderItemRequest setItemAttributes(Map<String, String> itemAttributes) {
        this.itemAttributes = itemAttributes;
        return this;
    }

    public Money getSalePriceOverride() {
        return salePriceOverride;
    }

    public void setSalePriceOverride(Money salePriceOverride) {
        this.salePriceOverride = salePriceOverride;
    }

    public Money getRetailPriceOverride() {
        return retailPriceOverride;
    }

    public void setRetailPriceOverride(Money retailPriceOverride) {
        this.retailPriceOverride = retailPriceOverride;
    }

}
