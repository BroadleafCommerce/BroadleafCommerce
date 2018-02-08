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
package org.broadleafcommerce.core.web.cookie;

import org.broadleafcommerce.common.web.filter.AbstractIgnorableOncePerRequestFilter;
import org.broadleafcommerce.common.web.filter.FilterOrdered;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Populate configured cookie values on the http request thread for use by MVEL request-based rules
 */
public class CookieRuleFilter extends AbstractIgnorableOncePerRequestFilter {

    protected CookieRuleRequestProcessor processor;

    public CookieRuleFilter(CookieRuleRequestProcessor processor) {
        this.processor = processor;
    }

    @Override
    protected void doFilterInternalUnlessIgnored(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        ServletWebRequest request = new ServletWebRequest(httpServletRequest, httpServletResponse);
        try {
            processor.process(request);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            processor.postProcess(request);
        }
    }

    @Override
    public int getOrder() {
        return FilterOrdered.POST_SECURITY_LOW;
    }
}

