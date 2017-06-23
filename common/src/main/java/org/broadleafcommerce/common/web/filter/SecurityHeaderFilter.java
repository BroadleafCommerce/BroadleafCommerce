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

import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.BaseUrlResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This filter adds security response headers to help protect against MIME type confusion, Cross-Site-Scripting and Clickjacking attacks. 
 * 
 * @author Chad Harchar (charchar)
 */
@Component("blSecurityHeaderFilter")
public class SecurityHeaderFilter extends OncePerRequestFilter {
    
    @Resource(name = "blBaseUrlResolver")
    BaseUrlResolver baseUrlResolver;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-XSS-Protection", "1; mode=block");


        if (BLCSystemProperty.resolveBooleanSystemProperty("security.header.x.frame.options", false)) {
            response.setHeader("X-Frame-Options", "SAMEORIGIN");
        }
        
        if (BLCSystemProperty.resolveBooleanSystemProperty("security.header.content.security.policy", false)) {
            response.setHeader("Content-Security-Policy", "frame-ancestors " + baseUrlResolver.getAdminBaseUrl() + " " + baseUrlResolver.getSiteBaseUrl());
        }
        
        filterChain.doFilter(request, response);
        return;
    }
}
