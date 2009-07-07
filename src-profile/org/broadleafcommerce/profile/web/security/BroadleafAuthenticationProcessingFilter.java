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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.profile.web.MergeCartProcessor;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.CredentialsExpiredException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;
import org.springframework.security.util.TextUtils;

public class BroadleafAuthenticationProcessingFilter extends AuthenticationProcessingFilter {

    private final List<PostLoginObserver> postLoginListeners = new ArrayList<PostLoginObserver>();

    @Resource
    private MergeCartProcessor mergeCartProcessor;

    private String passwordChangeUri = "/passwordChange.htm";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request) throws AuthenticationException {
        Authentication auth = null;
        try {
            auth = super.attemptAuthentication(request);
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

            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password, new GrantedAuthority[] { new GrantedAuthorityImpl("ROLE_PASSWORD_CHANGE_REQUIRED") });

            // Place the last username attempted into HttpSession for views
            HttpSession session = request.getSession(false);

            if (session != null || getAllowSessionCreation()) {
                request.getSession().setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY, TextUtils.escapeEntities(username));
            }

            // Allow subclasses to set the "details" property
            setDetails(request, authRequest);
            auth = authRequest;
        }
        return auth;
    }

    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException {
        super.onSuccessfulAuthentication(request, response, authResult);
        if (mergeCartProcessor != null) {
            mergeCartProcessor.execute(request, response, authResult);
        }
        notifyPostLoginListeners(request, response, authResult);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if ("ROLE_PASSWORD_CHANGE_REQUIRED".equals(ga.getAuthority())) {
                return passwordChangeUri;
            }
        }
        String successUrlParam = request.getParameter("successUrl");
        if (StringUtils.isNotEmpty(successUrlParam)) {
            return successUrlParam;
        }
        return super.determineTargetUrl(request);
    }

    @Override
    protected String determineFailureUrl(HttpServletRequest request, AuthenticationException failed) {
        String failureUrlParam = request.getParameter("failureUrl");
        String successUrlParam = request.getParameter("successUrl");
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
    }

    public void addPostLoginListener(PostLoginObserver postLoginObserver) {
        this.postLoginListeners.add(postLoginObserver);
    }

    public void removePostLoginListener(PostLoginObserver postLoginObserver) {
        if (this.postLoginListeners.contains(postLoginObserver)) {
            this.postLoginListeners.remove(postLoginObserver);
        }
    }

    public void notifyPostLoginListeners(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        for (Iterator<PostLoginObserver> iter = postLoginListeners.iterator(); iter.hasNext();) {
            PostLoginObserver listener = iter.next();
            listener.processPostLogin(request, response, authResult);
        }
    }

    public void setPasswordChangeUri(String passwordChangeUri) {
        this.passwordChangeUri = passwordChangeUri;
    }
}
