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

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.core.inventory.domain.Inventory;
import org.broadleafcommerce.core.inventory.exception.ConcurrentInventoryModificationException;
import org.broadleafcommerce.core.inventory.service.FulfillmentLocationService;
import org.broadleafcommerce.core.inventory.service.InventoryService;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
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

    protected static final String QUANTITY_AVAILABLE_CHANGE_FIELD_NAME = "quantityAvailableChange";
    protected static final String QUANTITY_ON_HAND_CHANGE_FIELD_NAME = "quantityOnHandChange";

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        String className = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        String[] customCriteria = persistencePackage.getCustomCriteria();
        return customCriteria != null && customCriteria.length > 0 && Inventory.class.getName().equals(className) && "inventoryUpdate".equals(customCriteria[0]);
    }

    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        return canHandleUpdate(persistencePackage);
    }

    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {

        try {

            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();

            //retrieve the default properties for Inventory
            Map<String, FieldMetadata> properties = helper.getSimpleMergedProperties(Inventory.class.getName(), persistencePerspective);

            //create a new field to hold change in quantity available
            BasicFieldMetadata fieldMetadata = new BasicFieldMetadata();
            fieldMetadata.setFieldType(SupportedFieldType.INTEGER);
            fieldMetadata.setMutable(true);
            fieldMetadata.setInheritedFromType(Inventory.class.getName());
            fieldMetadata.setAvailableToTypes(new String[]{Inventory.class.getName()});
            fieldMetadata.setForeignKeyCollection(false);
            fieldMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
            fieldMetadata.setName(QUANTITY_AVAILABLE_CHANGE_FIELD_NAME);
            fieldMetadata.setFriendlyName("quantityAvailableChange");
            fieldMetadata.setGroup("Quantities");
            fieldMetadata.setOrder(3);
            fieldMetadata.setExplicitFieldType(SupportedFieldType.INTEGER);
            fieldMetadata.setProminent(false);
            fieldMetadata.setBroadleafEnumeration("");
            fieldMetadata.setReadOnly(false);
            fieldMetadata.setVisibility(VisibilityEnum.GRID_HIDDEN);
            fieldMetadata.setExcluded(false);

            properties.put(QUANTITY_AVAILABLE_CHANGE_FIELD_NAME, fieldMetadata);

            //create a new field to hold change in quantity available
            BasicFieldMetadata quantityOnHandChangeMetadata = new BasicFieldMetadata();
            quantityOnHandChangeMetadata.setFieldType(SupportedFieldType.INTEGER);
            quantityOnHandChangeMetadata.setMutable(true);
            quantityOnHandChangeMetadata.setInheritedFromType(Inventory.class.getName());
            quantityOnHandChangeMetadata.setAvailableToTypes(new String[]{Inventory.class.getName()});
            quantityOnHandChangeMetadata.setForeignKeyCollection(false);
            quantityOnHandChangeMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
            quantityOnHandChangeMetadata.setName(QUANTITY_ON_HAND_CHANGE_FIELD_NAME);
            quantityOnHandChangeMetadata.setFriendlyName("quantityOnHandChange");
            quantityOnHandChangeMetadata.setGroup("Quantities");
            quantityOnHandChangeMetadata.setOrder(4);
            quantityOnHandChangeMetadata.setExplicitFieldType(SupportedFieldType.INTEGER);
            quantityOnHandChangeMetadata.setProminent(false);
            quantityOnHandChangeMetadata.setBroadleafEnumeration("");
            quantityOnHandChangeMetadata.setReadOnly(false);
            quantityOnHandChangeMetadata.setVisibility(VisibilityEnum.GRID_HIDDEN);
            quantityOnHandChangeMetadata.setExcluded(false);


            properties.put(QUANTITY_ON_HAND_CHANGE_FIELD_NAME, quantityOnHandChangeMetadata);

            allMergedProperties.put(MergedPropertyType.PRIMARY, properties);
            Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Inventory.class);
            ClassMetadata mergedMetadata = helper.getMergedClassMetadata(entityClasses, allMergedProperties);

            return new DynamicResultSet(mergedMetadata, null, null);

        } catch (Exception e) {
            String className = persistencePackage.getCeilingEntityFullyQualifiedClassname();
            ServiceException ex = new ServiceException("Unable to retrieve inspection results for " + className, e);
            LOG.error("Unable to retrieve inspection results for " + className, ex);
            throw ex;
        }
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {

        Entity entity  = persistencePackage.getEntity();

        try {

            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Inventory.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            Inventory adminInstance = (Inventory) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
            adminInstance = (Inventory) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);

            Integer quantityAvailableChange = 0;
            Integer quantityAvailableOnHandChange = 0;

            Property[] properties = entity.getProperties();
            for (Property property : properties) {
                if (QUANTITY_AVAILABLE_CHANGE_FIELD_NAME.equals(property.getName())) {
                    quantityAvailableChange = NumberUtils.toInt(property.getValue());
                } else if (QUANTITY_ON_HAND_CHANGE_FIELD_NAME.equals(property.getName())) {
                    quantityAvailableOnHandChange = NumberUtils.toInt(property.getValue());
                }
            }

            adminInstance.setQuantityAvailable(adminInstance.getQuantityAvailable() + quantityAvailableChange);
            adminInstance.setQuantityOnHand(adminInstance.getQuantityOnHand() + quantityAvailableOnHandChange);

            if (adminInstance.getQuantityAvailable() < 0) {
                entity.setValidationFailure(true);
                entity.addValidationError(QUANTITY_AVAILABLE_CHANGE_FIELD_NAME, "quantityAvailableIsNegative");
                return entity;
            } else if (adminInstance.getQuantityOnHand() < 0) {
                entity.setValidationFailure(true);
                entity.addValidationError(QUANTITY_ON_HAND_CHANGE_FIELD_NAME, "quantityOnHandIsNegative");
                return entity;
            }

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
                    inventoryService.save(adminInstance);
                    break;
                } catch (ConcurrentInventoryModificationException ex) {
                    retryCount++;
                    if (retryCount == MAX_RETRIES) {
                        throw new ServiceException("Unable to update the inventory due to too many users" +
                                "concurrently updating this inventory. Please try again.");
                    }
                }

            }

            return helper.getRecord(adminProperties, adminInstance, null, null);

        } catch (Exception e) {
            LOG.error("Unable to update entity for " + entity.getType()[0], e);
            throw new ServiceException("Unable to update entity for " + entity.getType()[0], e);
        }

    }

}


