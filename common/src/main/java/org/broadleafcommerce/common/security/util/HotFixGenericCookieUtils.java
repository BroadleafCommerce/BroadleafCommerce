package org.broadleafcommerce.common.security.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * BLC HOT FIX - add SameSite=None to secure cookies
 */
public class HotFixGenericCookieUtils extends GenericCookieUtilsImpl {
    private static final Log LOG = LogFactory.getLog(HotFixGenericCookieUtils.class);

    @Override
    public void setCookieValue(HttpServletResponse response, String cookieName,
        String cookieValue, String path, Integer maxAge, Boolean isSecure) {

            if (StringUtils.isBlank(cookieValue)){
                cookieValue = COOKIE_INVALIDATION_PLACEHOLDER_VALUE;
                maxAge = 0;
            }

            Cookie cookie = new Cookie(cookieName, cookieValue);
            cookie.setPath(path);
            if (maxAge != null){
                cookie.setMaxAge(maxAge);
            }

            if (shouldUseSecureCookieIfApplicable()){
                cookie.setSecure(isSecure);
            } else {
                cookie.setSecure(false);
                LOG.info("HTTP cookie set regardless of the value of isSecure.");
            }

            final StringBuffer sb = new StringBuffer();
            ServerCookie.appendCookieValue
                    (sb, cookie.getVersion(), cookie.getName(), cookie.getValue(),
                        cookie.getPath(), cookie.getDomain(), cookie.getComment(),
                        cookie.getMaxAge(), cookie.getSecure(), true);
            //if we reached here, no exception, cookie is valid
            // the header name is Set-Cookie for both "old" and v.1 ( RFC2109 )
            // RFC2965 is not supported by browsers and the Servlet spec
            // asks for 2109.

            /** HOTFIX added fragment **/
            if (isSecure){
                sb.append("; SameSite=None");
            }

        ESAPI.httpUtilities().addHeader(response, "Set-Cookie", sb.toString());
    }
}
