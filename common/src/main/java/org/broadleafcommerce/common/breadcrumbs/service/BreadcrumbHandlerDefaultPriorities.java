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
package org.broadleafcommerce.common.breadcrumbs.service;


/**
 * Contains a list of static priority fields for commonly used Breadcrumb elements.
 * 
 * ***** NOTE *****
 * These handlers run in reverse priority order to allow for the later breadcrumbs to modify
 * the URL by removing relevant pieces.
 * 
 * Individual handlers reference these priorities but can be overridden by custom implementations.
 * 
 * @author bpolster
 */
public class BreadcrumbHandlerDefaultPriorities {

    public static final int HOME_CRUMB = -1000;
    public static final int CATEGORY_CRUMB = 2000;
    public static final int SEARCH_CRUMB = 3000;
    public static final int PRODUCT_CRUMB = 4000;
    public static final int PAGE_CRUMB = 5000;
}
