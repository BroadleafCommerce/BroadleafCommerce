/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.security.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.security.service.ExploitProtectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Checks the validity of the CSRF token on every POST request.
 * You can inject excluded Request URI patterns to bypass this filter.
 * This filter uses the AntPathRequestMatcher which compares a pre-defined ant-style pattern against the URL
 * ({@code servletPath + pathInfo}) of an {@code HttpServletRequest}.
 * This allows you to use wildcard matching as well, for example {@code /**} or {@code **}
 *
 * @see AntPathRequestMatcher
 * @deprecated Use {@link SecurityFilter} instead
 * @author Andre Azzolini (apazzolini)
 */
@Deprecated
public class CsrfFilter extends GenericFilterBean {
    protected static final Log LOG = LogFactory.getLog(CsrfFilter.class);

    @Autowired
    @Qualifier("blExploitProtectionService")
    protected ExploitProtectionService exploitProtectionService;

    protected List<String> excludedRequestPatterns;

    @Override
    public void doFilter(ServletRequest baseRequest, ServletResponse baseResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) baseRequest;
        HttpServletResponse response = (HttpServletResponse) baseResponse;

        boolean excludedRequestFound = false;
        if (excludedRequestPatterns != null && excludedRequestPatterns.size() > 0) {
            for (String pattern : excludedRequestPatterns) {
                RequestMatcher matcher = new AntPathRequestMatcher(pattern);
                if (matcher.matches(request)){
                    excludedRequestFound = true;
                    break;
                }
            }
        }

        // We only validate CSRF tokens on POST
        if (request.getMethod().equals("POST") && !excludedRequestFound) {
            String requestToken = request.getParameter(exploitProtectionService.getCsrfTokenParameter());
            try {
                exploitProtectionService.compareToken(requestToken);
            } catch (ServiceException e) {
                throw new ServletException(e);
            }
        }

        chain.doFilter(request, response);
    }

    public List<String> getExcludedRequestPatterns() {
        return excludedRequestPatterns;
    }

    /**
     * This allows you to declaratively set a list of excluded Request Patterns
     *
     * <bean id="blCsrfFilter" class="org.broadleafcommerce.common.security.handler.CsrfFilter" >
     *     <property name="excludedRequestPatterns">
     *         <list>
     *             <value>/exclude-me/**</value>
     *         </list>
     *     </property>
     * </bean>
     *
     **/
    public void setExcludedRequestPatterns(List<String> excludedRequestPatterns) {
        this.excludedRequestPatterns = excludedRequestPatterns;
    }
}
