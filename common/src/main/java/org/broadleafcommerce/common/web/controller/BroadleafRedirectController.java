/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.web.controller;

import org.broadleafcommerce.common.util.BLCRequestUtils;
import org.springframework.ui.Model;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This controller works in conjunction with the broadleaf-ajax style redirect.
 * 
 * The logic is quite complex but solves a problem related to redirects and
 * an Ajax form.
 * 
 * It is intended to solve a problem with using an Ajax style login modal 
 * along with Spring Security.
 * 
 * Spring Security wants to redirect after a successful login.   Unfortunately,
 * we can reliably redirect from Spring Security to a page within the BLC 
 * system when the login modal is presented in Ajax.
 * 
 * To solve this problem, Spring Security can be configured to use 
 * the BroadleafWindowLocationRedirectStrategy.   That strategy will add an attribute to 
 * session for the page you want to redirect to if the request is coming in
 * from an Ajax call.    It will then cause a redirect that should be picked 
 * up by this controller.   This controller will then render a page with the
 * blc-redirect-div.    The client-side javaScript (BLC.js) will intercept
 * this code and force the browser to load the new page (e.g. via window.location)
 * 
 * @see BroadleafRedirectStrategy
 * 
 * @author bpolster
 */
public class BroadleafRedirectController {
    
    public String redirect(HttpServletRequest request, HttpServletResponse response, Model model) {
        String path = null;
        if (BLCRequestUtils.isOKtoUseSession(new ServletWebRequest(request))) {
            path = (String) request.getSession().getAttribute("BLC_REDIRECT_URL");
        }

        if (path == null) {
            path = request.getContextPath();
        }
        return "ajaxredirect:" + path;
    }
}
