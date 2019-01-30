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
package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.money.Money;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Represents the {@link org.broadleafcommerce.core.catalog.domain.Sku} being sold in a bundle along with metadata
 * about the relationship itself like how many items should be included in the
 * bundle
 *
 * @author Phillip Verheyden
 * @see ProductBundle, Product
 *
 * @deprecated instead, use the ProductType Module's Product Add-Ons to build and configure bundles
 */
@Deprecated
public interface SkuBundleItem extends Serializable, MultiTenantCloneable<SkuBundleItem> {

    public Long getId();

    public void setId(Long id);

    public Integer getQuantity();

    public void setQuantity(Integer quantity);

    /**
    * Allows for overriding the related Product's sale price. This is only used
    * if the pricing model for the bundle is a composition of its parts
    * getProduct().getDefaultSku().getSalePrice()
    *
    * @param itemSalePrice The sale price for this bundle item
    */
    public void setSalePrice(Money salePrice);

    /**
    * @return this itemSalePrice if it is set,
    *         getProduct().getDefaultSku().getSalePrice() if this item's itemSalePrice is
    *         null
    */
    public Money getSalePrice();

    public ProductBundle getBundle();

    public void setBundle(ProductBundle bundle);

    public Money getRetailPrice();

    public Sku getSku();

    public void setSku(Sku sku);

    /**
     * Removes any currently stored dynamic pricing
     */
    public void clearDynamicPrices();

    /**
     * Get the sequence order.
     * @return
     */
    public BigDecimal getSequence() ;

    /**
     * Set the order the item shows up in the display.
     * @param sequence
     */
    public void setSequence(BigDecimal sequence);
}
