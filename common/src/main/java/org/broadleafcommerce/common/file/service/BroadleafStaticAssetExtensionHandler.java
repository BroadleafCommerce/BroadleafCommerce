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
package org.broadleafcommerce.common.file.service;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.site.domain.Site;
import org.springframework.ui.Model;

/**
 * 
 * @author Chris Kittrell (ckittrell)
 */
public interface BroadleafStaticAssetExtensionHandler extends ExtensionHandler {

    public ExtensionResultStatusType removeShareOptionsForMTStandardSite(Model model, Site currentSite);

    /**
     * Provide an extension point to modify the url for a StaticAsset in the case
     * where multiple assets have the same url.
     * @param urlBuilder
     * @return
     */
    public ExtensionResultStatusType modifyDuplicateAssetURL(StringBuilder urlBuilder);

}
