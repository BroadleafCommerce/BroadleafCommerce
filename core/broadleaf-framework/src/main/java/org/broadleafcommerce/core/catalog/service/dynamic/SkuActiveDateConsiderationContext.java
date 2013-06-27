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
    
    private static final ThreadLocal<DynamicSkuActiveDatesService> skuActiveDatesService = new ThreadLocal<DynamicSkuActiveDatesService>();
    
    @SuppressWarnings("rawtypes")
    private static final ThreadLocal<HashMap> skuActiveDatesConsiderationContext = new ThreadLocal<HashMap>();
    
    @SuppressWarnings("rawtypes")
    public static HashMap getSkuActiveDatesConsiderationContext() {
        return SkuActiveDateConsiderationContext.skuActiveDatesConsiderationContext.get();
    }
    
    public static void setSkuActiveDatesConsiderationContext(@SuppressWarnings("rawtypes") HashMap skuActiveDatesConsiderationContext) {
        SkuActiveDateConsiderationContext.skuActiveDatesConsiderationContext.set(skuActiveDatesConsiderationContext);
    }
    
    public static DynamicSkuActiveDatesService getSkuActiveDatesService() {
        return SkuActiveDateConsiderationContext.skuActiveDatesService.get();
    }
    
    public static void setSkuActiveDatesService(DynamicSkuActiveDatesService skuActiveDatesService) {
        SkuActiveDateConsiderationContext.skuActiveDatesService.set(skuActiveDatesService);
    }
    
    public static boolean hasDynamicActiveDates() {
        return (getSkuActiveDatesService() != null);
    }
}
