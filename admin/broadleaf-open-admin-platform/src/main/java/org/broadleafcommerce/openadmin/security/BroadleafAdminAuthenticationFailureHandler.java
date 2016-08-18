/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.security;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.util.StringUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BroadleafAdminAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private String defaultFailureUrl;

    public BroadleafAdminAuthenticationFailureHandler() {
        super();
    }

    public BroadleafAdminAuthenticationFailureHandler(String defaultFailureUrl) {
        super(defaultFailureUrl);
        this.defaultFailureUrl = defaultFailureUrl;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String failureUrlParam = StringUtil.cleanseUrlString(request.getParameter("failureUrl"));
        String successUrlParam = StringUtil.cleanseUrlString(request.getParameter("successUrl"));
        String failureUrl = (failureUrlParam != null) ? failureUrlParam.trim() : null;
        Boolean sessionTimeout = (Boolean) request.getAttribute("sessionTimeout");

        if (StringUtils.isEmpty(failureUrl) && BooleanUtils.isNotTrue(sessionTimeout)) {
            failureUrl = defaultFailureUrl;
        }

        if (BooleanUtils.isTrue(sessionTimeout)) {
            failureUrl = "?sessionTimeout=true";
        }

        if (StringUtils.isEmpty(successUrlParam)) {
            //Grab url the user, was redirected from
            successUrlParam = request.getHeader("referer");
        }
        
        if (failureUrl != null) {
            if (!StringUtils.isEmpty(successUrlParam)) {
            	//Preserve the original successUrl from the referer.  If there is one, it must be the last url segment
            	int successUrlPos = successUrlParam.indexOf("successUrl");
            	if (successUrlPos >= 0) {
            		successUrlParam = successUrlParam.substring(successUrlPos);
            	} else {
            		successUrlParam = "successUrl=" + successUrlParam;
            	}

                if (!failureUrl.contains("?")) {
                    failureUrl += "?" + successUrlParam;
                } else {
                    failureUrl += "&" + successUrlParam;
                }
            }

            saveException(request, exception);
            getRedirectStrategy().sendRedirect(request, response, failureUrl);
        } else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }

}
