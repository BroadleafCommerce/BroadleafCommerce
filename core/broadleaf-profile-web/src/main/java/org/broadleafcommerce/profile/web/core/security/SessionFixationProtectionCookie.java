/*
 * Copyright 2012 the original author or authors.
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

package org.broadleafcommerce.profile.web.core.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie used to protected against session fixation attacks
 * 
 * @see SessionFixationProtectionFilter
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class SessionFixationProtectionCookie {
	protected final Log logger = LogFactory.getLog(getClass());

	public static final String COOKIE_NAME = "ActiveID";
	
	public static String readActiveID(HttpServletRequest request) {
		String cookieData = null;
		
		try {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals(COOKIE_NAME)) {
						cookieData = cookie.getValue();
						break;
					}
				}
			}
		} catch(Exception e) {
			// Do nothing -- we'll be returning null
		}
		
		return cookieData;
	}

	public static void writeActiveID(HttpServletRequest request, HttpServletResponse response, String data) {
		if (data != null) {
			Cookie cookie = new Cookie(COOKIE_NAME, data);
			cookie.setMaxAge(-1);
			cookie.setSecure(true);
			cookie.setPath("/");
			response.addCookie(cookie);
		}
	}

	public static void remove(HttpServletRequest request, HttpServletResponse response) {
		if (request != null && request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals(COOKIE_NAME)) {
					cookie.setMaxAge(0);
					cookie.setPath("/");
					cookie.setSecure(true);
					cookie.setValue("-1");
					response.addCookie(cookie);
				}
			}
		}
	}
	
	public static void forceRemove(HttpServletRequest request, HttpServletResponse response) {
		Cookie cookie = new Cookie(COOKIE_NAME, "");
		cookie.setMaxAge(0);
		cookie.setPath("/");
		cookie.setSecure(true);
		cookie.setValue("-1");
		response.addCookie(cookie);
	}

}
