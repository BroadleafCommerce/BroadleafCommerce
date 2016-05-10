/*
 * #%L
 * BroadleafCommerce Framework Web
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
    public void afterCompletion(WebRequest request, Exception ex) throws Exception {
        // unimplemented
    }

}
