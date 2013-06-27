/*
 * Copyright 2008-2013 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.web.catalog;

import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuPricingService;
import org.broadleafcommerce.core.catalog.service.dynamic.SkuPricingConsiderationContext;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * <p>Interceptor version of the {@link DynamicSkuPricingFilter}. If you are using Broadleaf in a Servlet web application
 * then you should instead be using the {@link DefaultDynamicSkuPricingFilter}.</p>
 * 
 * <p>This should be configured in your Spring context, but not the root one. So if you are running in a Portlet
 * environment, then you should configure the interceptor in each individual portlet's context.</p>
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link DynamicSkuPricingFilter}
 */
public abstract class DynamicSkuPricingInterceptor implements WebRequestInterceptor {

    @Resource(name = "blDynamicSkuPricingService")
    protected DynamicSkuPricingService skuPricingService;

    @Override
    public void preHandle(WebRequest request) throws Exception {
        SkuPricingConsiderationContext.setSkuPricingConsiderationContext(getPricingConsiderations(request));
        SkuPricingConsiderationContext.setSkuPricingService(getDynamicSkuPricingService(request));
    }

    public DynamicSkuPricingService getDynamicSkuPricingService(WebRequest request) {
        return skuPricingService;
    }

    /**
     * Override to supply your own considerations to pass to the {@link SkuPricingConsiderationContext}.
     * @param request
     * @return considerations that the {@link DynamicSkuPricingService} will evaluate when implementing custom pricing
     */
    @SuppressWarnings("rawtypes")
    public abstract HashMap getPricingConsiderations(WebRequest request);

    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception {
        // unimplemented
    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception {
        // unimplemented
    }

}
