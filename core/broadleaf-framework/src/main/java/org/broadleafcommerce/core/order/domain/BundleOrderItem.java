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
package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.Sku;

import java.util.List;


public interface BundleOrderItem extends OrderItem, OrderItemContainer, SkuAccessor {

    List<DiscreteOrderItem> getDiscreteOrderItems();

    void setDiscreteOrderItems(List<DiscreteOrderItem> discreteOrderItems);

    Money getTaxablePrice();
    
    public List<BundleOrderItemFeePrice> getBundleOrderItemFeePrices();

    public void setBundleOrderItemFeePrices(List<BundleOrderItemFeePrice> bundleOrderItemFeePrices);

    public boolean hasAdjustedItems();

    public Money getBaseRetailPrice();

    public void setBaseRetailPrice(Money baseRetailPrice);

    public Money getBaseSalePrice();

    public void setBaseSalePrice(Money baseSalePrice);

    /**
     * For BundleOrderItem created from a ProductBundle, this will represent the default sku of
     * the product bundle.
     *
     * This can be null for implementations that programatically create product bundles.
     *
     * @return
     */
    Sku getSku();

    void setSku(Sku sku);

    /**
     * Returns the associated ProductBundle or null if not applicable.
     *
     * If null, then this ProductBundle was manually created.
     *
     * @return
     */
    ProductBundle getProductBundle();

    /**
     * Sets the ProductBundle associated with this BundleOrderItem.
     *
     * @param bundle
     */
    void setProductBundle(ProductBundle bundle);

    /**
     * Same as getProductBundle.
     */
    Product getProduct();

    public boolean shouldSumItems();
}
