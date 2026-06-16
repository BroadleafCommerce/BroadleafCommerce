/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
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
package org.broadleafcommerce.common.security;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.RedirectStrategy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This class insures that if using the successUrl or failureUrl request
 * parameter, then the urls are valid and are local to the application
 * (preventing a user modifying to go somewhere else on login success/failure)
 */
public class LocalRedirectStrategy implements RedirectStrategy {

    private static final Log LOG = LogFactory.getLog(LocalRedirectStrategy.class);
    private boolean contextRelative = false;
    private boolean enforcePortMatch = false;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.security.web.RedirectStrategy#sendRedirect(javax.
     * servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
     * java.lang.String)
     */
    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        if (isProtocolRelativeUrl(url)) {
            // A protocol-relative reference (e.g. "//evil.com" or "/\evil.com") begins with a slash,
            // so the legacy startsWith("/") check treated it as a safe local path. Browsers, however,
            // resolve it as an absolute reference to an external host, which allows the application's
            // redirect protection to be bypassed (open redirect). There is no legitimate local redirect
            // of this form, so reject it outright.
            String errorMessage = "Invalid redirect url specified.  Protocol-relative urls are not allowed";
            LOG.warn(errorMessage + ":  " + url);
            throw new MalformedURLException(errorMessage + ":  " + url);
        }
        if (!url.startsWith("/")) {
            if (StringUtils.equals(request.getParameter("successUrl"), url)
                    || StringUtils.equals(request.getParameter("failureUrl"), url)) {
                validateRedirectUrl(request.getContextPath(), url, request.getServerName(), request.getServerPort());
            }
        }
        String redirectUrl = calculateRedirectUrl(request.getContextPath(), url);
        redirectUrl = response.encodeRedirectURL(redirectUrl);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Redirecting to '" + url + "'");
        }

        response.sendRedirect(redirectUrl);
    }

    /**
     * Create the redirect url
     *
     * @param contextPath
     * @param url
     * @return
     */
    protected String calculateRedirectUrl(String contextPath, String url) {
        if ((!(url.startsWith("http://"))) && (!(url.startsWith("https://")))) {
            if (this.contextRelative) {
                return url;
            }
            return contextPath + url;
        }

        if (!(this.contextRelative)) {
            return url;
        }

        url = url.substring(url.indexOf("://") + 3);
        url = url.substring(url.indexOf(contextPath) + contextPath.length());

        if ((url.length() > 1) && (url.charAt(0) == '/')) {
            url = url.substring(1);
        }

        return url;
    }

    /**
     * Determine whether the supplied url is a protocol-relative (a.k.a. network-path) reference.
     *
     * <p>Such urls begin with two slashes ({@code //host}) or use a backslash variant
     * ({@code /\host}, {@code \/host}, {@code \\host}) that browsers normalize to {@code //host}.
     * They start with a slash and therefore pass a naive {@code startsWith("/")} "is local" check,
     * but the browser resolves them as absolute references to an external host. They must never be
     * treated as safe local redirects.</p>
     *
     * @param url the candidate redirect url
     * @return true if the url is a protocol-relative reference
     */
    protected boolean isProtocolRelativeUrl(String url) {
        if (url == null || url.length() < 2) {
            return false;
        }
        char first = url.charAt(0);
        char second = url.charAt(1);
        return (first == '/' || first == '\\') && (second == '/' || second == '\\');
    }

    /**
     * Insure the url is valid (must begin with http or https) and local to the
     * application
     *
     * @param contextPath       the application context path
     * @param url               the url to validate
     * @param requestServerName the server name of the request
     * @param requestServerPort the port of the request
     * @throws MalformedURLException if the url is invalid
     */
    protected void validateRedirectUrl(
            String contextPath,
            String url,
            String requestServerName,
            int requestServerPort
    ) throws MalformedURLException {
        URL urlObject = new URL(url);
        if (urlObject.getProtocol().equals("http") || urlObject.getProtocol().equals("https")) {
            if (StringUtils.equals(requestServerName, urlObject.getHost())) {
                if (!enforcePortMatch || requestServerPort == urlObject.getPort()) {
                    if (StringUtils.isEmpty(contextPath) || urlObject.getPath().startsWith("/" + contextPath)) {
                        return;
                    }
                }
            }
        }
        String errorMessage = "Invalid redirect url specified.  Must be of the form /<relative view> or http[s]://<server name>[:<server port>][/<context path>]/...";
        LOG.warn(errorMessage + ":  " + url);
        throw new MalformedURLException(errorMessage + ":  " + url);
    }

    /**
     * This forces the redirect url port to match the request port. This could
     * be problematic when switching between secure and non-secure (e.g.
     * http://localhost:8080 to https://localhost:8443)
     *
     * @param enforcePortMatch
     */
    public void setEnforcePortMatch(boolean enforcePortMatch) {
        this.enforcePortMatch = enforcePortMatch;
    }

    /**
     * Set whether or not the context should be included in the redirect path. If true, the context
     * is excluded from the generated path, otherwise it is included.
     *
     * @param contextRelative
     */
    public void setContextRelative(boolean contextRelative) {
        this.contextRelative = contextRelative;
    }

}
