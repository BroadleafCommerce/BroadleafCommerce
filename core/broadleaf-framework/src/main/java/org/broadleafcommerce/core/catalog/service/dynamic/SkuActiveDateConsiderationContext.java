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
 * Convenient place to store the active date context and the related service on thread local. 
 * 
 * @author jfischer
 * @see {@link SkuImpl#getActiveStartDate()}
 * @see {@link SkuImpl#getActiveEndDate()}
 */
public class SkuActiveDateConsiderationContext {

    private static final ThreadLocal<SkuActiveDateConsiderationContext> skuActiveDatesConsiderationContext =
            ThreadLocalManager.createThreadLocal(SkuActiveDateConsiderationContext.class);

    public static HashMap getSkuActiveDateConsiderationContext() {
        return SkuActiveDateConsiderationContext.skuActiveDatesConsiderationContext.get().considerations;
    }

    public static void setSkuActiveDateConsiderationContext(HashMap skuPricingConsiderations) {
        SkuActiveDateConsiderationContext.skuActiveDatesConsiderationContext.get().considerations = skuPricingConsiderations;
    }

    public static DynamicSkuActiveDatesService getSkuActiveDatesService() {
        return SkuActiveDateConsiderationContext.skuActiveDatesConsiderationContext.get().service;
    }

    public static void setSkuActiveDatesService(DynamicSkuActiveDatesService skuPricingService) {
        SkuActiveDateConsiderationContext.skuActiveDatesConsiderationContext.get().service = skuPricingService;
    }

    public static boolean hasDynamicActiveDates() {
        return (getSkuActiveDatesService() != null);
    }

    protected DynamicSkuActiveDatesService service;
    protected HashMap considerations;

}
