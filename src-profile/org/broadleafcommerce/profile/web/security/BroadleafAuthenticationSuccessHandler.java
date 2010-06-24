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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.profile.web.MergeCartProcessor;
import org.broadleafcommerce.util.StringUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public class BroadleafAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private String passwordChangeUri = "/passwordChange.htm";
    private final List<PostLoginObserver> postLoginListeners = new ArrayList<PostLoginObserver>();

    @Resource(name="blMergeCartProcessor")
    private MergeCartProcessor mergeCartProcessor;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        if (mergeCartProcessor != null) {
            mergeCartProcessor.execute(request, response, authentication);
        }
        notifyPostLoginListeners(request, response, authentication);
        super.onAuthenticationSuccess(request, response, authentication);
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

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
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
        return super.determineTargetUrl(request, response);
    }

    public void setPasswordChangeUri(String passwordChangeUri) {
        this.passwordChangeUri = passwordChangeUri;
    }
}
