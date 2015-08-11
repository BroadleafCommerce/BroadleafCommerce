package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.common.extension.ExtensionManager;
import org.springframework.stereotype.Service;

/**
 * @author Chad Harchar (charchar)
 */
@Service("blSearchFieldCustomPersistenceHandlerExtensionManager")
public class SearchFieldCustomPersistenceHandlerExtensionManager extends ExtensionManager<SearchFieldCustomPersistenceHandlerExtensionHandler> {

    public SearchFieldCustomPersistenceHandlerExtensionManager() {
        super(SearchFieldCustomPersistenceHandlerExtensionHandler.class);
    }

}
