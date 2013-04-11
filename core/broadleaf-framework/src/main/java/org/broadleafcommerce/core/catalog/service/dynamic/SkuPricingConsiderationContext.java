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
    
    private static final ThreadLocal<DynamicSkuPricingService> skuPricingService = new ThreadLocal<DynamicSkuPricingService>();
    
    @SuppressWarnings("rawtypes")
    private static final ThreadLocal<HashMap> skuPricingConsiderationContext = new ThreadLocal<HashMap>();
    
    @SuppressWarnings("rawtypes")
    public static HashMap getSkuPricingConsiderationContext() {
        return SkuPricingConsiderationContext.skuPricingConsiderationContext.get();
    }
    
    public static void setSkuPricingConsiderationContext(@SuppressWarnings("rawtypes") HashMap skuPricingConsiderationContext) {
        SkuPricingConsiderationContext.skuPricingConsiderationContext.set(skuPricingConsiderationContext);
    }
    
    public static DynamicSkuPricingService getSkuPricingService() {
        return SkuPricingConsiderationContext.skuPricingService.get();
    }
    
    public static void setSkuPricingService(DynamicSkuPricingService skuPricingService) {
        SkuPricingConsiderationContext.skuPricingService.set(skuPricingService);
    }
    
    public static boolean hasDynamicPricing() {
        return (
                    getSkuPricingConsiderationContext() != null && 
                getSkuPricingConsiderationContext().size() >= 0 &&
                    getSkuPricingService() != null
                   ); 
    }
}
