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
