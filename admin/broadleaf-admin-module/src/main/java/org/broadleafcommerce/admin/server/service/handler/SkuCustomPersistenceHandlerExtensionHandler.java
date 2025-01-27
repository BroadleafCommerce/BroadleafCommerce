/*-
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.admin.server.service.handler;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;

import java.util.List;

/**
 * Allows special behavior to be defined when a sku is updated via the admin.
 *
 * @author Jeff Fischer
 */
public interface SkuCustomPersistenceHandlerExtensionHandler extends ExtensionHandler {

    int DEFAULT_PRIORITY = Integer.MAX_VALUE;

    /**
     * Hook for the before and after save state of the sku
     *
     * @param updated
     * @return
     */
    ExtensionResultStatusType skuUpdated(Sku updated);

    ExtensionResultStatusType getAdditionalSkusCollection(Product product, ExtensionResultHolder<List<Sku>> erh);

}
