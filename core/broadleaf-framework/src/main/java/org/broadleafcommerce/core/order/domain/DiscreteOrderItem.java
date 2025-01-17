/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
     *
     * @return
     */
    BundleOrderItem getBundleOrderItem();

    /**
     * Sets the parent bundle item.
     * <p>
     * Setting to null removes this item from the bundle.
     *
     * @param bundleOrderItem
     */
    void setBundleOrderItem(BundleOrderItem bundleOrderItem);

    /**
     * If this item is part of a bundle that was created via a ProductBundle, then this
     * method returns a reference to the corresponding SkuBundleItem.
     * <p>
     * For manually created
     * <p>
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
     * @return the attributes
     * @deprecated use getOrderItemAttributes instead
     */
    Map<String, String> getAdditionalAttributes();

    /**
     * Arbitrary attributes associated with the order item
     *
     * @param additionalAttributes the map of attributes
     * @deprecated use setOrderItemAttributes instead
     */
    void setAdditionalAttributes(Map<String, String> additionalAttributes);

    Money getBaseRetailPrice();

    void setBaseRetailPrice(Money baseRetailPrice);

    Money getBaseSalePrice();

    void setBaseSalePrice(Money baseSalePrice);

    List<DiscreteOrderItemFeePrice> getDiscreteOrderItemFeePrices();

    void setDiscreteOrderItemFeePrices(List<DiscreteOrderItemFeePrice> orderItemFeePrices);

    /**
     * For items that are part of a bundle, this method will return the parent bundle item.  Otherwise,
     * returns null.
     *
     * @return
     */
    BundleOrderItem findParentItem();

    /**
     * Returns a boolean indicating whether this sku is active.  This is used to determine whether a user
     * the sku can add the sku to their cart.
     */
    boolean isSkuActive();

}
