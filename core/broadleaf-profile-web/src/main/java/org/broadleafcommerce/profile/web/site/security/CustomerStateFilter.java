/*
 * #%L
 * BroadleafCommerce Profile Web
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
package org.broadleafcommerce.profile.web.site.security;

import org.broadleafcommerce.common.util.BLCRequestUtils;
import org.broadleafcommerce.common.web.filter.AbstractIgnorableOncePerRequestFilter;
import org.broadleafcommerce.common.web.filter.FilterOrdered;
import org.broadleafcommerce.profile.web.core.security.CustomerStateRequestProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * This filter should be configured after the RememberMe listener from Spring Security.
 * Retrieves the Broadleaf Customer based using the authenticated user OR creates an Anonymous customer and stores them
 * in the session.  Calls Customer.setCookied(true) if the authentication token is an instance of
 * {@link org.springframework.security.providers.rememberme.RememberMeAuthenticationToken).   Calls Customer.setLoggedIn(true) if
 * the authentication token is an instance of {@link org.springframework.security.providers.UsernamePasswordAuthenticationToken}
 * </p>
 *
 * @author bpolster
 */
@Component("blCustomerStateFilter")
public class CustomerStateFilter extends AbstractIgnorableOncePerRequestFilter {
    
    @Autowired
    @Qualifier("blCustomerStateRequestProcessor")
    protected CustomerStateRequestProcessor customerStateProcessor;

    @Override
    public void doFilterInternalUnlessIgnored(HttpServletRequest baseRequest, HttpServletResponse baseResponse, FilterChain chain) throws IOException, ServletException {
        ServletWebRequest request = new ServletWebRequest(baseRequest, baseResponse);
        try {
            customerStateProcessor.process(request);
            chain.doFilter(baseRequest, baseResponse);
        } finally {
            customerStateProcessor.postProcess(request);
        }
    }

    @Override
    protected boolean isIgnored(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        boolean response = super.isIgnored(httpServletRequest, httpServletResponse);
        if (!response) {
            //ignore for stateless requests (i.e. rest api)
            response = !BLCRequestUtils.isOKtoUseSession(new ServletWebRequest(httpServletRequest));
        }
        return response;
    }

    @Override
    public int getOrder() {
        return FilterOrdered.POST_SECURITY_HIGH + 50;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

}
