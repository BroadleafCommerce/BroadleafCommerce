package org.broadleafcommerce.profile.web.security;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.web.RedirectStrategy;

/**
 * This class insures that if using the successUrl or failureUrl request
 * parameter, then the urls are valid and are local to the application
 * (preventing a user modifying to go somewhere else on login success/failure)
 */
public class LocalRedirectStrategy implements RedirectStrategy {

    private Logger logger = Logger.getLogger(this.getClass());
    private boolean enforcePortMatch = false;

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.security.web.RedirectStrategy#sendRedirect(javax.
     * servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
     * java.lang.String)
     */
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        if (!url.startsWith("/")) {
            if (StringUtils.equals(request.getParameter("successUrl"), url) || StringUtils.equals(request.getParameter("failureUrl"), url)) {
                validateRedirectUrl(request.getContextPath(), url, request.getServerName(), request.getServerPort());
            }
        }
        url = response.encodeRedirectURL(url);
        if (logger.isDebugEnabled()) {
            logger.debug("Redirecting to '" + url + "'");
        }

        response.sendRedirect(url);
    }

    /**
     * Insure the url is valid (must begin with http or https) and local to the
     * application
     * @param contextPath the application context path
     * @param url the url to validate
     * @param requestServerName the server name of the request
     * @param requestServerPort the port of the request
     * @throws MalformedURLException if the url is invalid
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
        logger.warn(errorMessage + ":  " + url);
        throw new MalformedURLException(errorMessage + ":  " + url);
    }

    /**
     * This forces the redirect url port to match the request port. This could
     * be problematic when switching between secure and non-secure (e.g.
     * http://localhost:8080 to https://localhost:8443)
     * @param enforcePortMatch
     */
    public void setEnforcePortMatch(boolean enforcePortMatch) {
        this.enforcePortMatch = enforcePortMatch;
    }
}
