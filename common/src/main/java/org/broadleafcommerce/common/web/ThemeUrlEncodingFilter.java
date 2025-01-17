/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.broadleafcommerce.common.admin.condition.ConditionalOnNotAdmin;
import org.broadleafcommerce.common.site.domain.Theme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.net.URISyntaxException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * Filter that wraps the HttpServletResponse allowing the encodeURL method to be
 * overwritten in order to append a Theme reference to the URL.
 *
 * @author Stanislav Fedorov
 * @author dcolgrove
 */
@Component
@ConditionalOnNotAdmin
public class ThemeUrlEncodingFilter extends GenericFilterBean {

    @Autowired
    @Qualifier("blThemeResolver")
    protected BroadleafThemeResolver themeResolver;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            filterChain.doFilter(
                    httpRequest,
                    new ThemeUrlEncodingFilter.ResourceUrlEncodingResponseWrapper(httpResponse, themeResolver)
            );
        } else {
            throw new ServletException("ThemeUrlEncodingFilter just supports HTTP requests");
        }
    }

    protected static class ResourceUrlEncodingResponseWrapper extends HttpServletResponseWrapper {

        private final Log LOG = LogFactory.getLog(ResourceUrlEncodingResponseWrapper.class);

        private final BroadleafThemeResolver themeResolver;
        private final HttpServletResponse wrappedResponse;

        public ResourceUrlEncodingResponseWrapper(HttpServletResponse wrapped, BroadleafThemeResolver themeResolver) {
            super(wrapped);
            this.wrappedResponse = wrapped;
            this.themeResolver = themeResolver;
        }

        /**
         * Provide special encoding of .js and .css files - specifically providing the themeConfigId so that
         * subsequent requests can use the correct theme
         */
        @Override
        public String encodeURL(String url) {
            BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
            Object themeChanged = brc.getAdditionalProperties().get(BroadleafThemeResolver.BRC_THEME_CHANGE_STATUS);
            if (themeChanged != null && Boolean.TRUE.equals(themeChanged)) {
                if (url.contains(".js") || url.contains(".css")) {
                    //WebRequest request = brc.getWebRequest();
                    Theme theme = brc.getTheme();
                    try {
                        url = new URIBuilder(url).addParameter("themeConfigId",
                                theme.getId().toString()).build().toString();
                    } catch (URISyntaxException e) {
                        LOG.error(String.format("URI syntax error building %s with parameter %s and themeId %s",
                                url, "themeConfigId", theme.getId().toString()));
                    }
                }
            }
            return wrappedResponse.encodeURL(url);
        }
    }

}
