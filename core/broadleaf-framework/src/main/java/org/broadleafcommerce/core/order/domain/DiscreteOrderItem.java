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

package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuBundleItem;

import java.util.List;
import java.util.Map;

public interface DiscreteOrderItem extends OrderItem, SkuAccessor, Cloneable {

    Sku getSku();

    void setSku(Sku sku);

    Product getProduct();

    void setProduct(Product product);

    /**
     * If this item is part of a bundle, this method will return the containing bundle item.
     * @return
     */
    BundleOrderItem getBundleOrderItem();

    /**
     * Sets the parent bundle item.
     *
     * Setting to null removes this item from the bundle.
     *
     * @param bundleOrderItem
     */
    void setBundleOrderItem(BundleOrderItem bundleOrderItem);

    /**
     * If this item is part of a bundle that was created via a ProductBundle, then this
     * method returns a reference to the corresponding SkuBundleItem.
     *
     * For manually created
     *
     * For all others, this method returns null.
     *
     * @return
     */
    SkuBundleItem getSkuBundleItem();

    /**
     * Sets the associated skuBundleItem.
     *
     * @param skuBundleItem
     */
    void setSkuBundleItem(SkuBundleItem skuBundleItem);

    Money getTaxablePrice();

    /**
     * Arbitrary attributes associated with the order item
     *
     * @deprecated use getOrderItemAttributes instead
     * @return the attributes
     */
    public Map<String, String> getAdditionalAttributes();

    /**
     * Arbitrary attributes associated with the order item
     *
     * @deprecated use setOrderItemAttributes instead
     * @param additionalAttributes the map of attributes
     */
    public void setAdditionalAttributes(Map<String, String> additionalAttributes);

    public Money getBaseRetailPrice();

    public void setBaseRetailPrice(Money baseRetailPrice);

    public Money getBaseSalePrice();

    public void setBaseSalePrice(Money baseSalePrice);
    
    public List<DiscreteOrderItemFeePrice> getDiscreteOrderItemFeePrices();

    public void setDiscreteOrderItemFeePrices(List<DiscreteOrderItemFeePrice> orderItemFeePrices);

    /**
     * For items that are part of a bundle, this method will return the parent bundle item.  Otherwise,
     * returns null.
     * 
     * @return
     */
    public BundleOrderItem findParentItem();

}
