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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

	public final static String CUSTOMER_COOKIE_NAME = "customerId";

    public static String getCookieValue(HttpServletRequest request, String cookieName) {
    	Cookie[] cookies = request.getCookies();
    	if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if (cookieName.equals(cookie.getName()))
					return (cookie.getValue());
			}
    	}
		return null;
	}

    public static void setCookieValue(HttpServletResponse response, String cookieName, String cookieValue, String path, Integer maxAge) {
		Cookie cookie = new Cookie(cookieName, cookieValue);
		cookie.setPath(path);
		if (maxAge != null) {
			cookie.setMaxAge(maxAge);
		}
		response.addCookie(cookie);
    }

    public static void setCookieValue(HttpServletResponse response, String cookieName, String cookieValue) {
    	setCookieValue(response, cookieName, cookieValue, "/", null);
    }

    public static void invalidateCookie(HttpServletResponse response, String cookieName) {
    	setCookieValue(response, cookieName, "", "/", 0);
    }

}
