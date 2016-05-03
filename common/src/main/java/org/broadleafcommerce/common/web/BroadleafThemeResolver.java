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

import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.domain.Theme;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * Responsible for returning the theme used by Broadleaf Commerce for the current request.
 * For a single site installation, this should return a theme whose path and name are empty string.
 *
 * @author bpolster
 */
public interface BroadleafThemeResolver {
    
    /**
     * 
     * @deprecated Use {@link #resolveTheme(WebRequest)} instead
     */
    @Deprecated
    public Theme resolveTheme(HttpServletRequest request, Site site);
    
    public Theme resolveTheme(WebRequest request);
}
