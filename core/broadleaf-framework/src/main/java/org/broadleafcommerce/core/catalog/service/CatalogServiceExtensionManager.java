package org.broadleafcommerce.core.catalog.service;

import org.broadleafcommerce.common.extension.ExtensionManager;
import org.springframework.stereotype.Service;

/**
 * @author Jeff Fischer
 */
@Service("blCatalogServiceExtensionManager")
public class CatalogServiceExtensionManager extends ExtensionManager<CatalogServiceExtensionHandler> {

    public CatalogServiceExtensionManager() {
        super(CatalogServiceExtensionHandler.class);
    }

}
