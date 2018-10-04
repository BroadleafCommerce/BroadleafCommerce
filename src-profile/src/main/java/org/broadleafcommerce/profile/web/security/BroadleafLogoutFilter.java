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
package org.broadleafcommerce.profile.web.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * Allows the logout URL to be specified dynamically in the session by setting the
 * session value - broadleaf_commerce.logout_success_url to the destination URL.
 * @author bpolster
 */
public class BroadleafLogoutFilter extends LogoutFilter {

    public static final String BC_LOGOUT_SUCCESS_URL_KEY = "broadleaf_commerce.logout_success_url";

    public BroadleafLogoutFilter(String logoutSuccessUrl, LogoutHandler[] handlers) {
        super(logoutSuccessUrl, handlers);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        String sessionUrl = (String) request.getSession().getAttribute(BC_LOGOUT_SUCCESS_URL_KEY);
        if (sessionUrl != null) {
            removeLogoutSuccessUrl(request);
            return sessionUrl;
        }
        return null;
    }

    public void setLogoutSuccessUrl(HttpServletRequest request, String refererUrl) {
        request.getSession().setAttribute(BC_LOGOUT_SUCCESS_URL_KEY, refererUrl);
    }

    public void removeLogoutSuccessUrl(HttpServletRequest request) {
        request.getSession().removeAttribute(BC_LOGOUT_SUCCESS_URL_KEY);
    }

    public void setLogoutSuccessUrlWithReferer(HttpServletRequest request) {
        setLogoutSuccessUrl(request, request.getHeader("Referer"));
    }
}
