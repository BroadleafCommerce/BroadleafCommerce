package org.broadleafcommerce.openadmin.server.service.extension;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;

/**
 * @author Jacob Mitash
 */
public interface CriteriaTransferObjectExtensionHandler extends ExtensionHandler {

    /**
     * Allows modification of the criteria transfer object for a fetch
     * @param request the persistence package request the {@code CriteriaTransferObject} was built from
     * @param cto the criteria transfer object to modify
     */
    ExtensionResultStatusType modifyFetchCriteriaTransferObject(PersistencePackageRequest request,
                                                                CriteriaTransferObject cto);
}
