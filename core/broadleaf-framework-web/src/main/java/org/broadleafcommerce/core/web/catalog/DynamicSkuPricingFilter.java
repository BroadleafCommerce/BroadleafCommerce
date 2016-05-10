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
