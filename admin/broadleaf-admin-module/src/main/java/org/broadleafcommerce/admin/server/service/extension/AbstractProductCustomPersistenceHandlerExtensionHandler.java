/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.admin.server.service.extension;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;

import java.util.Map;

/**
 * Abstract Extension handler for {@link ProductCustomPersistenceHandlerExtensionHandler}
 *
 * @author Jon Fleschler (jfleschler
 */
public abstract class AbstractProductCustomPersistenceHandlerExtensionHandler extends AbstractExtensionHandler
    implements ProductCustomPersistenceHandlerExtensionHandler {

    @Override
    public ExtensionResultStatusType manageParentCategoryForAdd(PersistencePackage persistencePackage, Product product) throws ServiceException {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType manageParentCategoryForUpdate(PersistencePackage persistencePackage, Product product) throws ServiceException {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType manageRemove(PersistencePackage persistencePackage, Product product) throws ServiceException {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType manageInspect(Map<String, FieldMetadata> metadata) throws ServiceException {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType manageFields(PersistencePackage persistencePackage, Product product) throws ServiceException {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType initiateFetchState() throws ServiceException {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType endFetchState() throws ServiceException {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType manageAdditionalFilterMappings(CriteriaTransferObject cto) throws ServiceException {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
}
