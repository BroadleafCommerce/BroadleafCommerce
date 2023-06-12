package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

public interface AdditionStatusDaoExtensionHandler extends ExtensionHandler {

    ExtensionResultStatusType cleanUpEntity(Object entity);

}
