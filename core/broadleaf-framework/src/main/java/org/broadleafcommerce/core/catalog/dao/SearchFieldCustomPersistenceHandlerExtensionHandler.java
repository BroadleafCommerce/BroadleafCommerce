package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.search.domain.SearchField;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;

/**
 * @author Chad Harchar (charchar)
 */
public interface SearchFieldCustomPersistenceHandlerExtensionHandler extends ExtensionHandler {

    ExtensionResultStatusType addtoSearchableFields(PersistencePackage persistencePackage, SearchField searchField) throws ServiceException;

}
