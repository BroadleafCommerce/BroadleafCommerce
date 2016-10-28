package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

/**
 * @author Nick Crum ncrum
 */
public abstract class AbstractSkuDaoExtensionHandler extends AbstractExtensionHandler
        implements SkuDaoExtensionHandler {

    @Override
    public ExtensionResultStatusType findSkuByURI(String uri, ExtensionResultHolder resultHolder) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
}
