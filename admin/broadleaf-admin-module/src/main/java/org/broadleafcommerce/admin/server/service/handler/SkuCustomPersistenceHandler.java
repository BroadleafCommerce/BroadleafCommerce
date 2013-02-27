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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.client.FilterAndSortCriteria;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.Resource;

/**
 * @author Phillip Verheyden
 *
 */
public class SkuCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(SkuCustomPersistenceHandler.class);
    
    public static String PRODUCT_OPTION_FIELD_PREFIX = "productOption";
    
    @Resource(name="blCatalogService")
    protected CatalogService catalogService;
    
    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            //since this is the default for all Skus, it's possible that we are providing custom criteria for this
            //Sku lookup. In that case, we probably want to delegate to a child class, so only use this particular
            //persistence handler if there is no custom criteria being used
            Class testClass = Class.forName(ceilingEntityFullyQualifiedClassname);
            return Sku.class.isAssignableFrom(testClass) && ArrayUtils.isEmpty(persistencePackage.getCustomCriteria());
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        return canHandleInspect(persistencePackage);
    }
    
    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        return canHandleInspect(persistencePackage);
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
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
                BasicFieldMetadata metadata = new BasicFieldMetadata();
                metadata.setFieldType(SupportedFieldType.EXPLICIT_ENUMERATION);
                metadata.setMutable(true);
                metadata.setInheritedFromType(SkuImpl.class.getName());
                metadata.setAvailableToTypes(new String[]{SkuImpl.class.getName()});
                metadata.setForeignKeyCollection(false);
                metadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
                
                //Set up the enumeration based on the product option values
                String[][] optionValues = new String[option.getAllowedValues().size()][2];
                for (int i = 0; i < option.getAllowedValues().size(); i++) {
                    ProductOptionValue value = option.getAllowedValues().get(i);
                    optionValues[i][0] = value.getId().toString();
                    optionValues[i][1] = value.getAttributeValue();
                }
                metadata.setEnumerationValues(optionValues);

                metadata.setName(PRODUCT_OPTION_FIELD_PREFIX + option.getId());
                metadata.setFriendlyName(option.getLabel());
                metadata.setGroup("");
                metadata.setOrder(order);
                metadata.setExplicitFieldType(SupportedFieldType.UNKNOWN);
                metadata.setProminent(true);
                metadata.setVisibility(VisibilityEnum.NOT_SPECIFIED);
                metadata.setBroadleafEnumeration("");
                metadata.setReadOnly(false);
                metadata.setRequiredOverride(BooleanUtils.isFalse(option.getRequired()));
                
                //add this to the built Sku properties
                properties.put("productOption" + option.getId(), metadata);
            }

            allMergedProperties.put(MergedPropertyType.PRIMARY, properties);
            Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Sku.class);
            ClassMetadata mergedMetadata = helper.getMergedClassMetadata(entityClasses, allMergedProperties);
            DynamicResultSet results = new DynamicResultSet(mergedMetadata, null, null);

            return results;
        } catch (Exception e) {
            ServiceException ex = new ServiceException("Unable to retrieve inspection results for " + 
                                                           persistencePackage.getCeilingEntityFullyQualifiedClassname(), e);
            LOG.error("Unable to retrieve inspection results for " + 
                                                          persistencePackage.getCeilingEntityFullyQualifiedClassname(), ex);
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
            BaseCtoConverter ctoConverter = helper.getCtoConverter(persistencePerspective, cto, 
                                                                    ceilingEntityFullyQualifiedClassname, originalProps);
            PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, ceilingEntityFullyQualifiedClassname);
            //allow subclasses to provide additional criteria before executing the query
            applyAdditionalFetchCriteria(queryCriteria, cto, persistencePackage);

            List<Serializable> records = dynamicEntityDao.query(queryCriteria, 
                                               Class.forName(persistencePackage.getCeilingEntityFullyQualifiedClassname()));
            //records = filterListByProductOptionValues(cto, records);

            //allow subclasses to filter more
            records = filterFetchList(records, cto, persistencePackage);
            
            //Convert Skus into the client-side Entity representation
            Entity[] payload = helper.getRecords(originalProps, records);
            //int totalRecords = helper.getTotalRecords(persistencePackage, cto, ctoConverter);
            int totalRecords = records.size();
            
            //Communicate to the front-end to allow form editing for all of the product options available for the current
            //Product to allow inserting Skus one at a time
            ClassMetadata metadata = new ClassMetadata();
            if (cto.get("product").getFilterValues().length > 0) {
                Long productId = Long.parseLong(cto.get("product").getFilterValues()[0]);
                Product product = catalogService.findProductById(productId);
                List<Property> properties = new ArrayList<Property>();
                for (ProductOption option : product.getProductOptions()) {
                    Property optionProperty = new Property();
                    optionProperty.setName(PRODUCT_OPTION_FIELD_PREFIX + option.getId());
                    properties.add(optionProperty);
                }
                metadata.setProperties(properties.toArray(new Property[0]));
            }
            
            //Now fill out the relevant properties for the product options for the Skus that were returned
            for (int i = 0; i < records.size(); i++) {
                Sku sku = (Sku)records.get(i);
                Entity entity = payload[i];
                
                //In the list of Skus from the database, it is possible that some important properties (like name,
                //description, etc) are actually null. This isn't a problem on the site because the getters for Sku
                //use the defaultSku on this Sku's product if they are null. Nothing like this happens for displaying the
                //list of Skus in the admin, however, because everything is done via property reflection. Let's attempt to
                //actually call the getters (using bean utils) if the properties are null from this Sku, so that values
                //actually come back from the defaultSku (just like on the site)
                for (Property property : entity.getProperties()) {
                    if (StringUtils.isEmpty(property.getValue())) {
                        //only attempt the getter on the first-level Sku properties
                        String propertyName = property.getName();
                        if (propertyName.contains(".")) {
                            StringTokenizer tokens = new StringTokenizer(propertyName, ".");
                            propertyName = tokens.nextToken();
                        }
                        Object value = PropertyUtils.getProperty(sku, propertyName);

                        String strVal;
                        if (value == null) {
                            strVal = null;
                        } else {
                            if (Date.class.isAssignableFrom(value.getClass())) {
                                strVal = helper.getDateFormatter().format((Date) value);
                            } else if (Timestamp.class.isAssignableFrom(value.getClass())) {
                                strVal = helper.getDateFormatter().format(new Date(((Timestamp) value).getTime()));
                            } else if (Calendar.class.isAssignableFrom(value.getClass())) {
                                strVal = helper.getDateFormatter().format(((Calendar) value).getTime());
                            } else if (Double.class.isAssignableFrom(value.getClass())) {
                                strVal = helper.getDecimalFormatter().format(value);
                            } else if (BigDecimal.class.isAssignableFrom(value.getClass())) {
                                strVal = helper.getDecimalFormatter().format(((BigDecimal) value).doubleValue());
                            } else {
                                strVal = value.toString();
                            }
                        }
                        property.setValue(strVal);
                    }
                }

                List<ProductOptionValue> optionValues = sku.getProductOptionValues();
                for (ProductOptionValue value : optionValues) {
                    Property optionProperty = new Property();
                    optionProperty.setName(PRODUCT_OPTION_FIELD_PREFIX + value.getProductOption().getId());
                    optionProperty.setValue(value.getId().toString());
                    entity.addProperty(optionProperty);
                }
            }
            
            return new DynamicResultSet(metadata, payload, totalRecords);
        } catch (Exception e) {
            LOG.error("Unable to execute persistence activity", e);
            throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }

    //    protected List<Serializable> filterListByProductOptionValues(CriteriaTransferObject cto, List<Serializable> skus) {
    //
    //
    //        
    //        //now that we have the product option values
    //        if (productOptionValueFilterIDs.size() > 0) {
    //            List<Serializable> filteredSkus = new ArrayList<Serializable>();
    //            for (Serializable sku : skus) {
    //                Sku record = (Sku) sku;
    //                for (ProductOptionValue value : record.getProductOptionValues()) {
    //                    if (productOptionValueFilterIDs.contains(value.getId())) {
    //                        filteredSkus.add(sku);
    //                        break;
    //                    }
    //                }
    //            }
    //            return filteredSkus;
    //        }
    //        return skus;
    //    }

    /**
     * <p>Available override point for subclasses if they would like to add additional criteria via the queryCritiera. At the
     * point that this method has been called, criteria from the frontend has already been applied, thus allowing you to
     * override from there as well.</p>
     * <p>Subclasses that choose to override this should also call this super method so that correct filter criteria
     * can be applied for product option values</p>
     * 
     */
    public void applyAdditionalFetchCriteria(PersistentEntityCriteria queryCriteria, CriteriaTransferObject cto, PersistencePackage persistencePackage) {
        //TODO: this should be fully implemented and replace what is currently there for filtering the list of Skus by
        //product option values using the PersistentEntityCritiera rather than executing the query and then filtering the
        //resulting list. Potentially gives back incorrect results with pagination

        //        Set<String> filterPropertySet = cto.getPropertyIdSet();
        //
        //        final List<Long> productOptionValueFilterIDs = new ArrayList<Long>();
        //        for (String filterProperty : filterPropertySet) {
        //            if (filterProperty.startsWith(PRODUCT_OPTION_FIELD_PREFIX)) {
        //                FilterAndSortCriteria criteria = cto.get(filterProperty);
        //                productOptionValueFilterIDs.add(Long.parseLong(criteria.getFilterValues()[0]));
        //            }
        //        }
        //        if (productOptionValueFilterIDs.size() > 0) {
        //
        //            ((NestedPropertyCriteria) queryCriteria).add(
        //                    new FilterCriterion(new AssociationPath(new AssociationPathElement("productOptionValues")),
        //                            "id",
        //                            productOptionValueFilterIDs,
        //                            false,
        //                            new SimpleFilterCriterionProvider(FilterDataStrategy.DIRECT, productOptionValueFilterIDs.size()) {
        //                @Override
        //                public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
        //                                    return Restrictions.in(targetPropertyName, directValues);
        //                }
        //            }));
        //        }
    }

    /**
     * After applying the criteria and performing the fetch, this allows additional filtering from the list of Skus that was
     * returned. This is available for subclasses to implement if because you actually have Skus available from thist list,
     * but note that there could still be performance implications as at this point, the database has already been hit. For
     * maximum performance, consider using the {@link #applyAdditionalFetchCriteria(PersistentEntityCriteria, PersistencePackage)}
     * and applying the filtering there.
     * 
     * @param skus - the list of Skus returned from the database
     */
    public List<Serializable> filterFetchList(List<Serializable> skus, CriteriaTransferObject cto, PersistencePackage persistencePackage) {
        Set<String> filterPropertySet = cto.getPropertyIdSet();

        final List<Long> productOptionValueFilterIDs = new ArrayList<Long>();
        for (String filterProperty : filterPropertySet) {
            if (filterProperty.startsWith(PRODUCT_OPTION_FIELD_PREFIX)) {
                FilterAndSortCriteria criteria = cto.get(filterProperty);
                productOptionValueFilterIDs.add(Long.parseLong(criteria.getFilterValues()[0]));
            }
        }
        if (productOptionValueFilterIDs.size() > 0) {
            List<Serializable> result = new ArrayList<Serializable>();
            for (Serializable record : skus) {
                Sku sku = (Sku) record;
                //convert the option values into their Long representations
                ArrayList<Long> skuOptionValueIds = new ArrayList<Long>();
                CollectionUtils.collect(sku.getProductOptionValues(), new Transformer() {
                    @Override
                    public Object transform(Object input) {
                        return ((ProductOptionValue)input).getId();
                    }
                }, skuOptionValueIds);
                //grab the intersection of the filter option values
                if (CollectionUtils.containsAny(skuOptionValueIds, productOptionValueFilterIDs)) {
                    result.add(sku);
                }
            }
        }

        return skus;
    }
    
    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            //Fill out the Sku instance from the form
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Sku adminInstance = (Sku) Class.forName(entity.getType()[0]).newInstance();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Sku.class.getName(), persistencePerspective);
            adminInstance = (Sku) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);
            
            //Verify that there isn't already a Sku for this particular product option value combo
            Entity errorEntity = validateUniqueProductOptionValueCombination(adminInstance.getProduct(), 
                                                                                getProductOptionProperties(entity), null);
            if (errorEntity != null) {
                return errorEntity;
            }
            
            //persist the newly-created Sku
            adminInstance = (Sku)dynamicEntityDao.persist(adminInstance);
            
            //associate the product option values
            associateProductOptionValuesToSku(entity, adminInstance, dynamicEntityDao);
            
            //After associating the product option values, save off the Sku
            adminInstance = (Sku)dynamicEntityDao.merge(adminInstance);
            
            //Fill out the DTO and add in the product option value properties to it
            Entity result = helper.getRecord(adminProperties, adminInstance, null, null);
            for (Property property : getProductOptionProperties(entity)) {
                result.addProperty(property);
            }
            return result;
        } catch (Exception e) {
            LOG.error("Unable to execute persistence activity", e);
            throw new ServiceException("Unable to perform fetch for entity: " + Sku.class.getName(), e);
        }
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            //Fill out the Sku instance from the form
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Sku.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            Sku adminInstance = (Sku) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
            adminInstance = (Sku) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);
                        
            //Verify that there isn't already a Sku for this particular product option value combo
            Entity errorEntity = validateUniqueProductOptionValueCombination(adminInstance.getProduct(), 
                                                        getProductOptionProperties(entity), adminInstance);
            if (errorEntity != null) {
                return errorEntity;
            }
            
            associateProductOptionValuesToSku(entity, adminInstance, dynamicEntityDao);
            
            adminInstance = (Sku)dynamicEntityDao.merge(adminInstance);
            
            //Fill out the DTO and add in the product option value properties to it
            Entity result = helper.getRecord(adminProperties, adminInstance, null, null);
            for (Property property : getProductOptionProperties(entity)) {
                result.addProperty(property);
            }
            return result;
        } catch (Exception e) {
            LOG.error("Unable to execute persistence activity", e);
            throw new ServiceException("Unable to perform fetch for entity: " + Sku.class.getName(), e);
        }
    }

    /**
     * This initially removes all of the product option values that are currently related to the Sku and then re-associates
     * the {@link PrdouctOptionValue}s
     * @param entity
     * @param adminInstance
     */
    protected void associateProductOptionValuesToSku(Entity entity, Sku adminInstance, DynamicEntityDao dynamicEntityDao) {
        //Get the list of product option value ids that were selected from the form
        List<Long> productOptionValueIds = new ArrayList<Long>();
        for (Property property : getProductOptionProperties(entity)) {
            productOptionValueIds.add(Long.parseLong(property.getValue()));
        }
        
        //remove the current list of product option values from the Sku
        if (adminInstance.getProductOptionValues().size() > 0) {
            adminInstance.getProductOptionValues().clear();
            dynamicEntityDao.merge(adminInstance);
        }
        
        //Associate the product option values from the form with the Sku
        List<ProductOption> productOptions = adminInstance.getProduct().getProductOptions();
        for (ProductOption option : productOptions) {
            for (ProductOptionValue value : option.getAllowedValues()) {
                if (productOptionValueIds.contains(value.getId())) {
                    adminInstance.getProductOptionValues().add(value);
                }
            }
        }
    }
        
    protected List<Property> getProductOptionProperties(Entity entity) {
        List<Property> productOptionProperties = new ArrayList<Property>();
        for (Property property : entity.getProperties()) {
            if (property.getName().startsWith(PRODUCT_OPTION_FIELD_PREFIX)) {
                productOptionProperties.add(property);
            }
        }
        return productOptionProperties;
    }
    
    /**
     * Ensures that the given list of {@link ProductOptionValue} IDs is unique for the given {@link Product}
     * @param product
     * @param productOptionValueIds
     * @param currentSku - for update operations, this is the current Sku that is being updated; should be excluded from
     * attempting validation
     * @return <b>null</b> if successfully validation, the error entity otherwise
     */
    protected Entity validateUniqueProductOptionValueCombination(Product product, List<Property> productOptionProperties, Sku currentSku) {
        List<Long> productOptionValueIds = new ArrayList<Long>();
        for (Property property : productOptionProperties) {
            productOptionValueIds.add(Long.parseLong(property.getValue()));
        }

        boolean validated = true;
        for (Sku sku : product.getAdditionalSkus()) {
            if (currentSku == null || !sku.getId().equals(currentSku.getId())) {
                List<Long> testList = new ArrayList<Long>();
                for (ProductOptionValue optionValue : sku.getProductOptionValues()) {
                    testList.add(optionValue.getId());
                }
                
                if (productOptionValueIds.containsAll(testList) && productOptionValueIds.size() == testList.size()) {
                    validated = false;
                    break;
                }
            }
        }
        
        if (!validated) {
            Entity errorEntity = new Entity();
            errorEntity.setValidationFailure(true);
            for (Property productOptionProperty : productOptionProperties) {
                errorEntity.addValidationError(productOptionProperty.getName(), "uniqueSkuError");
            }
            return errorEntity;
        }
        return null;
    }
    
}
