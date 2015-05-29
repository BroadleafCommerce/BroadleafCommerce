/*
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.server.service.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.catalog.domain.SkuBundleItem;
import org.broadleafcommerce.core.catalog.domain.SkuBundleItemImpl;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * Overridden to provide the option values field on the SkuBundleItem list
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blSkuBundleItemCustomPersistenceHandler")
public class SkuBundleItemCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(SkuBundleItemCustomPersistenceHandler.class);
    
    @Resource(name = "blSkuCustomPersistenceHandler")
    protected SkuCustomPersistenceHandler skuPersistenceHandler;
    
    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        return canHandle(persistencePackage);
    }
    
    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        return canHandle(persistencePackage);
    }
    
    protected Boolean canHandle(PersistencePackage persistencePackage) {
        String className = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            return SkuBundleItem.class.isAssignableFrom(Class.forName(className));
        } catch (ClassNotFoundException e) {
            LOG.warn("Could not find the class " + className + ", skipping the inventory custom persistence handler");
            return false;
        }
    }
    
    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();

            //retrieve the default properties for Inventory
            Map<String, FieldMetadata> properties = helper.getSimpleMergedProperties(SkuBundleItem.class.getName(), persistencePerspective);

            //add in the consolidated product options field
            FieldMetadata options = skuPersistenceHandler.createConsolidatedOptionField(SkuBundleItemImpl.class);
            options.setOrder(3);
            properties.put(SkuCustomPersistenceHandler.CONSOLIDATED_PRODUCT_OPTIONS_FIELD_NAME, options);
            
            allMergedProperties.put(MergedPropertyType.PRIMARY, properties);
            Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(SkuBundleItem.class);
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
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            //get the default properties from Inventory and its subclasses
            Map<String, FieldMetadata> originalProps = helper.getSimpleMergedProperties(SkuBundleItem.class.getName(), persistencePerspective);

            //Pull back the Inventory based on the criteria from the client
            List<FilterMapping> filterMappings = helper.getFilterMappings(persistencePerspective, cto, ceilingEntityFullyQualifiedClassname, originalProps);

            //attach the product option criteria
            skuPersistenceHandler.applyProductOptionValueCriteria(filterMappings, cto, persistencePackage, "sku");
            
            List<Serializable> records = helper.getPersistentRecords(persistencePackage.getCeilingEntityFullyQualifiedClassname(), filterMappings, cto.getFirstResult(), cto.getMaxResults());
            //Convert Skus into the client-side Entity representation
            Entity[] payload = helper.getRecords(originalProps, records);

            int totalRecords = helper.getTotalRecords(persistencePackage.getCeilingEntityFullyQualifiedClassname(), filterMappings);
            
            for (int i = 0; i < payload.length; i++) {
                Entity entity = payload[i];
                SkuBundleItem bundleItem = (SkuBundleItem) records.get(i);

                Property optionsProperty = skuPersistenceHandler.getConsolidatedOptionProperty(bundleItem.getSku().getProductOptionValuesCollection());
                entity.addProperty(optionsProperty);
            }

            return new DynamicResultSet(null, payload, totalRecords);
        } catch (Exception e) {
            throw new ServiceException("There was a problem fetching inventory. See server logs for more details", e);
        }
    }
    
}
