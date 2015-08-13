package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.search.domain.SearchFacet;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;

/**
 * @author Chad Harchar (charchar)
 */
public interface SearchFacetCustomPersistenceHandlerExtensionHandler extends ExtensionHandler {

    ExtensionResultStatusType addtoSearchableFields(PersistencePackage persistencePackage, SearchFacet SearchFacet) throws ServiceException;

}
