/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.service.persistence.module;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.SecurityServiceException;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.common.value.ValueAssignable;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.ForeignKey;
import org.broadleafcommerce.openadmin.dto.MapStructure;
import org.broadleafcommerce.openadmin.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.dto.SimpleValueMapStructure;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.RequiredPropertyValidator;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * 
 * @author jfischer
 *
 */
@Component("blMapStructurePersistenceModule")
@Scope("prototype")
public class MapStructurePersistenceModule extends BasicPersistenceModule {

    @Resource(name="blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;

    private static final Log LOG = LogFactory.getLog(MapStructurePersistenceModule.class);
    
    @Override
    public boolean isCompatible(OperationType operationType) {
        return OperationType.MAP.equals(operationType);
    }
    
    @Override
    public void extractProperties(Class<?>[] inheritanceLine, Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties, List<Property> properties) throws NumberFormatException {
        if (mergedProperties.get(MergedPropertyType.MAPSTRUCTUREKEY) != null) {
            extractPropertiesFromMetadata(inheritanceLine, mergedProperties.get(MergedPropertyType.MAPSTRUCTUREKEY), properties, false, MergedPropertyType.MAPSTRUCTUREKEY);
        }
        if (mergedProperties.get(MergedPropertyType.MAPSTRUCTUREVALUE) != null) {
            extractPropertiesFromMetadata(inheritanceLine, mergedProperties.get(MergedPropertyType.MAPSTRUCTUREVALUE), properties, false, MergedPropertyType.MAPSTRUCTUREVALUE);
        }
    }

    @Override
    public void updateMergedProperties(PersistencePackage persistencePackage, Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {   
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            MapStructure mapStructure = (MapStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);
            if (mapStructure != null) {
                PersistentClass persistentClass = persistenceManager.getDynamicEntityDao().getPersistentClass(mapStructure.getKeyClassName());
                Map<String, FieldMetadata> keyMergedProperties;
                if (persistentClass == null) {
                    keyMergedProperties = persistenceManager.getDynamicEntityDao().getPropertiesForPrimitiveClass(
                        mapStructure.getKeyPropertyName(), 
                        mapStructure.getKeyPropertyFriendlyName(),
                        Class.forName(mapStructure.getKeyClassName()), 
                        Class.forName(ceilingEntityFullyQualifiedClassname), 
                        MergedPropertyType.MAPSTRUCTUREKEY
                    );
                } else {
                    keyMergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                        mapStructure.getKeyClassName(), 
                        new Class[]{Class.forName(mapStructure.getKeyClassName())},
                        null, 
                        new String[]{}, 
                        new ForeignKey[]{}, 
                        MergedPropertyType.MAPSTRUCTUREKEY,
                        persistencePerspective.getPopulateToOneFields(), 
                        persistencePerspective.getIncludeFields(), 
                        persistencePerspective.getExcludeFields(),
                        persistencePerspective.getConfigurationKey(),
                        ""
                    );
                }
                allMergedProperties.put(MergedPropertyType.MAPSTRUCTUREKEY, keyMergedProperties);
                
                persistentClass = persistenceManager.getDynamicEntityDao().getPersistentClass(mapStructure.getValueClassName());
                Map<String, FieldMetadata> valueMergedProperties;
                if (persistentClass == null) {
                    if (!SimpleValueMapStructure.class.isAssignableFrom(mapStructure.getClass())) {
                        throw new IllegalStateException("The map structure was determined to not be a simple value, but the system was unable to identify the entity designated for the map structure value(" + mapStructure.getValueClassName() + ")");
                    }
                    valueMergedProperties = persistenceManager.getDynamicEntityDao().getPropertiesForPrimitiveClass(
                        ((SimpleValueMapStructure) mapStructure).getValuePropertyName(), 
                        ((SimpleValueMapStructure) mapStructure).getValuePropertyFriendlyName(),
                        Class.forName(mapStructure.getValueClassName()), 
                        Class.forName(ceilingEntityFullyQualifiedClassname), 
                        MergedPropertyType.MAPSTRUCTUREVALUE
                    );
                } else {
                    valueMergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                        mapStructure.getValueClassName(), 
                        new Class[]{Class.forName(mapStructure.getValueClassName())},
                        null, 
                        new String[]{}, 
                        new ForeignKey[]{}, 
                        MergedPropertyType.MAPSTRUCTUREVALUE,
                        persistencePerspective.getPopulateToOneFields(), 
                        persistencePerspective.getIncludeFields(), 
                        persistencePerspective.getExcludeFields(),
                        persistencePerspective.getConfigurationKey(),
                        ""
                    );
                }
                allMergedProperties.put(MergedPropertyType.MAPSTRUCTUREVALUE, valueMergedProperties);
            }
        } catch (Exception e) {
            throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
        }
    }

    @Override
    public Entity add(PersistencePackage persistencePackage) throws ServiceException {
        String[] customCriteria = persistencePackage.getCustomCriteria();
        if (customCriteria != null && customCriteria.length > 0) {
            LOG.warn("custom persistence handlers and custom criteria not supported for add types other than BASIC");
        }
        PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
        Entity entity = persistencePackage.getEntity();
        MapStructure mapStructure = (MapStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);
        if (!mapStructure.getMutable()) {
            throw new SecurityServiceException("Field not mutable");
        }
        try {
            Map<String, FieldMetadata> ceilingMergedProperties = getSimpleMergedProperties(entity.getType()[0],
                    persistencePerspective);
            String mapKey = entity.findProperty(mapStructure.getKeyPropertyName()).getValue();
            if (StringUtils.isEmpty(mapKey)) {
                entity.addValidationError(mapStructure.getKeyPropertyName(), RequiredPropertyValidator.ERROR_MESSAGE);
                LOG.debug("No key property passed in for map, failing validation");
            }
            
            if (ceilingMergedProperties.containsKey(mapStructure.getMapProperty() + FieldManager.MAPFIELDSEPARATOR + mapKey)) {
                throw new ServiceException("\"" + mapKey + "\" is a reserved property name.");
            }

            Serializable instance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(entity.getType()
                    [0]), Long.valueOf(entity.findProperty("symbolicId").getValue()));

            Assert.isTrue(instance != null, "Entity not found");

            FieldManager fieldManager = getFieldManager();
            Map map = (Map) fieldManager.getFieldValue(instance, mapStructure.getMapProperty());
            
            if (map.containsKey(mapKey)) {
                entity.addValidationError(mapStructure.getKeyPropertyName(), "keyExistsValidationError");
            }

            if (StringUtils.isNotBlank(mapStructure.getMapKeyValueProperty())) {
                Property p = entity.findProperty("key");
                Property newP = new Property();
                newP.setName(mapStructure.getMapKeyValueProperty());
                newP.setValue(p.getValue());
                newP.setIsDirty(p.getIsDirty());
                entity.addProperty(newP);
            }
            
            PersistentClass persistentClass = persistenceManager.getDynamicEntityDao().getPersistentClass(mapStructure.getValueClassName());
            Map<String, FieldMetadata> valueUnfilteredMergedProperties;
            if (persistentClass == null) {
                valueUnfilteredMergedProperties = persistenceManager.getDynamicEntityDao().getPropertiesForPrimitiveClass(
                    ((SimpleValueMapStructure) mapStructure).getValuePropertyName(), 
                    ((SimpleValueMapStructure) mapStructure).getValuePropertyFriendlyName(),
                    Class.forName(mapStructure.getValueClassName()), 
                    Class.forName(entity.getType()[0]), 
                    MergedPropertyType.MAPSTRUCTUREVALUE
                );
            } else {
                valueUnfilteredMergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                    mapStructure.getValueClassName(), 
                    new Class[]{Class.forName(mapStructure.getValueClassName())},
                    null, 
                    new String[]{}, 
                    new ForeignKey[]{}, 
                    MergedPropertyType.MAPSTRUCTUREVALUE,
                    persistencePerspective.getPopulateToOneFields(), 
                    persistencePerspective.getIncludeFields(), 
                    persistencePerspective.getExcludeFields(),
                    persistencePerspective.getConfigurationKey(),
                    ""
                );
            }
            Map<String, FieldMetadata> valueMergedProperties = filterOutCollectionMetadata(valueUnfilteredMergedProperties);
            
            if (persistentClass != null) {
                Serializable valueInstance = (Serializable) Class.forName(mapStructure.getValueClassName()).newInstance();
                valueInstance = createPopulatedInstance(valueInstance, entity, valueMergedProperties, false);
                if (valueInstance instanceof ValueAssignable) {
                    //This is likely a OneToMany map (see productAttributes) whose map key is actually the name field from
                    //the mapped entity.
                    ((ValueAssignable) valueInstance).setName(entity.findProperty(mapStructure.getKeyPropertyName()).getValue());
                }
                if (mapStructure.getManyToField() != null) {
                    //Need to fulfill a bi-directional association back to the parent entity
                    fieldManager.setFieldValue(valueInstance, mapStructure.getManyToField(), instance);
                }
                valueInstance = persistenceManager.getDynamicEntityDao().persist(valueInstance);
                /*
                 * TODO this map manipulation code currently assumes the key value is a String. This should be widened to accept
                 * additional types of primitive objects.
                 */
                map.put(mapKey, valueInstance); 
            } else {
                String propertyName = ((SimpleValueMapStructure) mapStructure).getValuePropertyName();
                String value = entity.findProperty(propertyName).getValue();
                Object convertedPrimitive = convertPrimitiveBasedOnType(propertyName, value, valueMergedProperties);
                map.put(mapKey, convertedPrimitive);
            }
            
            Entity[] responses = getMapRecords(instance, mapStructure, ceilingMergedProperties, valueMergedProperties, entity.findProperty("symbolicId"));
            for (Entity response : responses) {
                if (response.findProperty(mapStructure.getKeyPropertyName()).getValue().equals(persistencePackage.getEntity().findProperty(mapStructure.getKeyPropertyName()).getValue())) {
                    return response;
                }
            }
            return responses[0];
        } catch (Exception e) {
            throw new ServiceException("Problem updating entity : " + e.getMessage(), e);
        }
    }
    
    protected Object convertPrimitiveBasedOnType(String valuePropertyName, String value, Map<String, FieldMetadata> valueMergedProperties) throws ParseException {
        switch(((BasicFieldMetadata) valueMergedProperties.get(valuePropertyName)).getFieldType()) {
            case BOOLEAN :
                return Boolean.parseBoolean(value);
            case DATE :
                return getSimpleDateFormatter().parse(value);
            case DECIMAL :
                return new BigDecimal(value);
            case MONEY :
                return new Money(value);
            case INTEGER :
                return Integer.parseInt(value);
            default :
                return value;
        }
    }

    @Override
    public Entity update(PersistencePackage persistencePackage) throws ServiceException {
        String[] customCriteria = persistencePackage.getCustomCriteria();
        if (customCriteria != null && customCriteria.length > 0) {
            LOG.warn("custom persistence handlers and custom criteria not supported for update types other than BASIC");
        }
        PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
        Entity entity = persistencePackage.getEntity();
        MapStructure mapStructure = (MapStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);
        if (!mapStructure.getMutable()) {
            throw new SecurityServiceException("Field not mutable");
        }
        try {
            Map<String, FieldMetadata> ceilingMergedProperties = getSimpleMergedProperties(entity.getType()[0],
                    persistencePerspective);
            String mapKey = entity.findProperty(mapStructure.getKeyPropertyName()).getValue();
            if (ceilingMergedProperties.containsKey(mapStructure.getMapProperty() + FieldManager.MAPFIELDSEPARATOR + mapKey)) {
                throw new ServiceException("\"" + mapKey + "\" is a reserved property name.");
            }

            Serializable instance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(entity.getType()[0]), Long.valueOf(entity.findProperty("symbolicId").getValue()));

            Assert.isTrue(instance != null, "Entity not found");

            FieldManager fieldManager = getFieldManager();
            Map map = (Map) fieldManager.getFieldValue(instance, mapStructure.getMapProperty());
            
            PersistentClass persistentClass = persistenceManager.getDynamicEntityDao().getPersistentClass(mapStructure.getValueClassName());
            Map<String, FieldMetadata> valueUnfilteredMergedProperties;
            if (persistentClass == null) {
                valueUnfilteredMergedProperties = persistenceManager.getDynamicEntityDao().getPropertiesForPrimitiveClass(
                    ((SimpleValueMapStructure) mapStructure).getValuePropertyName(), 
                    ((SimpleValueMapStructure) mapStructure).getValuePropertyFriendlyName(),
                    Class.forName(mapStructure.getValueClassName()), 
                    Class.forName(entity.getType()[0]), 
                    MergedPropertyType.MAPSTRUCTUREVALUE
                );
            } else {
                valueUnfilteredMergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                    mapStructure.getValueClassName(), 
                    new Class[]{Class.forName(mapStructure.getValueClassName())},
                    null, 
                    new String[]{}, 
                    new ForeignKey[]{}, 
                    MergedPropertyType.MAPSTRUCTUREVALUE,
                    persistencePerspective.getPopulateToOneFields(), 
                    persistencePerspective.getIncludeFields(), 
                    persistencePerspective.getExcludeFields(),
                    persistencePerspective.getConfigurationKey(),
                    ""
                );
            }
            Map<String, FieldMetadata> valueMergedProperties = filterOutCollectionMetadata(valueUnfilteredMergedProperties);
            
            if (StringUtils.isEmpty(mapKey)) {
                entity.addValidationError(mapStructure.getKeyPropertyName(), RequiredPropertyValidator.ERROR_MESSAGE);
                LOG.debug("No key property passed in for map, failing validation");
            }

            populate: {
                if (persistentClass != null) {
                    Serializable valueInstance = (Serializable) map.get(entity.findProperty("priorKey").getValue());

                    if (valueInstance == null) {
                        valueInstance = procureSandBoxMapValue(mapStructure, entity);
                        if (valueInstance == null) {
                            break populate;
                        }
                    }

                    if (map.get(mapKey) != null && !map.get(mapKey).equals(valueInstance)) {
                        entity.addValidationError(mapStructure.getKeyPropertyName(), "keyExistsValidationError");
                    }

                    if (StringUtils.isNotBlank(mapStructure.getMapKeyValueProperty())) {
                        Property p = entity.findProperty("key");
                        Property newP = new Property();
                        newP.setName(mapStructure.getMapKeyValueProperty());
                        newP.setValue(p.getValue());
                        newP.setIsDirty(p.getIsDirty());
                        entity.addProperty(newP);
                    }

                    //allow validation on other properties in order to show key validation errors along with all the other properties
                    //validation errors
                    valueInstance = createPopulatedInstance(valueInstance, entity, valueMergedProperties, false);

                    if (StringUtils.isNotEmpty(mapKey) && !entity.isValidationFailure()) {
                        if (!entity.findProperty("priorKey").getValue().equals(mapKey)) {
                            map.remove(entity.findProperty("priorKey").getValue());
                        }
                        /*
                         * TODO this map manipulation code currently assumes the key value is a String. This should be widened to accept
                         * additional types of primitive objects.
                         */
                        map.put(entity.findProperty(mapStructure.getKeyPropertyName()).getValue(), valueInstance);
                    }
                } else {
                    if (StringUtils.isNotEmpty(mapKey) && !entity.isValidationFailure()) {
                        map.put(entity.findProperty(mapStructure.getKeyPropertyName()).getValue(), entity.findProperty(((SimpleValueMapStructure) mapStructure).getValuePropertyName()).getValue());
                    }
                }
            }

            instance = persistenceManager.getDynamicEntityDao().merge(instance);
            
            Entity[] responses = getMapRecords(instance, mapStructure, ceilingMergedProperties, valueMergedProperties, entity.findProperty("symbolicId"));
            for (Entity response : responses) {
                if (response.findProperty(mapStructure.getKeyPropertyName()).getValue().equals(persistencePackage.getEntity().findProperty(mapStructure.getKeyPropertyName()).getValue())) {
                    return response;
                }
            }
            //could be empty if reverting a sandbox item that has experienced a deletion. make sure to at least return an empty instance of Entity.
            return ArrayUtils.isEmpty(responses)?new Entity():responses[0];
        } catch (Exception e) {
            throw new ServiceException("Problem updating entity : " + e.getMessage(), e);
        }
    }

    @Override
    public void remove(PersistencePackage persistencePackage) throws ServiceException {
        String[] customCriteria = persistencePackage.getCustomCriteria();
        if (customCriteria != null && customCriteria.length > 0) {
            LOG.warn("custom persistence handlers and custom criteria not supported for remove types other than BASIC");
        }
        PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
        Entity entity = persistencePackage.getEntity();
        MapStructure mapStructure = (MapStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);
        if (!mapStructure.getMutable()) {
            throw new SecurityServiceException("Field not mutable");
        }
        try {
            Map<String, FieldMetadata> ceilingMergedProperties = getSimpleMergedProperties(entity.getType()[0],
                    persistencePerspective);
            String mapKey = entity.findProperty(mapStructure.getKeyPropertyName()).getValue();
            if (ceilingMergedProperties.containsKey(mapStructure.getMapProperty() + FieldManager.MAPFIELDSEPARATOR + mapKey)) {
                throw new ServiceException("\"" + mapKey + "\" is a reserved property name.");
            }

            Serializable instance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(entity.getType()[0]), Long.valueOf(entity.findProperty("symbolicId").getValue()));

            Assert.isTrue(instance != null, "Entity not found");

            FieldManager fieldManager = getFieldManager();
            Map map = (Map) fieldManager.getFieldValue(instance, mapStructure.getMapProperty());
            
            Object value = map.remove(entity.findProperty("priorKey").getValue());
            if (mapStructure.getDeleteValueEntity()) {
                persistenceManager.getDynamicEntityDao().remove((Serializable) value);
            }
        } catch (Exception e) {
            throw new ServiceException("Problem removing entity : " + e.getMessage(), e);
        }
    }
    
    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException {
        Entity[] payload;
        int totalRecords;
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        if (StringUtils.isEmpty(persistencePackage.getFetchTypeFullyQualifiedClassname())) {
            persistencePackage.setFetchTypeFullyQualifiedClassname(ceilingEntityFullyQualifiedClassname);
        }
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Class<?>[] entities = persistenceManager.getPolymorphicEntities(ceilingEntityFullyQualifiedClassname);
            Map<String, FieldMetadata> mergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                ceilingEntityFullyQualifiedClassname, 
                entities, 
                (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY), 
                persistencePerspective.getAdditionalNonPersistentProperties(), 
                persistencePerspective.getAdditionalForeignKeys(),
                MergedPropertyType.PRIMARY,
                persistencePerspective.getPopulateToOneFields(), 
                persistencePerspective.getIncludeFields(), 
                persistencePerspective.getExcludeFields(),
                persistencePerspective.getConfigurationKey(),
                ""
            );
            MapStructure mapStructure = (MapStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);
            
            PersistentClass persistentClass = persistenceManager.getDynamicEntityDao().getPersistentClass(mapStructure.getValueClassName());
            Map<String, FieldMetadata> valueUnfilteredMergedProperties;
            if (persistentClass == null) {
                valueUnfilteredMergedProperties = persistenceManager.getDynamicEntityDao().getPropertiesForPrimitiveClass(
                    ((SimpleValueMapStructure) mapStructure).getValuePropertyName(), 
                    ((SimpleValueMapStructure) mapStructure).getValuePropertyFriendlyName(),
                    Class.forName(mapStructure.getValueClassName()), 
                    Class.forName(ceilingEntityFullyQualifiedClassname), 
                    MergedPropertyType.MAPSTRUCTUREVALUE
                );
            } else {
                valueUnfilteredMergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                    mapStructure.getValueClassName(), 
                    new Class[]{Class.forName(mapStructure.getValueClassName())},
                    null, 
                    new String[]{}, 
                    new ForeignKey[]{}, 
                    MergedPropertyType.MAPSTRUCTUREVALUE,
                    false,
                    new String[]{},
                    new String[]{},
                    null,
                    ""
                );
            }
            Map<String, FieldMetadata> valueMergedProperties = filterOutCollectionMetadata(valueUnfilteredMergedProperties);

            List<FilterMapping> filterMappings = getFilterMappings(persistencePerspective, cto, persistencePackage
                    .getFetchTypeFullyQualifiedClassname(), mergedProperties);

            if (CollectionUtils.isNotEmpty(cto.getAdditionalFilterMappings())) {
                filterMappings.addAll(cto.getAdditionalFilterMappings());
            }

            totalRecords = getTotalRecords(persistencePackage.getFetchTypeFullyQualifiedClassname(), filterMappings);
            if (totalRecords > 1) {
                throw new ServiceException("Queries to retrieve an entity containing a MapStructure must return only 1 entity. Your query returned ("+totalRecords+") values.");
            }
            List<Serializable> records = getPersistentRecords(persistencePackage.getFetchTypeFullyQualifiedClassname(), filterMappings, cto.getFirstResult(), cto.getMaxResults());
            Map<String, FieldMetadata> ceilingMergedProperties = getSimpleMergedProperties(ceilingEntityFullyQualifiedClassname,
                    persistencePerspective);
            payload = getMapRecords(records.get(0), mapStructure, ceilingMergedProperties, valueMergedProperties, null);
        } catch (Exception e) {
            throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
        }
        
        DynamicResultSet results = new DynamicResultSet(null, payload, payload.length);
        
        return results;
    }

    protected Serializable procureSandBoxMapValue(MapStructure mapStructure, Entity entity) {
        try {
            Serializable valueInstance = null;
            //this is probably a sync from another sandbox where they've updated a map item for which we've updated the key in our own sandbox
            //(i.e. the map entry key was changed by us in our sandbox, so our map does not have the requested key)
            Class<?> valueClass = Class.forName(mapStructure.getValueClassName());
            Map<String, Object> idMetadata = getPersistenceManager().getDynamicEntityDao().
                    getIdMetadata(valueClass);
            String idProperty = (String) idMetadata.get("name");
            Property prop = entity.findProperty(idProperty);
            if (prop != null) {
                Serializable identifier;
                if (!(((Type) idMetadata.get("type")) instanceof StringType)) {
                    identifier = Long.parseLong(prop.getValue());
                } else {
                    identifier = prop.getValue();
                }
                valueInstance = (Serializable) getPersistenceManager().getDynamicEntityDao().find(valueClass, identifier);
                BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
                if (sandBoxHelper.isSandBoxable(valueInstance.getClass().getName()) &&
                        context != null && !context.isProductionSandBox()) {
                    if (sandBoxHelper.isPromote() && !sandBoxHelper.isReject()) {
                        //if this is a prod record (i.e. the destination map has deleted our record), then duplicate our value
                        //so it's available in this sandbox
                        valueInstance = getPersistenceManager().getDynamicEntityDao().merge(valueInstance);
                    } else {
                        valueInstance = null;
                    }
                }
            }
            return valueInstance;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected Entity[] getMapRecords(Serializable record, MapStructure mapStructure, Map<String, FieldMetadata> ceilingMergedProperties, Map<String, FieldMetadata> valueMergedProperties, Property symbolicIdProperty) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchFieldException {
        //compile a list of mapKeys that were used as mapFields
        List<String> mapFieldKeys = new ArrayList<String>();
        String mapProperty = mapStructure.getMapProperty();
        for (Map.Entry<String, FieldMetadata> entry : ceilingMergedProperties.entrySet()) {
            if (entry.getKey().startsWith(mapProperty + FieldManager.MAPFIELDSEPARATOR)) {
                mapFieldKeys.add(entry.getKey().substring(entry.getKey().indexOf(FieldManager.MAPFIELDSEPARATOR) + FieldManager.MAPFIELDSEPARATOR.length(), entry.getKey().length()));
            }
        }
        Collections.sort(mapFieldKeys);

        FieldManager fieldManager = getFieldManager();
        Map map;
        try {
            map = (Map) fieldManager.getFieldValue(record, mapProperty);
        } catch (FieldNotAvailableException e) {
            throw new IllegalArgumentException(e);
        }
        List<Entity> entities = new ArrayList<Entity>(map.size());
        for (Object key : map.keySet()) {
            if (key instanceof String && mapFieldKeys.contains(key)) {
                continue;
            }
            entities.add(getMapRecord(record.getClass().getName(), (Serializable) map.get(key), mapStructure, valueMergedProperties, symbolicIdProperty, key));
        }

        return entities.toArray(new Entity[entities.size()]);
    }

    protected Entity getMapRecord(String ceilingClass, Serializable valueInstance, MapStructure mapStructure, Map<String, FieldMetadata> valueMergedProperties, Property symbolicIdProperty, Object key) {
        Entity entityItem = new Entity();
        entityItem.setType(new String[]{ceilingClass});
        List<Property> props = new ArrayList<Property>();

        Property keyProperty = new Property();
        keyProperty.setName(mapStructure.getKeyPropertyName());
        String keyPropertyValue;
        if (Date.class.isAssignableFrom(key.getClass())) {
            keyPropertyValue = getSimpleDateFormatter().format((Date) key);
        } else if (Timestamp.class.isAssignableFrom(key.getClass())) {
            keyPropertyValue = getSimpleDateFormatter().format(new Date(((Timestamp) key).getTime()));
        } else if (Calendar.class.isAssignableFrom(key.getClass())) {
            keyPropertyValue = getSimpleDateFormatter().format(((Calendar) key).getTime());
        } else if (Double.class.isAssignableFrom(key.getClass())) {
            keyPropertyValue = getDecimalFormatter().format(key);
        } else if (BigDecimal.class.isAssignableFrom(key.getClass())) {
            keyPropertyValue = getDecimalFormatter().format(key);
        } else {
            keyPropertyValue = key.toString();
        }
        keyProperty.setValue(keyPropertyValue);
        props.add(keyProperty);
        if (SimpleValueMapStructure.class.isInstance(mapStructure)) {
            SimpleValueMapStructure simpleValueMapStructure = (SimpleValueMapStructure) mapStructure;
            Property valueProperty = new Property();
            valueProperty.setName(simpleValueMapStructure.getValuePropertyName());
            valueProperty.setDisplayValue((String)valueInstance);
            valueProperty.setValue((String)valueInstance);
            props.add(valueProperty);
        }
        extractPropertiesFromPersistentEntity(valueMergedProperties, valueInstance, props);
        if (symbolicIdProperty != null) {
            props.add(symbolicIdProperty);
        }

        Property[] properties = new Property[props.size()];
        properties = props.toArray(properties);
        entityItem.setProperties(properties);

        return entityItem;
    }
}
