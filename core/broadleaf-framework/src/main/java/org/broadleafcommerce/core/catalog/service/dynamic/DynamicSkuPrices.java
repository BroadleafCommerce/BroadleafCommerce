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
package org.broadleafcommerce.core.catalog.service.dynamic;

import org.broadleafcommerce.common.money.Money;

import java.io.Serializable;

/**
 * DTO to represent pricing overrides returned from invocations to {@link DynamicSkuPricingService}
 * @author jfischer
 * @see {@link DynamicSkuPricingService}
 */
public class DynamicSkuPrices implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Money retailPrice;
    protected Money salePrice;
    protected Money priceAdjustment;
    protected Boolean didOverride;

    public Money getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(Money retailPrice) {
        this.retailPrice = retailPrice;
    }

    public Money getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Money salePrice) {
        this.salePrice = salePrice;
    }

    public Money getPriceAdjustment() {
        return priceAdjustment;
    }

    public void setPriceAdjustment(Money priceAdjustment) {
        this.priceAdjustment = priceAdjustment;
    }

    /**
     * The out of box implementation returns {@link #getPrice()}.   Intended as a hook for
     * advanced pricing considerations like those in BLC Enterprise pricing.
     * 
     * @param quantity
     * @param currentPrice
     * @return
     */
    public Money getPriceForQuantity(long quantity) {
        return getPrice();
    }

    /**
     * Returns the lower of {@link #getSalePrice()} and {@link #getRetailPrice()}.  Intended as a hook for
     * advanced pricing considerations like those in BLC Enterprise pricing.
     * @return
     */
    public Money getPrice() {
        if (getSalePrice() == null) {
            return getRetailPrice();
        }
        if (getRetailPrice() != null) {
            if (getRetailPrice().lessThan(getSalePrice())) {
                return getRetailPrice();
            }
        }

        return getSalePrice();
    }

    public Boolean getDidOverride() {
        if (didOverride == null) {
            return false;
        }
        return didOverride;
    }

    public void setDidOverride(Boolean didOverride) {
        this.didOverride = didOverride;
    }
}
