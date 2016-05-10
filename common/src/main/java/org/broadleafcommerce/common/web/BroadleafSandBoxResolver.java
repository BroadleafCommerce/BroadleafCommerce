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
package org.broadleafcommerce.common.web;

import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.site.domain.Site;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * Responsible for determining the SandBox to use for the current request. 
 * SandBox's are used to store a user's changes to products, content-items, etc. 
 * until they are ready to be pushed to production.  
 * 
 * If a request is being served with a SandBox parameter, it indicates that the user
 * wants to see the site as if their changes were applied.
 *
 * @author bpolster
 */
public interface BroadleafSandBoxResolver  {

    public static String SANDBOX_ID_VAR = "blSandboxId";

    /**
     * @deprecated use {@link #resolveSandBox(WebRequest, Site)} instead
     */
    @Deprecated
    public SandBox resolveSandBox(HttpServletRequest request, Site site);

    /**
     * Resolve the sandbox for the given site and request
     * 
     * @param request
     * @param site
     * @return the sandbox for the current request
     */
    public SandBox resolveSandBox(WebRequest request, Site site);

}
