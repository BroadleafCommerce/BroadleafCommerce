/*
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

/**
 * 
 */
package org.broadleafcommerce.admin.server.service.handler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.FieldPresentationAttributes;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;

/**
 * @author Phillip Verheyden
 *
 */
public class SkuCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(SkuCustomPersistenceHandler.class);
    
    public static String PRODUCT_OPTION_FIELD_PREFIX = "productOption";
    
    @Resource
    protected CatalogService catalogService;
    
    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        String[] customCriteria = persistencePackage.getCustomCriteria();
        return !ArrayUtils.isEmpty(customCriteria) && "productSkuList".equals(customCriteria[0]) && Sku.class.getName().equals(ceilingEntityFullyQualifiedClassname);
    }
    
    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        return canHandleInspect(persistencePackage);
    }
    
    /**
     * Build out the extra fields for the product options
     */
    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();
            
            //Grab the default properties for the Sku
            Map<String, FieldMetadata> properties = helper.getSimpleMergedProperties(Sku.class.getName(), persistencePerspective);
            
            //look up all the ProductOptions and then create new fields for each of them. Although
            //all of the options might not be relevant for the current Product (and thus the Skus as well) we
            //can hide the irrelevant fields in the fetch via a custom ClientEntityModule
            List<ProductOption> options = catalogService.readAllProductOptions();
            int order = 0;
            for (ProductOption option : options) {
                FieldMetadata metadata = new FieldMetadata();
                metadata.setFieldType(SupportedFieldType.EXPLICIT_ENUMERATION);
                metadata.setMutable(true);
                metadata.setInheritedFromType(SkuImpl.class.getName());
                metadata.setAvailableToTypes(new String[]{SkuImpl.class.getName()});
                metadata.setCollection(false);
                metadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
                
                //Set up the enumeration based on the product option values
                String[][] optionValues = new String[option.getAllowedValues().size()][2];
                for (int i = 0; i < option.getAllowedValues().size(); i++) {
                    ProductOptionValue value = option.getAllowedValues().get(i);
                    optionValues[i][0] = value.getId().toString();
                    optionValues[i][1] = value.getAttributeValue();
                }
                metadata.setEnumerationValues(optionValues);
                
                FieldPresentationAttributes attributes = new FieldPresentationAttributes();
                metadata.setPresentationAttributes(attributes);
                attributes.setName(PRODUCT_OPTION_FIELD_PREFIX + option.getId());
                attributes.setFriendlyName(option.getLabel());
                attributes.setGroup("");
                attributes.setOrder(order);
                attributes.setExplicitFieldType(SupportedFieldType.UNKNOWN);
                attributes.setProminent(true);
                attributes.setBroadleafEnumeration("");
                attributes.setReadOnly(false);
                attributes.setRequiredOverride(true);
                
                //add this to the built Sku properties
                properties.put("productOption" + option.getId(), metadata);
            }

            allMergedProperties.put(MergedPropertyType.PRIMARY, properties);
            Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Sku.class);
            ClassMetadata mergedMetadata = helper.getMergedClassMetadata(entityClasses, allMergedProperties);
            DynamicResultSet results = new DynamicResultSet(mergedMetadata, null, null);

            return results;
        } catch (Exception e) {
            ServiceException ex = new ServiceException("Unable to retrieve inspection results for " + persistencePackage.getCeilingEntityFullyQualifiedClassname(), e);
            LOG.error("Unable to retrieve inspection results for " + persistencePackage.getCeilingEntityFullyQualifiedClassname(), ex);
            throw ex;
        }
    }
    
    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            //get the default properties from Sku and its subclasses
            Map<String, FieldMetadata> originalProps = helper.getSimpleMergedProperties(Sku.class.getName(), persistencePerspective);
            
            //Pull back the Skus based on the criteria from the client
            BaseCtoConverter ctoConverter = helper.getCtoConverter(persistencePerspective, cto, ceilingEntityFullyQualifiedClassname, originalProps);
            PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, ceilingEntityFullyQualifiedClassname);
            List<Serializable> records = dynamicEntityDao.query(queryCriteria, Class.forName(persistencePackage.getCeilingEntityFullyQualifiedClassname()));
            
            //Convert Skus into the client-side Entity representation
            Entity[] payload = helper.getRecords(originalProps, records);
            int totalRecords = helper.getTotalRecords(ceilingEntityFullyQualifiedClassname, persistencePackage.getFetchTypeFullyQualifiedClassname(), cto, ctoConverter);
            
            //Now fill out the relevant properties for the product options for the Skus that were returned
            for (int i = 0; i < records.size(); i++) {
                Sku sku = (Sku)records.get(i);
                Entity entity = payload[i];
                
                List<ProductOptionValue> optionValues = sku.getProductOptionValues();
                for (ProductOptionValue value : optionValues) {
                    Property optionProperty = new Property();
                    optionProperty.setName(PRODUCT_OPTION_FIELD_PREFIX + value.getProductOption().getId());
                    optionProperty.setValue(value.getAttributeValue());
                    entity.addProperty(optionProperty);
                }
            }
            
            return new DynamicResultSet(null, payload, totalRecords);
        } catch (Exception e) {
            LOG.error("Unable to execute persistence activity", e);
            throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }
    
}
