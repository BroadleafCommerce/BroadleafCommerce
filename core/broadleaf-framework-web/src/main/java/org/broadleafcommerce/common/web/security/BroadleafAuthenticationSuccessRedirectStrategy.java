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
package org.broadleafcommerce.common.web.security;

import org.broadleafcommerce.common.util.BLCRequestUtils;
import org.broadleafcommerce.common.web.controller.BroadleafControllerUtility;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * If the incoming request is an ajax request, the system will add the desired redirect path to the session and
 * then redirect to the path configured for the redirectPath property.
 *
 * It is assumed that the redirectPath will be picked up by the BroadleafRedirectController.
 *
 * @author bpolster
 *
 */
@Component("blAuthenticationSuccessRedirectStrategy")
public class BroadleafAuthenticationSuccessRedirectStrategy implements RedirectStrategy {

    private String redirectPath = "/redirect";
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        if (BroadleafControllerUtility.isAjaxRequest(request)) {
            if (BLCRequestUtils.isOKtoUseSession(new ServletWebRequest(request))) {
                request.getSession().setAttribute("BLC_REDIRECT_URL", url);
            }
            url = getRedirectPath();
        }
        getRedirectStrategy().sendRedirect(request, response, url);
    }

    public String updateLoginErrorUrlForAjax(String url) {
        String blcAjax = BroadleafControllerUtility.BLC_AJAX_PARAMETER;
        if (url != null && url.indexOf("?") > 0) {
            url = url + "&" + blcAjax + "=true";
        } else {
            url = url + "?" + blcAjax + "=true";
        }
        return url;
    }

    public String getRedirectPath() {
        return redirectPath;
    }

    public void setRedirectPath(String redirectPath) {
        this.redirectPath = redirectPath;
    }

    public RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }
}
