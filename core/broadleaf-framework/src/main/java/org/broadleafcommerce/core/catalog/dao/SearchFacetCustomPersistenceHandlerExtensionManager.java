package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.common.extension.ExtensionManager;
import org.springframework.stereotype.Service;

/**
 * @author Chad Harchar (charchar)
 */
@Service("blSearchFacetCustomPersistenceHandlerExtensionManager")
public class SearchFacetCustomPersistenceHandlerExtensionManager extends ExtensionManager<SearchFacetCustomPersistenceHandlerExtensionHandler> {

    public SearchFacetCustomPersistenceHandlerExtensionManager() {
        super(SearchFacetCustomPersistenceHandlerExtensionHandler.class);
    }

}
