package org.broadleafcommerce.core.catalog.service;

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

/**
 * @author Nick Crum ncrum
 */
public abstract class AbstractCatalogServiceExtensionHandler extends AbstractExtensionHandler
        implements CatalogServiceExtensionHandler {

    @Override
    public ExtensionResultStatusType findCategoryByURI(String uri, ExtensionResultHolder resultHolder) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType findProductByURI(String uri, ExtensionResultHolder resultHolder) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType findSkuByURI(String uri, ExtensionResultHolder resultHolder) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
}
