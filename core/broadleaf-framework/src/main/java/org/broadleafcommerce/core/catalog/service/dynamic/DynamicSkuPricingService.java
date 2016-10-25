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
import org.broadleafcommerce.core.catalog.domain.ProductOptionValueImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuBundleItem;
import org.broadleafcommerce.core.catalog.domain.pricing.SkuPriceWrapper;

import javax.annotation.Nonnull;
import java.util.HashMap;

/**
 * <p>Interface for calculating dynamic pricing for a {@link Sku}. This should be hooked up via a custom subclass of 
 * {@link org.broadleafcommerce.core.web.catalog.DefaultDynamicSkuPricingFilter} where an implementation of this class
 * should be injected and returned in the getPricing() method.</p>
 * 
 * <p>Rather than implementing this interface directly, consider subclassing the {@link DefaultDynamicSkuPricingServiceImpl}
 * and providing overrides to methods there.</p>
 * 
 * @author jfischer
 * @see {@link DefaultDynamicSkuPricingServiceImpl}
 * @see {@link org.broadleafcommerce.core.web.catalog.DefaultDynamicSkuPricingFilter}
 * @see {@link SkuPricingConsiderationContext}
 */
public interface DynamicSkuPricingService {

    /**
     * While this method should return a {@link DynamicSkuPrices} (and not just null) the members of the result can all
     * be null; they do not have to be set
     * 
     * @param skuWrapper
     * @param skuPricingConsiderations
     * @return
     */
    @Nonnull
    @SuppressWarnings("rawtypes")
    public DynamicSkuPrices getSkuPrices(SkuPriceWrapper skuWrapper, HashMap skuPricingConsiderations);

    @Nonnull
    @SuppressWarnings("rawtypes")
    @Deprecated
    public DynamicSkuPrices getSkuPrices(Sku sku, HashMap skuPricingConsiderations);

    /**
     * Used for t
     * 
     * @param sku
     * @param skuPricingConsiderations
     * @return
     */
    @SuppressWarnings("rawtypes")
    public DynamicSkuPrices getSkuBundleItemPrice(SkuBundleItem sku, HashMap skuPricingConsiderations);

    /**
     * Execute dynamic pricing on the price of a product option value. 
     * @param productOptionValueImpl
     * @param priceAdjustment
     * @param skuPricingConsiderationContext
     * @return
     */
    @SuppressWarnings("rawtypes")
    public DynamicSkuPrices getPriceAdjustment(ProductOptionValueImpl productOptionValueImpl, Money priceAdjustment,
            HashMap skuPricingConsiderationContext);

}
