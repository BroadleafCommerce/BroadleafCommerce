package org.broadleafcommerce.cms.page.service;

import org.broadleafcommerce.common.extension.ExtensionManager;
import org.broadleafcommerce.common.extension.SparselyPopulatedQueryExtensionHandler;
import org.springframework.stereotype.Service;

/**
 * Provide specialized cache keys for Pages.
 *
 * @see org.broadleafcommerce.common.extension.SparselyPopulatedQueryExtensionHandler*
 * @author Daniel Colgrove (dcolgrove)
 */
@Service("blPageQueryExtensionManager")
public class PageQueryExtensionManager extends ExtensionManager<SparselyPopulatedQueryExtensionHandler> {

    public PageQueryExtensionManager() {
        super(SparselyPopulatedQueryExtensionHandler.class);
    }

}
