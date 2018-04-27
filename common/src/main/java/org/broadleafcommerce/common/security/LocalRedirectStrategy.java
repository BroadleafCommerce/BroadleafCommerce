/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.security;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.RedirectStrategy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class insures that if using the successUrl or failureUrl request
 * parameter, then the urls are valid and are local to the application
 * (preventing a user modifying to go somewhere else on login success/failure)
 */
public class LocalRedirectStrategy implements RedirectStrategy {


    private boolean contextRelative = false;
    private static final Log LOG = LogFactory.getLog(LocalRedirectStrategy.class);
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
        if (!url.startsWith("/")) {
            if (StringUtils.equals(request.getParameter("successUrl"), url) || StringUtils.equals(request.getParameter("failureUrl"), url)) {
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
     * Insure the url is valid (must begin with http or https) and local to the
     * application
     *
     * @param contextPath
     *            the application context path
     * @param url
     *            the url to validate
     * @param requestServerName
     *            the server name of the request
     * @param requestServerPort
     *            the port of the request
     * @throws MalformedURLException
     *             if the url is invalid
     */
    private void validateRedirectUrl(String contextPath, String url, String requestServerName, int requestServerPort) throws MalformedURLException {
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