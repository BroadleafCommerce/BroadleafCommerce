package org.broadleafcommerce.profile.web.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.web.RedirectStrategy;

public class LocalRedirectStrategy implements RedirectStrategy {

    private Logger logger = Logger.getLogger(this.getClass());

    private boolean contextRelative;

    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        String redirectUrl = calculateRedirectUrl(request.getContextPath(), url, request.getServerName(), request.getServerPort());
        redirectUrl = response.encodeRedirectURL(redirectUrl);

        if (logger.isDebugEnabled()) {
            logger.debug("Redirecting to '" + redirectUrl + "'");
        }

        response.sendRedirect(redirectUrl);
    }

    private String calculateRedirectUrl(String contextPath, String url, String requestServerName, int requestServerPort) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            if (contextRelative) {
                return url;
            } else {
                return contextPath + url;
            }
        }

        if (!contextRelative) {
            return url;
        }

        String originalUrl = url;
        // Full URL, including http(s)://

        // Calculate the relative URL from the fully qualifed URL, minus the
        // protocol and base context.
        String channelServer = "https://" + requestServerName;
        if (originalUrl.startsWith(channelServer)) {
            String temp = originalUrl.substring(channelServer.length());
            if (temp.startsWith(":") || temp.startsWith("/") && temp.indexOf(contextPath) > -1) {
                return originalUrl;
            }
        }

        // Calculate the relative URL from the fully qualified URL, minus the
        // protocol and base context.
        channelServer = "://" + requestServerName;

        // strip off protocol and server
        url = url.substring(url.indexOf(channelServer) + channelServer.length());
        if (url.startsWith(":") && url.indexOf('/') > -1) {
            url = url.substring(url.indexOf('/'));
        }
        url = url.substring(url.indexOf(contextPath) + contextPath.length());

        if (url.length() > 1 && url.charAt(0) == '/') {
            url = url.substring(1);
        }

        return url;
    }

    /**
     * If <tt>true</tt>, causes any redirection URLs to be calculated minus the
     * protocol and context path (defaults to <tt>false</tt>).
     */
    public void setContextRelative(boolean useRelativeContext) {
        this.contextRelative = useRelativeContext;
    }
}
