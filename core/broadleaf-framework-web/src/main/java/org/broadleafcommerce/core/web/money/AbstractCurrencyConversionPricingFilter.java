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
package org.broadleafcommerce.core.web.money;

import org.broadleafcommerce.common.money.CurrencyConversionContext;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public abstract class AbstractCurrencyConversionPricingFilter implements CurrencyConversionPricingFilter {
    
    public void destroy() {
        //do nothing
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        CurrencyConversionContext.setCurrencyConversionContext(getCurrencyConversionContext(request));
        CurrencyConversionContext.setCurrencyConversionService(getCurrencyConversionService(request));
        try {
            filterChain.doFilter(request, response);
        } finally {
            CurrencyConversionContext.setCurrencyConversionContext(null);
            CurrencyConversionContext.setCurrencyConversionService(null);
        }
    }

    public void init(FilterConfig arg0) throws ServletException {
        //do nothing
    }
    
}
