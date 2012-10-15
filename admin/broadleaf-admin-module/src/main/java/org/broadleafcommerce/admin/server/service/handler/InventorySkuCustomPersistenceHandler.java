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

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.inventory.domain.FulfillmentLocation;
import org.broadleafcommerce.core.inventory.domain.FulfillmentLocationImpl;
import org.broadleafcommerce.core.inventory.service.InventoryService;
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventorySkuCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(InventorySkuCustomPersistenceHandler.class);

    private static final Integer MAX_RETRIES = 5;

    @Resource(name = "blInventoryService")
    protected InventoryService inventoryService;

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        String className = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        String[] customCriteria = persistencePackage.getCustomCriteria();
        return customCriteria != null && customCriteria.length > 0 && Sku.class.getName().equals(className) && "filteredSkuList".equals(customCriteria[0]);
    }

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        return canHandleInspect(persistencePackage);
    }

    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {

        try {

            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();

            //retrieve the default properties for the Sku
            Map<String, FieldMetadata> properties = helper.getSimpleMergedProperties(Sku.class.getName(), persistencePerspective);

            //create a new field to hold a list of all applicable product options for the sku
            BasicFieldMetadata fieldMetadata = new BasicFieldMetadata();
            fieldMetadata.setFieldType(SupportedFieldType.STRING);
            fieldMetadata.setMutable(true);
            fieldMetadata.setInheritedFromType(SkuImpl.class.getName());
            fieldMetadata.setAvailableToTypes(new String[]{SkuImpl.class.getName()});
            fieldMetadata.setForeignKeyCollection(false);
            fieldMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
            fieldMetadata.setName("productOptionList");
            fieldMetadata.setFriendlyName("Product Options");
            fieldMetadata.setGroup("");
            fieldMetadata.setOrder(3);
            fieldMetadata.setExplicitFieldType(SupportedFieldType.STRING);
            fieldMetadata.setProminent(true);
            fieldMetadata.setBroadleafEnumeration("");
            fieldMetadata.setReadOnly(false);
            fieldMetadata.setVisibility(VisibilityEnum.VISIBLE_ALL);

            properties.put("productOptionList", fieldMetadata);

            properties.get("id").setOrder(1);
            properties.get("name").setOrder(2);

            allMergedProperties.put(MergedPropertyType.PRIMARY, properties);
            Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Sku.class);
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
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {

        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();

        try {

            Long fulfillmentLocationId = Long.parseLong(persistencePackage.getCustomCriteria()[1]);
            FulfillmentLocation fulfillmentLocation = (FulfillmentLocation) dynamicEntityDao.retrieve(FulfillmentLocationImpl.class, fulfillmentLocationId);
            List<Sku> skus = inventoryService.readSkusNotAtFulfillmentLocation(fulfillmentLocation);

            // if any sku in this list does not have InventoryType.BASIC or if its parent category does not have
            // InventoryType.BASIC, remove it from the list.
            CollectionUtils.filter(skus, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    Sku sku = (Sku) object;
                    return InventoryType.BASIC.equals(sku.getInventoryType()) ||
                            (
                                sku.getProduct().getDefaultCategory() != null &&
                                InventoryType.BASIC.equals(sku.getProduct().getDefaultCategory().getInventoryType())
                            );
                }
            });

            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();

            //get the default properties from Sku hierarchy
            Map<String, FieldMetadata> originalProps = helper.getSimpleMergedProperties(Sku.class.getName(), persistencePerspective);

            //Convert Skus into the client-side Entity representation
            Entity[] payload = helper.getRecords(originalProps, skus);
            int totalRecords = skus.size();

            //now fill out the relevant properties for the product options for the Skus that were returned

            for (int i = 0; i < totalRecords; i++) {

                Sku sku = skus.get(i);
                Entity entity = payload[i];

                List<ProductOptionValue> optionValues = sku.getProductOptionValues();
                StringBuilder options = new StringBuilder("");

                for (ProductOptionValue value : optionValues) {
                    if (StringUtils.isNotBlank(options.toString())) {
                        options.append("; ");
                    }
                    options.append(value.getProductOption().getAttributeName()).append(": ").append(value.getAttributeValue());
                }

                Property optionProperty = new Property();
                optionProperty.setName("productOptionList");
                optionProperty.setValue(options.toString());
                entity.addProperty(optionProperty);

            }

            return new DynamicResultSet(payload, totalRecords);

        } catch (Exception e) {
            LOG.error("Unable to execute persistence activity", e);
            throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }

    }

}
