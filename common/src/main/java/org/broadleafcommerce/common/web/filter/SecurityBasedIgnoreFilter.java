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
package org.broadleafcommerce.common.web.filter;

import org.broadleafcommerce.common.util.BLCRequestUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
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
 * Sets a request attribute that informs all concerned Broadleaf filters that they should pass the request through and
 * ignore all normal operation. This implementation makes it's determination based on whether or not Spring Security
 * is configured to ignore the requested URI.
 *
 * @see AbstractIgnorableFilter
 * @see AbstractIgnorableOncePerRequestFilter
 * @author Jeff Fischer
 */
@Component("blSecurityBasedIgnoreFilter")
public class SecurityBasedIgnoreFilter extends GenericFilterBean implements Ordered {

    FilterChainProxy springSecurity = null;

    @EventListener(ContextRefreshedEvent.class)
    public void init(ContextRefreshedEvent event) {
        try {
            springSecurity = (FilterChainProxy) event.getApplicationContext().getBean("springSecurityFilterChain");
        } catch (BeansException e) {
            //do nothing
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (springSecurity != null) {
            List<SecurityFilterChain> securityChains = springSecurity.getFilterChains();
            for (SecurityFilterChain chain : securityChains) {
                if (chain.matches((HttpServletRequest) request) && chain.getFilters().isEmpty()) {
                    BLCRequestUtils.setIsFilteringIgnoredForUri(new ServletWebRequest((HttpServletRequest) request, (HttpServletResponse) response), Boolean.TRUE);
                    break;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public int getOrder() {
        return FilterOrdered.PRE_SECURITY_HIGH - 500;
    }
}
