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
package org.broadleafcommerce.common.web.util;

import org.broadleafcommerce.common.web.filter.FilterOrdered;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Enable html minification. All unnecessary white space will be removed from html, inline CSS and inline JS. This should
 * serve to provide some reduction in byte size of the response. This is a suggested pagespeed optimization from Google.
 * </p>
 * The drawbacks of this filter include: a slight expense in processing the response payload, and the page response is
 * kept entirely in memory to facilitate processing before responding to the caller (usually the http response can be (is) pushed
 * back incrementally to avoid keeping the entire page contents in memory).
 * </p>
 * The filter is disabled by default. Activate by including this property declaration in your Spring environment property
 * file(s): {@code filter.html.minification.enabled=true}.
 *
 * @author Jeff Fischer
 */
@Component("blHtmlMinifyFilter")
@ConditionalOnProperty("filter.html.minification.enabled")
public class HtmlMinifyFilter extends OncePerRequestFilter implements Ordered {

    protected HtmlCompressor compressor;
    protected Pattern pattern = Pattern.compile("-?[0-9]*?");

    @Override
    public int getOrder() {
        //Should be after the CachingCompressedResponseFilter
        return FilterOrdered.PRE_SECURITY_HIGH - 1000;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String uri = httpServletRequest.getRequestURI();
        if ((!uri.contains(".") || uri.endsWith(".html")) && !isWidget(uri)) {
            CharResponseWrapper responseWrapper = new CharResponseWrapper(httpServletResponse);
            filterChain.doFilter(httpServletRequest, responseWrapper);
            String servletResponse = responseWrapper.toString();
            httpServletResponse.getWriter().write(compressor.compress(servletResponse));
        } else {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    protected boolean isWidget(String uri) {
        if (uri.contains("/") && !uri.contains(".")) {
            String fragment = uri.substring(uri.lastIndexOf("/") + 1, uri.length());
            return pattern.matcher(fragment).matches();
        }
        return false;
    }

    @Override
    public void initFilterBean() {
        compressor = new HtmlCompressor();
        compressor.setCompressCss(true);
        compressor.setCompressJavaScript(true);
    }

    public class CharResponseWrapper extends HttpServletResponseWrapper {

        private final CharArrayWriter output;

        @Override
        public String toString() {
            return output.toString();
        }

        public CharResponseWrapper(HttpServletResponse response) {
            super(response);
            output = new CharArrayWriter();
        }

        @Override
        public PrintWriter getWriter() {
            return new PrintWriter(output);
        }

    }
}
