/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.order.security;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.util.BLCRequestUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Designed to be manually instantiated in client-specific security settings
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class BroadleafAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    protected static final String SESSION_ATTR = "SFP-ActiveID";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
        
        String targetUrl = request.getParameter(getTargetUrlParameter());
        if (BLCRequestUtils.isOKtoUseSession(new ServletWebRequest(request))) {
            request.getSession().removeAttribute(SESSION_ATTR);
        }
        if (StringUtils.isNotBlank(targetUrl) && targetUrl.contains(":")) {
            getRedirectStrategy().sendRedirect(request, response, getDefaultTargetUrl());
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
