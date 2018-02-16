/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.filter;


import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.security.handler.CsrfFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class attempts the work flow of the CsrfFilter, but in the event of a Csrf token mismatch
 * (Session reset for example) the User will be redirected to login, if not session reset User is sent to previous location.
 *
 * The "blCsrfFilter' from applicationContext-admin-security should reference this class (org.broadleafcommerce.openadmin.web.filter.AdminCsrfFilter)
 * instead of the CsrfFilter
 *
 *     <bean id="blCsrfFilter" class="org.broadleafcommerce.openadmin.web.filter.AdminCsrfFilter" />
 *
 * @deprecated Use {@link AdminSecurityFilter} instead
 * @author trevorleffert
 */
@Deprecated
public class AdminCsrfFilter extends CsrfFilter {

    @Autowired
    @Qualifier("blAdminAuthenticationFailureHandler")
    protected AuthenticationFailureHandler failureHandler;

    @Override
    public void doFilter(ServletRequest baseRequest, ServletResponse baseResponse, FilterChain chain) throws IOException, ServletException {
        try {
            super.doFilter(baseRequest, baseResponse, chain);
        } catch (ServletException e) {
            if (e.getCause() instanceof ServiceException) {
                HttpServletRequest baseHttpRequest = (HttpServletRequest) baseRequest;
                //if authentication is null and CSRF token is invalid, must be session time out
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    baseHttpRequest.setAttribute("sessionTimeout", true);
                    failureHandler.onAuthenticationFailure((HttpServletRequest) baseRequest, (HttpServletResponse) baseResponse, new SessionAuthenticationException("Session Time Out"));
                } else {
                    throw e;
                }
            } else {
                throw e;
            }
        }
    }
}
