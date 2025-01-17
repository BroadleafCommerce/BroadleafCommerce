/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
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
