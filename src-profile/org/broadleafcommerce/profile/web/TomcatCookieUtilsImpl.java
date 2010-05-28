/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.profile.web;

import java.lang.reflect.Method;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TomcatCookieUtilsImpl extends GenericCookieUtilsImpl {

    private static final Log LOG = LogFactory.getLog(TomcatCookieUtilsImpl.class);

    @Override
    public void setCookieValue(HttpServletResponse response, String cookieName, String cookieValue, String path, Integer maxAge) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath(path);
        if (maxAge != null) {
            cookie.setMaxAge(maxAge);
        }
        /*
         * set the cookie to the Tomcat specific response and
         * include the httpOnly value. This protects the cookie
         * from possible XSS exploits, as browsers that support
         * the httpOnly cookie value will not allow JS access
         * to the cookie.
         */
        try {
            Method addCookie = response.getClass().getDeclaredMethod("addCookieInternal", new Class[]{Cookie.class, boolean.class});
            addCookie.invoke(response, new Object[]{cookie, true});
        } catch (Exception e) {
            LOG.warn("Unable to set cookie to the Tomcat response using the httpOnly parameter. Are you sure your version of Tomcat is >= 6.0.19? Setting the cookie without the httpOnly parameter");
            super.setCookieValue(response, cookieName, cookieValue, path, maxAge);
        }
    }

}
