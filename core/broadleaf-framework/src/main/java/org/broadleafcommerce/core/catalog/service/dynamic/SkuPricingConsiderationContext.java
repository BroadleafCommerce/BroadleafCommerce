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

import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;

import java.util.HashMap;

/**
 * Convenient place to store the pricing considerations context and the pricing service on thread local. This class is
 * usually filled out by a {@link org.broadleafcommerce.core.web.catalog.DynamicSkuPricingFilter}. The default
 * implementation of this is {@link org.broadleafcommerce.core.web.catalog.DefaultDynamicSkuPricingFilter}.
 * 
 * @author jfischer
 * @see {@link SkuImpl#getRetailPrice}
 * @see {@link SkuImpl#getSalePrice}
 * @see {@link org.broadleafcommerce.core.web.catalog.DynamicSkuPricingFilter}
 */
public class SkuPricingConsiderationContext {

    private static final ThreadLocal<SkuPricingConsiderationContext> skuPricingConsiderationContext = ThreadLocalManager.createThreadLocal(SkuPricingConsiderationContext.class);

    public static HashMap getSkuPricingConsiderationContext() {
        return SkuPricingConsiderationContext.skuPricingConsiderationContext.get().considerations;
    }
    
    public static void setSkuPricingConsiderationContext(HashMap skuPricingConsiderations) {
        SkuPricingConsiderationContext.skuPricingConsiderationContext.get().considerations = skuPricingConsiderations;
    }

    public static DynamicSkuPricingService getSkuPricingService() {
        return SkuPricingConsiderationContext.skuPricingConsiderationContext.get().pricingService;
    }
    
    public static void setSkuPricingService(DynamicSkuPricingService skuPricingService) {
        SkuPricingConsiderationContext.skuPricingConsiderationContext.get().pricingService = skuPricingService;
    }
    
    public static boolean hasDynamicPricing() {
        return (
            getSkuPricingConsiderationContext() != null &&
            getSkuPricingConsiderationContext().size() >= 0 &&
            getSkuPricingService() != null
        );
    }

    protected DynamicSkuPricingService pricingService;
    protected HashMap considerations;
}
