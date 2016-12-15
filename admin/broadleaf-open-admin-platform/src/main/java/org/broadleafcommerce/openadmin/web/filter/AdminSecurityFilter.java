/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.security.handler.SecurityFilter;
import org.broadleafcommerce.common.security.service.StaleStateProtectionService;
import org.broadleafcommerce.common.security.service.StaleStateServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

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
 * This class also handles stale state detection for the admin. This can occur when an admin page form is submitted
 * and the system detects that key state has changed since the time the page was originally rendered.
 * See {@link StaleStateProtectionService} for details.
 * </p>
 * applicationContext-admin-security should reference this class as follows:
 * </p>
 * {@code
 *      ...
 *       <sec:custom-filter ref="blPreSecurityFilterChain" before="CHANNEL_FILTER"/>
 *        <sec:custom-filter ref="blSecurityFilter" before="FORM_LOGIN_FILTER"/>
 *        <sec:custom-filter ref="blAdminFilterSecurityInterceptor" after="EXCEPTION_TRANSLATION_FILTER"/>
 *        <sec:custom-filter ref="blPostSecurityFilterChain" after="SWITCH_USER_FILTER"/>
 *    </sec:http>
 *   <bean id="blSecurityFilter" class="org.broadleafcommerce.openadmin.web.filter.AdminSecurityFilter" />
 *   ...
 * }
 *
 *     
 * @author trevorleffert, Jeff Fischer
 */
@Component("blAdminCsrfFilter")
public class AdminSecurityFilter extends SecurityFilter {

    private static final Log LOG = LogFactory.getLog(AdminSecurityFilter.class);
    
    @Autowired(required = false)
    @Qualifier("blAdminAuthenticationFailureHandler")
    protected AuthenticationFailureHandler failureHandler;
    
    @Override
    public void doFilter(ServletRequest baseRequest, ServletResponse baseResponse, FilterChain chain) throws IOException, ServletException {
        try {
            super.doFilter(baseRequest, baseResponse, chain);
        } catch (ServletException e) {
            if (e.getCause() instanceof StaleStateServiceException) {
                LOG.debug("Stale state detected", e);
                ((HttpServletResponse) baseResponse).setStatus(HttpServletResponse.SC_CONFLICT);
                baseResponse.getWriter().write("Stale State Detected\n");
                baseResponse.getWriter().write(e.getMessage() + "\n");
            } else if (e.getCause() instanceof ServiceException) {
                HttpServletRequest baseHttpRequest = (HttpServletRequest) baseRequest;
                //if authentication is null and CSRF token is invalid, must be session time out
                if (SecurityContextHolder.getContext().getAuthentication() == null && failureHandler != null) {
                    baseHttpRequest.setAttribute("sessionTimeout", true);
                    failureHandler.onAuthenticationFailure((HttpServletRequest) baseRequest, (HttpServletResponse)
                            baseResponse, new SessionAuthenticationException("Session Time Out"));
                } else {
                    throw e;
                }
            } else {
                throw e;
            }
        }
    }
}
