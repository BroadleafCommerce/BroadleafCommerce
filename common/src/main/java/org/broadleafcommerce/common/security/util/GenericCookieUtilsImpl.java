/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.common.security.util;

import org.apache.commons.lang3.StringUtils;
import org.owasp.esapi.ESAPI;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component("blCookieUtils")
public class GenericCookieUtilsImpl implements CookieUtils {

    protected final String COOKIE_INVALIDATION_PLACEHOLDER_VALUE = "CookieInvalidationPlaceholderValue";

    /* (non-Javadoc)
     * @see org.broadleafcommerce.profile.web.CookieUtils#getCookieValue(javax.servlet.http.HttpServletRequest, java.lang.String)
     */
    public String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return (cookie.getValue());
                }
            }
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.profile.web.CookieUtils#setCookieValue(javax.servlet.http.HttpServletResponse, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer)
     *   Note, this method uses a cookieValue of "CookieInvalidationPlaceholderValue" simply because the later call to
     *  `ESAPI.httpUtilities().addHeader()` fails if the value is null or an empty String. If an empty cookieValue is passed, this is considered
     *  a request to remove the cookie and the maxAge is set to 0 to force the removal.  In addition, calls to `ESAPI.httpUtilities().killCookie()` have shown
     *  to be ineffective and this approach for removing cookies works.
     */
    public void setCookieValue(HttpServletResponse response, String cookieName, String cookieValue, String path, Integer maxAge, Boolean isSecure) {
        if (StringUtils.isBlank(cookieValue)) {
            cookieValue = COOKIE_INVALIDATION_PLACEHOLDER_VALUE;
            maxAge = 0;
        }

        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath(path);
        if (maxAge != null) {
            cookie.setMaxAge(maxAge);
        }
        cookie.setSecure(isSecure);

        final StringBuffer sb = new StringBuffer();
        ServerCookie.appendCookieValue
        (sb, cookie.getVersion(), cookie.getName(), cookie.getValue(),
                cookie.getPath(), cookie.getDomain(), cookie.getComment(),
                cookie.getMaxAge(), cookie.getSecure(), true);
        //if we reached here, no exception, cookie is valid
        // the header name is Set-Cookie for both "old" and v.1 ( RFC2109 )
        // RFC2965 is not supported by browsers and the Servlet spec
        // asks for 2109.
        ESAPI.httpUtilities().addHeader(response, "Set-Cookie", sb.toString());
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.profile.web.CookieUtils#setCookieValue(javax.servlet.http.HttpServletResponse, java.lang.String, java.lang.String)
     */
    public void setCookieValue(HttpServletResponse response, String cookieName, String cookieValue) {
        setCookieValue(response, cookieName, cookieValue, "/", null, false);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.profile.web.CookieUtils#invalidateCookie(javax.servlet.http.HttpServletResponse, java.lang.String)
     */
    public void invalidateCookie(HttpServletResponse response, String cookieName) {
        setCookieValue(response, cookieName, "", "/", 0, false);
    }

}
