/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
