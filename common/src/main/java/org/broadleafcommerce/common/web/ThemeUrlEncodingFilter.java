/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2019 Broadleaf Commerce
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
package org.broadleafcommerce.common.web;

import org.broadleafcommerce.common.admin.condition.ConditionalOnNotAdmin;
import org.broadleafcommerce.common.site.domain.Theme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

@Component
@ConditionalOnNotAdmin
public class ThemeUrlEncodingFilter extends ResourceUrlEncodingFilter {

    @Autowired
    @Qualifier("blThemeResolver")
    protected BroadleafThemeResolver themeResolver;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest)request;
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            filterChain.doFilter(httpRequest, new ThemeUrlEncodingFilter.ResourceUrlEncodingResponseWrapper(httpResponse, themeResolver));
        } else {
            throw new ServletException("ThemeUrlEncodingFilter just supports HTTP requests");
        }
    }

    private static class ResourceUrlEncodingResponseWrapper extends HttpServletResponseWrapper {

        private final BroadleafThemeResolver themeResolver;

        public ResourceUrlEncodingResponseWrapper(HttpServletResponse wrapped, BroadleafThemeResolver themeResolver) {
            super(wrapped);
            this.themeResolver = themeResolver;
        }

        @Override
        public String encodeURL(String url) {
            Theme theme = findTheme();
            if (url.contains(".js") || url.contains(".css")) {
                return url + "?themeConfigId=" + theme.getId();
            }
            return url;
        }
        
        protected Theme findTheme() {
            BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
            Theme theme = brc.getTheme();
            WebRequest request = brc.getWebRequest();
            String themeID = request.getParameter("themeConfigId");
            //If the passed themeConfigId is different than the theme set by the default themeResolver, then look it up
            if (themeID != null && !brc.getTheme().getId().toString().equals(themeID)) {
                theme = this.themeResolver.resolveTheme(request);
            }
            return theme;
        }
    }
}
