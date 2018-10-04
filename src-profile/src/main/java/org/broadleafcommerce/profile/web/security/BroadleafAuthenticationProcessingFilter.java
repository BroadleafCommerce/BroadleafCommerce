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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.TextEscapeUtils;

public class BroadleafAuthenticationProcessingFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Authentication auth = null;
        try {
            auth = super.attemptAuthentication(request, response);
        } catch (CredentialsExpiredException e) {
            String username = obtainUsername(request);
            String password = obtainPassword(request);

            if (username == null) {
                username = "";
            }

            if (password == null) {
                password = "";
            }

            username = username.trim();

            List<GrantedAuthority> grantedAuthorities =  new ArrayList<GrantedAuthority>();
            grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_PASSWORD_CHANGE_REQUIRED"));
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password, grantedAuthorities);

            // Place the last username attempted into HttpSession for views
            HttpSession session = request.getSession(false);

            if (session != null || getAllowSessionCreation()) {
                request.getSession().setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY, TextEscapeUtils.escapeEntities(username));
            }

            // Allow subclasses to set the "details" property
            setDetails(request, authRequest);
            auth = authRequest;
        }
        return auth;
    }

    /*@Override
    protected String determineTargetUrl(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if ("ROLE_PASSWORD_CHANGE_REQUIRED".equals(ga.getAuthority())) {
                return passwordChangeUri;
            }
        }
        String successUrlParam = StringUtil.cleanseUrlString(request.getParameter("successUrl"));
        if (StringUtils.isNotEmpty(successUrlParam)) {
            return successUrlParam;
        }
        return super.determineTargetUrl(request);
    }*/

    /*@Override
    protected String determineFailureUrl(HttpServletRequest request, AuthenticationException failed) {
        String failureUrlParam = StringUtil.cleanseUrlString(request.getParameter("failureUrl"));
        String successUrlParam = StringUtil.cleanseUrlString(request.getParameter("successUrl"));
        String failureUrl = null;
        if (StringUtils.isNotEmpty(failureUrlParam)) {
            failureUrl = failureUrlParam;
        } else {
            failureUrl = super.determineFailureUrl(request, failed);
        }
        if (StringUtils.isNotEmpty(successUrlParam)) {
            if (!failureUrl.contains("?")) {
                failureUrl += "?successUrl=" + successUrlParam;
            } else {
                failureUrl += "&successUrl=" + successUrlParam;
            }
        }
        return failureUrl;
    }*/

}
