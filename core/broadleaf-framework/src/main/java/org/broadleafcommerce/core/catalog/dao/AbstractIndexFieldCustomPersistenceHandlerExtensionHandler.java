package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.search.domain.IndexField;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;

/**
 * @author Nick Crum ncrum
 */
public abstract class AbstractIndexFieldCustomPersistenceHandlerExtensionHandler extends AbstractExtensionHandler
        implements IndexFieldCustomPersistenceHandlerExtensionHandler {

    @Override
    public ExtensionResultStatusType addtoSearchableFields(PersistencePackage persistencePackage, IndexField searchField) throws ServiceException {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
}
