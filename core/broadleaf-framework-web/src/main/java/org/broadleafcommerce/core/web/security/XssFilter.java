/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2020 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.security;

import org.broadleafcommerce.common.admin.condition.ConditionalOnNotAdmin;
import org.broadleafcommerce.common.web.filter.AbstractIgnorableOncePerRequestFilter;
import org.broadleafcommerce.common.web.filter.FilterOrdered;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@ConditionalOnNotAdmin
@Component("blXssFilter")
public class XssFilter extends AbstractIgnorableOncePerRequestFilter {

    @Autowired
    protected Environment environment;

    @Override
    public void destroy() {

    }

    private String[] whiteListUris;
    private String[] whiteListParamNames;

    @PostConstruct
    public void init(){
        String whiteList = environment.getProperty("blc.site.xssWrapper.whitelist.uri", "");
        String whiteListParams = environment.getProperty("blc.site.xssWrapper.whitelist.params", "");
        whiteListUris = whiteList.split(",");
        whiteListParamNames = whiteListParams.split(",");
    }

    @Override
    protected void doFilterInternalUnlessIgnored(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {

        //we can use esapi SecurityWrapperRequest but then we need to tweak patterns for header, parameter names, also add a number of HttpUtilities.xxx params to esapi.properties
        //oob it is not allowing abc[xxx]. so using custom one for now.
//        filterChain.doFilter(new SecurityWrapperRequest((HttpServletRequest) httpServletRequest), httpServletResponse);
        String enabled = environment.getProperty("blc.site.enable.xssWrapper", "false");
        if(Boolean.parseBoolean(enabled) && isValidUrl(httpServletRequest.getRequestURI())) {
            filterChain.doFilter(new XssRequestWrapper((HttpServletRequest) httpServletRequest, environment, whiteListParamNames), httpServletResponse);
        }else{
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    private boolean isValidUrl(String requestURI) {
        for (String uri : whiteListUris) {
            if(uri.equals(requestURI)){
                return false;
            }
        }
        return true;
    }

    @Override
    public int getOrder() {
        return FilterOrdered.POST_SECURITY_HIGH;
    }
}
