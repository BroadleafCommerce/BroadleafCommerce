/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.catalog.service;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

/**
 * @author Jeff Fischer
 */
public interface CatalogServiceExtensionHandler extends ExtensionHandler {

    public ExtensionResultStatusType findCategoryByURI(String uri, ExtensionResultHolder resultHolder);

    public ExtensionResultStatusType findProductByURI(String uri, ExtensionResultHolder resultHolder);

    public ExtensionResultStatusType findSkuByURI(String uri, ExtensionResultHolder resultHolder);

    public ExtensionResultStatusType findProductByURI(CatalogContextDTO context, String uri, ExtensionResultHolder resultHolder);

    public ExtensionResultStatusType findCategoryByURI(CatalogContextDTO context, String uri, ExtensionResultHolder resultHolder);

    public ExtensionResultStatusType findSkuByURI(CatalogContextDTO context, String uri, ExtensionResultHolder resultHolder);
}
