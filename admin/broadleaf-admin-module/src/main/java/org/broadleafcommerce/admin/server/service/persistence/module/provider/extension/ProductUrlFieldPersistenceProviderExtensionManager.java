/*
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.admin.server.service.persistence.module.provider.extension;

import org.broadleafcommerce.common.extension.ExtensionManager;
import org.springframework.stereotype.Service;

@Service("blProductUrlFieldPersistenceProviderExtensionManager")
public class ProductUrlFieldPersistenceProviderExtensionManager
        extends ExtensionManager<ProductUrlFieldPersistenceProviderExtensionHandler> {

    public ProductUrlFieldPersistenceProviderExtensionManager() {
        super(ProductUrlFieldPersistenceProviderExtensionHandler.class);
    }
}
