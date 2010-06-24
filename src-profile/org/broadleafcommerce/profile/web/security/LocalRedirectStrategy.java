package org.broadleafcommerce.profile.web.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.web.RedirectStrategy;

public class LocalRedirectStrategy implements RedirectStrategy {

    private Logger logger = Logger.getLogger(this.getClass());

    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        String redirectUrl = calculateRedirectUrl(request.getContextPath(), url, request.getServerName());
        redirectUrl = response.encodeRedirectURL(redirectUrl);

        if (logger.isDebugEnabled()) {
            logger.debug("Redirecting to '" + redirectUrl + "'");
        }

        response.sendRedirect(redirectUrl);
    }

    private String calculateRedirectUrl(String contextPath, String url, String requestServerName) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return url;
        }

        String originalUrl = url;
        // Full URL, including http(s)://

        // Calculate the relative URL from the fully qualifed URL, minus the
        // protocol and base context.
        url = url.substring(url.indexOf("://") + 3); // strip off protocol
        if (originalUrl.startsWith("https://") && url.indexOf(':') > -1 && requestServerName.equals(url.substring(0, url.indexOf(':')))) {
            return originalUrl;
        } else {
            url = url.substring(url.indexOf(contextPath) + contextPath.length());
        }

        if (url.length() > 1 && url.charAt(0) == '/') {
            url = url.substring(1);
        }

        return url;
    }
}
