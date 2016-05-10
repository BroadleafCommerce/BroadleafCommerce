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
package org.broadleafcommerce.common.web.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;


public class SessionlessHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public SessionlessHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }
    
    @Override
    public String getRequestedSessionId() {
        return null;
    }

    @Override
    public HttpSession getSession(boolean create) {
        if (!create) {
            return null;
        }
        throw new UnsupportedOperationException("You are in a sessionless environment and cannot get/create a HttpSession.");
    }

    @Override
    public HttpSession getSession() {
        throw new UnsupportedOperationException("You are in a sessionless environment and cannot get/create a HttpSession.");
    }

}
