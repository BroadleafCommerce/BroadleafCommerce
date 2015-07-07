/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.web.catalog;

import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuPricingService;
import org.broadleafcommerce.core.catalog.service.dynamic.SkuPricingConsiderationContext;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import java.util.HashMap;

/**
 * Responsible for setting up the {@link SkuPricingConsiderationContext}. Rather than simply creating a filter that
 * implements this interface, consider instead subclassing the {@link DefaultDynamicSkuPricingFilter} and overriding the
 * appropriate methods.
 * 
 * @author jfischer
 * @see {@link DefaultDynamicSkuPricingFilter}
 * @see {@link AbstractDynamicSkuPricingFilter}
 * @see {@link DynamicSkuPricingService}
 * @see {@link SkuPricingConsiderationContext}
 */
public interface DynamicSkuPricingFilter extends Filter {

    /**
     * The result of this invocation should be set on
     * {@link SkuPricingConsiderationContext#setSkuPricingConsiderationContext(HashMap)} and ultimately passed to
     * {@link DynamicSkuPricingService} to determine prices.
     * 
     * @param request
     * @return a map of considerations to be used by the service in {@link #getDynamicSkuPricingService(ServletRequest)}.
     * @see {@link SkuPricingConsiderationContext#getSkuPricingConsiderationContext()}
     * @see {@link DynamicSkuPricingService}
     */
    @SuppressWarnings("rawtypes")
    public HashMap getPricingConsiderations(ServletRequest request);

    /**
     * The result of this invocation should be set on
     * {@link SkuPricingConsiderationContext#setSkuPricingService(DynamicSkuPricingService)}. This is the service that will
     * be used in calculating dynamic prices for a Sku or product option value
     * 
     * @param request
     * @return
     * @see {@link Sku#getRetailPrice()}
     * @see {@link Sku#getSalePrice()}
     */
    public DynamicSkuPricingService getDynamicSkuPricingService(ServletRequest request);
    
}
