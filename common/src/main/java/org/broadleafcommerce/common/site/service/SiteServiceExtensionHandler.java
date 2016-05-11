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
package org.broadleafcommerce.common.site.service;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.site.domain.SiteImpl;

/**
 * <p>
 * ExtensionHandler for methods within {@link SiteServiceImpl}
 * 
 * <p>
 * Rather than implementing this interface directly you should extend your implementation from
 * {@link AbstractSiteServiceExtensionHandler}.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link AbstractSiteServiceExtensionHandler}
 */
public interface SiteServiceExtensionHandler extends ExtensionHandler {

    /**
     * Invoked via {@link SiteServiceImpl#getNonPersistentSite(Site)} after the initial framework clone. If more properties
     * are dynamically weaved into {@link SiteImpl} then they should be cloned here.
     * 
     * @param from the {@link Site} being copied from, usually just looked up from the database
     * @param to the 
     * @see {@link SiteServiceImpl#getNonPersistentSite(Site)}
     */
    public ExtensionResultStatusType contributeNonPersitentSiteProperties(Site from, Site to);

}
