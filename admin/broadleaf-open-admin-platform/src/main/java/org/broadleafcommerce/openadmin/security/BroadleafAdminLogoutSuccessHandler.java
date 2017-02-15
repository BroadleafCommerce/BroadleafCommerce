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

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.util.StringUtil;
import org.broadleafcommerce.common.util.UrlUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 10/20/11
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
@Component("blAdminLogoutSuccessHandler")
public class BroadleafAdminLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + StringUtil.sanitize(targetUrl));
            return;
        }

        String queryString = request.getQueryString();
        if (!StringUtils.isEmpty(queryString)) {
            targetUrl += "?" + queryString;
        }

        request.getSession().invalidate();

        try {
            UrlUtil.validateUrl(targetUrl, request);
        } catch (IOException e) {
            logger.error("SECURITY FAILURE Bad redirect location: " + StringUtil.sanitize(targetUrl), e);
            response.sendError(403);
            return;
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}
