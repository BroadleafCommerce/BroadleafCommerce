/**
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.admin.server.service.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.inventory.domain.Inventory;
import org.broadleafcommerce.core.inventory.exception.ConcurrentInventoryModificationException;
import org.broadleafcommerce.core.inventory.service.FulfillmentLocationService;
import org.broadleafcommerce.core.inventory.service.InventoryService;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

public class InventoryCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(InventoryCustomPersistenceHandler.class);

    private static final Integer MAX_RETRIES = 5;

    @Resource(name = "blInventoryService")
    protected InventoryService inventoryService;

    @Resource(name = "blFulfillmentLocationService")
    protected FulfillmentLocationService fulfillmentLocationService;

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        String className = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        String[] customCriteria = persistencePackage.getCustomCriteria();
        return customCriteria != null && customCriteria.length > 0 && Inventory.class.getName().equals(className) && "inventoryList".equals(customCriteria[0]);
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {

        Entity entity  = persistencePackage.getEntity();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Inventory adminInstance = (Inventory) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Inventory.class.getName(), persistencePerspective);
            adminInstance = (Inventory) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);

            // There is a retry policy set in case of concurrent update exceptions where several
            // requests would try to update the inventory at the same time. The call to decrement inventory,
            // by default creates a new transaction because repeatable reads would occur if it were called
            // inside of the same transaction. Essentially, we want to try to transactionally decrement the
            // inventory, but if it fails due to locking, then we need to leave the transaction and re-read
            // the data to ensure repeatable reads don't prevent us from getting the freshest data. The
            // retry count is in place to handle higher concurrency situations where there may be more than one
            // failure.
            int retryCount = 0;

            while (retryCount < MAX_RETRIES) {
                try {
                    adminInstance = inventoryService.save(adminInstance);
                    break;
                } catch (ConcurrentInventoryModificationException ex) {
                    retryCount++;
                    if (retryCount == MAX_RETRIES) {
                        //maximum number of retries has been reached, bubble up exception
                        throw ex;
                    }
                }
            }

            //todo


            return helper.getRecord(adminProperties, adminInstance, null, null);

        } catch (Exception e) {
            LOG.error("Unable to update entity for " + entity.getType()[0], e);
            throw new ServiceException("Unable to update entity for " + entity.getType()[0], e);
        }
    }

}


