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
package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.Sku;

import java.util.List;

/**
 * @deprecated instead, see the ProductType Module's Product Add-On's usage of {@link DiscreteOrderItem}s
 */
@Deprecated
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
