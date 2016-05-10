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
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * Register this filter via Spring DelegatingFilterProxy, or register your own implementation
 * that provides additional, desirable members to the pricingConsiderations Map
 * that is generated from the getPricingConsiderations method.
 * 
 * @author jfischer
 *
 */
public class DefaultDynamicSkuPricingFilter extends AbstractDynamicSkuPricingFilter {
    
    @Resource(name="blDynamicSkuPricingService")
    protected DynamicSkuPricingService skuPricingService;
    
    @Resource(name="blCustomerState")
    protected CustomerState customerState;

    public DynamicSkuPricingService getDynamicSkuPricingService(ServletRequest request) {
        return skuPricingService;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public HashMap getPricingConsiderations(ServletRequest request) {
        HashMap pricingConsiderations = new HashMap();
        Customer customer = customerState.getCustomer((HttpServletRequest)  request);
        pricingConsiderations.put("customer", customer);
        
        return pricingConsiderations;
    }

}
