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
package org.broadleafcommerce.openadmin.server.service.persistence.module.extension;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.service.persistence.module.BasicPersistenceModule;

import java.io.Serializable;
import java.util.Map;

/**
 * For internal usage. Allows extending API calls without subclassing the entity.
 *
 * @author Jeff Fischer
 */
public interface BasicPersistenceModuleExtensionHandler extends ExtensionHandler {

    /**
     * Handle reorder change requests from the admin for sortable basic collections
     *
     * @param basicPersistenceModule the persistence module responsible for handling basic collection persistence
     *                               operations
     * @param persistencePackage     the data representing the change
     * @param instance               the persisted entity
     * @param mergedProperties       descriptive data about the entity structure
     * @param primaryKey             the primary key value for the persisted entity
     * @param resultHolder           container for any relevant operation results
     * @return the status of execution for this handler - informs the manager on how to proceed
     */
    ExtensionResultStatusType rebalanceForUpdate(BasicPersistenceModule basicPersistenceModule,
                                                 PersistencePackage persistencePackage, Serializable instance,
                                                 Map<String, FieldMetadata> mergedProperties, Object primaryKey,
                                                 ExtensionResultHolder<Serializable> resultHolder);

    /**
     * Handle additions of new members to a basic collection when the items are sortable
     *
     * @param basicPersistenceModule the persistence module responsible for handling basic collection persistence
     *                               operations
     * @param persistencePackage     the data representing the change
     * @param instance               the persisted entity
     * @param mergedProperties       descriptive data about the entity structure
     * @param resultHolder           container for any relevant operation results
     * @return the status of execution for this handler - informs the manager on how to proceed
     */
    ExtensionResultStatusType rebalanceForAdd(BasicPersistenceModule basicPersistenceModule,
                                              PersistencePackage persistencePackage, Serializable instance,
                                              Map<String, FieldMetadata> mergedProperties,
                                              ExtensionResultHolder<Serializable> resultHolder);

    public static final int DEFAULT_PRIORITY = Integer.MAX_VALUE;
}
