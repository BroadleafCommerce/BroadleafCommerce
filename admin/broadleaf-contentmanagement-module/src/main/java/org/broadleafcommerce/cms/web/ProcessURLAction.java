/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.web;

/**
 * @deprecated in favor of Spring MVC mechanisms (@see PageHandlerMapping)
 * Enum that indicates the action to take when processing a URL.
 * <ul>
 *     <li>PAGE - indicates that the URL will be handled as a CMS managed page</li>
 *     <li>PRODUCT - indicates that the URL is an SEO manged product page</li>
 *     <li>CATEGORY - indicate that the URL is an SEO managed category URL</li>
 *     <li>PROCEED - indicates that the URL should be passed through and is not handled by BLC custom filters</li>
 *     <li>REDIRECT - indicates that the URL should be redirected to another URL</li>
 *     <li>UNKNOWN - indicates that it has not yet been determined how to process the URL</li>
 * </ul>
 *
 * Created by bpolster.
 */
public enum ProcessURLAction {
    PAGE,
    PRODUCT,
    CATEGORY,
    PROCEED,
    REDIRECT,
    UNKNOWN
}
