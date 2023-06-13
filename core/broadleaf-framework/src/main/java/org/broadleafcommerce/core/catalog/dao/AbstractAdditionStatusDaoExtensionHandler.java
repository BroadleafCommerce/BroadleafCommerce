package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

public class AbstractAdditionStatusDaoExtensionHandler extends AbstractExtensionHandler
        implements AdditionStatusDaoExtensionHandler {

    @Override
    public ExtensionResultStatusType cleanUpEntity(Object entity) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

}
