/*
 * #%L
 * BroadleafCommerce Profile Web
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
package org.broadleafcommerce.profile.web.site.security;

import org.broadleafcommerce.profile.web.core.security.CustomerStateRequestProcessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

import javax.annotation.Resource;


/**
 * Interceptor responsible for putting the current customer on the current request. Note that this should always come after
 * the {@link PortletAuthenticationProcessingInterceptor} in order for this to work properly as this assumes that the
 * Spring {@link Authentication} object has already been set on Spring's {@link SecurityContext} (assuming that the user
 * is authenticated to begin with).
 * 
 * @author Phillip Verheyden
 * @see {@link CustomerStateRequestProcessor}
 * @see {@lnk CustomerState}
 */
public class CustomerStateInterceptor implements WebRequestInterceptor {

    @Resource(name = "blCustomerStateRequestProcessor")
    protected CustomerStateRequestProcessor customerStateProcessor;

    @Override
    public void preHandle(WebRequest request) throws Exception {
        customerStateProcessor.process(request);
    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception {
        // unimplemented
    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception {
        // unimplemented
    }

}
