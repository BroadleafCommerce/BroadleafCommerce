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
package org.broadleafcommerce.common.security.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.config.service.SystemPropertiesService;
import org.owasp.esapi.ESAPI;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("blCookieUtils")
public class GenericCookieUtilsImpl implements CookieUtils {

    private static final Log LOG = LogFactory.getLog(GenericCookieUtilsImpl.class);
    protected final String COOKIE_INVALIDATION_PLACEHOLDER_VALUE = "CookieInvalidationPlaceholderValue";
    @Resource
    protected SystemPropertiesService systemPropertiesService;

    protected Pattern cookieValuePattern = Pattern.compile("^[a-zA-Z0-9()\\-=\\*\\.\\?;,+\\/:&_ \"]*$");

    @Override
    public Boolean shouldUseSecureCookieIfApplicable() {
        return systemPropertiesService.resolveBooleanSystemProperty("cookies.use.secure", false);
    }

    @Override
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

    @Override
    public void setCookieValue(
            HttpServletResponse response,
            String cookieName,
            String cookieValue,
            String path,
            Integer maxAge,
            Boolean isSecure
    ) {
        if (StringUtils.isBlank(cookieValue)) {
            cookieValue = COOKIE_INVALIDATION_PLACEHOLDER_VALUE;
            maxAge = 0;
        }

        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath(path);
        if (maxAge != null) {
            cookie.setMaxAge(maxAge);
        }

        if (shouldUseSecureCookieIfApplicable()) {
            cookie.setSecure(isSecure);
        } else {
            cookie.setSecure(false);
            LOG.info("HTTP cookie set regardless of the value of isSecure.");
        }

        final StringBuffer sb = new StringBuffer();
        ServerCookie.appendCookieValue(
                sb, cookie.getVersion(), cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getDomain(),
                cookie.getComment(), cookie.getMaxAge(), cookie.getSecure(), true
        );
        //if we reached here, no exception, cookie is valid
        // the header name is Set-Cookie for both "old" and v.1 ( RFC2109 )
        // RFC2965 is not supported by browsers and the Servlet spec
        // asks for 2109.

        if (isSecure) {
            sb.append("; SameSite=None");
        }

        String string = sb.toString();
        if (string.length() < 4096) {
            String canonicalize = ESAPI.encoder().canonicalize(string);
            if (cookieValuePattern.matcher(canonicalize).matches()) {
                response.addHeader("Set-Cookie", canonicalize);
            } else {
                LOG.warn("Attempt to set Cookie[" + cookieName + "]=" + string
                        + " . It doesn't match allowed pattern and this is considered security rules violation");
            }
        } else {
            LOG.warn("Attempt to set Cookie name:" + cookieName + " cookie length exceeds 4096");
        }
    }

    @Override
    public void setCookieValue(HttpServletResponse response, String cookieName, String cookieValue) {
        setCookieValue(response, cookieName, cookieValue, "/", null, false);
    }

    @Override
    public void invalidateCookie(HttpServletResponse response, String cookieName) {
        setCookieValue(response, cookieName, "", "/", 0, false);
    }

}
