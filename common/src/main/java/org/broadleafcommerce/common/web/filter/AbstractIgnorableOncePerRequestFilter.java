/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.BLCRequestUtils;
import org.springframework.core.Ordered;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides common logic for {@link OncePerRequestFilter} implementations to allow for ignoring behavior based on the presence (or absence)
 * of a request param. Sublclasses should implement {@link #doFilterInternalUnlessIgnored(HttpServletRequest, HttpServletResponse, FilterChain)}, knowing
 * that this method will faithfully be called unless the logic here determines the filter should pass the request through without
 * processing.
 *
 * @see SecurityBasedIgnoreFilter
 * @author Jeff Fischer
 */
public abstract class AbstractIgnorableOncePerRequestFilter extends OncePerRequestFilter implements Ordered {

    private static final Log LOG = LogFactory.getLog(AbstractIgnorableOncePerRequestFilter.class);

    @Override
    public void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if (isIgnored(httpServletRequest, httpServletResponse)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(String.format("%s filtering is disabled for %s", this.getClass().getName(), httpServletRequest.getRequestURI()));
            }
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace(String.format("%s filtering is enabled for %s", this.getClass().getName(), httpServletRequest.getRequestURI()));
            }
            doFilterInternalUnlessIgnored(httpServletRequest, httpServletResponse, filterChain);
        }
    }

    protected boolean isIgnored(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        boolean isUriSecurityIgnored = BLCRequestUtils.isFilteringIgnoredForUri(new ServletWebRequest(httpServletRequest, httpServletResponse));
        return isUriSecurityIgnored;
    }

    protected abstract void doFilterInternalUnlessIgnored(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException;

}
