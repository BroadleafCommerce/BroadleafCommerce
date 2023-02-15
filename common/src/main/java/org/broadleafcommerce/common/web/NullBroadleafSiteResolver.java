/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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

import org.broadleafcommerce.common.exception.SiteNotFoundException;
import org.broadleafcommerce.common.site.domain.Site;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * Returns null for the Site (typical for non-multi-site implementations of
 * Broadleaf Commerce.
 *
 * @author bpolster
 */
public class NullBroadleafSiteResolver implements BroadleafSiteResolver {

    @Override
    public Site resolveSite(HttpServletRequest request) {
        return resolveSite(new ServletWebRequest(request));
    }
    
    @Override
    public Site resolveSite(WebRequest request) {
        return resolveSite(request, false);
    }

    @Override
    public Site resolveSite(WebRequest request, boolean allowNullSite) throws SiteNotFoundException {
        return null;
    }
    
}
