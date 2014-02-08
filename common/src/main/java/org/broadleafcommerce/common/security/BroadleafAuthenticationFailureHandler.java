/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.security;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.util.StringUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BroadleafAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private String defaultFailureUrl;

    public BroadleafAuthenticationFailureHandler() {
        super();
    }

    public BroadleafAuthenticationFailureHandler(String defaultFailureUrl) {
        super(defaultFailureUrl);
        this.defaultFailureUrl = defaultFailureUrl;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String failureUrlParam = StringUtil.cleanseUrlString(request.getParameter("failureUrl"));
        String successUrlParam = StringUtil.cleanseUrlString(request.getParameter("successUrl"));
        String failureUrl = StringUtils.trimToNull(failureUrlParam);

        // Verify that the url passed in is a servlet path and not a link redirecting away from the webapp.
        failureUrl = validateUrlParam(failureUrl);
        successUrlParam = validateUrlParam(successUrlParam);

        if (failureUrl == null) {
            failureUrl = StringUtils.trimToNull(defaultFailureUrl);
        }
        if (failureUrl != null) {
            if (StringUtils.isNotEmpty(successUrlParam)) {
                if (!failureUrl.contains("?")) {
                    failureUrl += "?successUrl=" + successUrlParam;
                } else {
                    failureUrl += "&successUrl=" + successUrlParam;
                }
            }
            saveException(request, exception);
            getRedirectStrategy().sendRedirect(request, response, failureUrl);
        } else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }

    public String validateUrlParam(String url) {
        if (url != null) {
            if (url.contains("http") || url.contains("www") || url.contains(".")) {
                return null;
            }
        }
        return url;
    }

}
