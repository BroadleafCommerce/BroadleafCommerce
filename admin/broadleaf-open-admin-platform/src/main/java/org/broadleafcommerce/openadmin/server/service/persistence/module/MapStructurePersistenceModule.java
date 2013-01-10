/*
 * Copyright 2008-2009 the original author or authors.
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

package org.broadleafcommerce.openadmin.server.service.persistence.module;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.money.Money;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MapStructure;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.OperationType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.dto.SimpleValueMapStructure;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.hibernate.mapping.PersistentClass;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class MapStructurePersistenceModule extends BasicPersistenceModule {

    private static final Log LOG = LogFactory.getLog(MapStructurePersistenceModule.class);
    
    public boolean isCompatible(OperationType operationType) {
        return OperationType.MAPSTRUCTURE.equals(operationType);
    }
    
    public void extractProperties(Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties, List<Property> properties) throws NumberFormatException {
        if (mergedProperties.get(MergedPropertyType.MAPSTRUCTUREKEY) != null) {
            extractPropertiesFromMetadata(mergedProperties.get(MergedPropertyType.MAPSTRUCTUREKEY), properties, false);
        }
        if (mergedProperties.get(MergedPropertyType.MAPSTRUCTUREVALUE) != null) {
            extractPropertiesFromMetadata(mergedProperties.get(MergedPropertyType.MAPSTRUCTUREVALUE), properties, false);
        }
    }

    protected Entity[] getMapRecords(Serializable record, MapStructure mapStructure, Map<String, FieldMetadata> valueMergedProperties, Property symbolicIdProperty) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException, IllegalArgumentException, ClassNotFoundException {
        FieldManager fieldManager = getFieldManager();
        Map map = (Map) fieldManager.getFieldValue(record, mapStructure.getMapProperty());
        Entity[] entities = new Entity[map.size()];
        int j=0;
        for (Object key : map.keySet()) {
            Entity entityItem = new Entity();
            entityItem.setType(new String[]{record.getClass().getName()});
            entities[j] = entityItem;
            List<Property> props = new ArrayList<Property>();
            
            Property propertyItem = new Property();
            propertyItem.setName(mapStructure.getKeyPropertyName());
            props.add(propertyItem);
            String strVal;
            if (Date.class.isAssignableFrom(key.getClass())) {
                strVal = dateFormat.format((Date) key);
            } else if (Timestamp.class.isAssignableFrom(key.getClass())) {
                strVal = dateFormat.format(new Date(((Timestamp) key).getTime()));
            } else if (Calendar.class.isAssignableFrom(key.getClass())) {
                strVal = dateFormat.format(((Calendar) key).getTime());
            } else if (Double.class.isAssignableFrom(key.getClass())) {
                strVal = decimalFormat.format((Double) key);
            } else if (BigDecimal.class.isAssignableFrom(key.getClass())) {
                strVal = decimalFormat.format(((BigDecimal) key).doubleValue());
            } else {
                strVal = key.toString();
            }
            propertyItem.setValue(strVal);
            
            PersistentClass persistentClass = persistenceManager.getDynamicEntityDao().getPersistentClass(mapStructure.getValueClassName());
            if (persistentClass == null) {
                Property temp = new Property();
                temp.setName(((SimpleValueMapStructure) mapStructure).getValuePropertyName());
                temp.setValue(String.valueOf(map.get(key)));
                props.add(temp);
            } else {
                extractPropertiesFromPersistentEntity(valueMergedProperties, (Serializable) map.get(key), props);
            }
            if (symbolicIdProperty != null) {
                props.add(symbolicIdProperty);
            }
            
            Property[] properties = new Property[props.size()];
            properties = props.toArray(properties);
            entityItem.setProperties(properties);
            j++;
        }
        
        return entities;
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
            LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
            throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Entity add(PersistencePackage persistencePackage) throws ServiceException {
        String[] customCriteria = persistencePackage.getCustomCriteria();
        if (customCriteria != null && customCriteria.length > 0) {
            LOG.warn("custom persistence handlers and custom criteria not supported for add types other than ENTITY");
        }
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Entity entity = persistencePackage.getEntity();
            MapStructure mapStructure = (MapStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);
            
            Serializable instance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(entity.getType()[0]), Long.valueOf(entity.findProperty("symbolicId").getValue()));
            FieldManager fieldManager = getFieldManager();
            Map map = (Map) fieldManager.getFieldValue(instance, mapStructure.getMapProperty());
            
            PersistentClass persistentClass = persistenceManager.getDynamicEntityDao().getPersistentClass(mapStructure.getValueClassName());
            Map<String, FieldMetadata> valueMergedProperties;
            if (persistentClass == null) {
                valueMergedProperties = persistenceManager.getDynamicEntityDao().getPropertiesForPrimitiveClass(
                    ((SimpleValueMapStructure) mapStructure).getValuePropertyName(), 
                    ((SimpleValueMapStructure) mapStructure).getValuePropertyFriendlyName(),
                    Class.forName(mapStructure.getValueClassName()), 
                    Class.forName(entity.getType()[0]), 
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
            
            if (persistentClass != null) {
                Serializable valueInstance = (Serializable) Class.forName(mapStructure.getValueClassName()).newInstance();
                valueInstance = createPopulatedInstance(valueInstance, entity, valueMergedProperties, false);
                valueInstance = persistenceManager.getDynamicEntityDao().persist(valueInstance);
                /*
                 * TODO this map manipulation code currently assumes the key value is a String. This should be widened to accept
                 * additional types of primitive objects.
                 */
                map.put(entity.findProperty(mapStructure.getKeyPropertyName()).getValue(), valueInstance); 
            } else {
                String propertyName = ((SimpleValueMapStructure) mapStructure).getValuePropertyName();
                String value = entity.findProperty(propertyName).getValue();
                Object convertedPrimitive = convertPrimitiveBasedOnType(propertyName, value, valueMergedProperties);
                map.put(entity.findProperty(mapStructure.getKeyPropertyName()).getValue(), convertedPrimitive);
            }
            
            instance = persistenceManager.getDynamicEntityDao().merge(instance);
            
            return getMapRecords(instance, mapStructure, valueMergedProperties, entity.findProperty("symbolicId"))[0];
        } catch (Exception e) {
            LOG.error("Problem editing entity", e);
            throw new ServiceException("Problem updating entity : " + e.getMessage(), e);
        }
    }
    
    protected Object convertPrimitiveBasedOnType(String valuePropertyName, String value, Map<String, FieldMetadata> valueMergedProperties) throws ParseException {
        switch(valueMergedProperties.get(valuePropertyName).getFieldType()) {
        case BOOLEAN :
            return Boolean.parseBoolean(value);
        case DATE :
            return dateFormat.parse(value);
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
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Entity update(PersistencePackage persistencePackage) throws ServiceException {
        String[] customCriteria = persistencePackage.getCustomCriteria();
        if (customCriteria != null && customCriteria.length > 0) {
            LOG.warn("custom persistence handlers and custom criteria not supported for update types other than ENTITY");
        }
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Entity entity = persistencePackage.getEntity();
            MapStructure mapStructure = (MapStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);
            
            Serializable instance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(entity.getType()[0]), Long.valueOf(entity.findProperty("symbolicId").getValue()));
            FieldManager fieldManager = getFieldManager();
            Map map = (Map) fieldManager.getFieldValue(instance, mapStructure.getMapProperty());
            
            PersistentClass persistentClass = persistenceManager.getDynamicEntityDao().getPersistentClass(mapStructure.getValueClassName());
            Map<String, FieldMetadata> valueMergedProperties;
            if (persistentClass == null) {
                valueMergedProperties = persistenceManager.getDynamicEntityDao().getPropertiesForPrimitiveClass(
                    ((SimpleValueMapStructure) mapStructure).getValuePropertyName(), 
                    ((SimpleValueMapStructure) mapStructure).getValuePropertyFriendlyName(),
                    Class.forName(mapStructure.getValueClassName()), 
                    Class.forName(entity.getType()[0]), 
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
            
            if (persistentClass != null) {
                Serializable valueInstance = (Serializable) map.get(entity.findProperty("priorKey").getValue());
                if (!entity.findProperty("priorKey").getValue().equals(entity.findProperty(mapStructure.getKeyPropertyName()).getValue())) {
                    map.remove(entity.findProperty("priorKey").getValue());
                }
                valueInstance = createPopulatedInstance(valueInstance, entity, valueMergedProperties, false);
                /*
                 * TODO this map manipulation code currently assumes the key value is a String. This should be widened to accept
                 * additional types of primitive objects.
                 */
                map.put(entity.findProperty(mapStructure.getKeyPropertyName()).getValue(), valueInstance);
            } else {
                map.put(entity.findProperty(mapStructure.getKeyPropertyName()).getValue(), entity.findProperty(((SimpleValueMapStructure) mapStructure).getValuePropertyName()).getValue());
            }
            
            
            instance = persistenceManager.getDynamicEntityDao().merge(instance);
            
            return getMapRecords(instance, mapStructure, valueMergedProperties, entity.findProperty("symbolicId"))[0];
        } catch (Exception e) {
            LOG.error("Problem editing entity", e);
            throw new ServiceException("Problem updating entity : " + e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public void remove(PersistencePackage persistencePackage) throws ServiceException {
        String[] customCriteria = persistencePackage.getCustomCriteria();
        if (customCriteria != null && customCriteria.length > 0) {
            LOG.warn("custom persistence handlers and custom criteria not supported for remove types other than ENTITY");
        }
        try {
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Entity entity = persistencePackage.getEntity();
            MapStructure mapStructure = (MapStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);
            
            Serializable instance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(entity.getType()[0]), Long.valueOf(entity.findProperty("symbolicId").getValue()));
            FieldManager fieldManager = getFieldManager();
            Map map = (Map) fieldManager.getFieldValue(instance, mapStructure.getMapProperty());
            
            Object value = map.remove(entity.findProperty("priorKey").getValue());
            if (mapStructure.getDeleteValueEntity()) {
                persistenceManager.getDynamicEntityDao().remove((Serializable) value);
            }
        } catch (Exception e) {
            LOG.error("Problem editing entity", e);
            throw new ServiceException("Problem removing entity : " + e.getMessage(), e);
        }
    }
    
    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException {
        Entity[] payload;
        int totalRecords;
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
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
            Map<String, FieldMetadata> valueMergedProperties;
            if (persistentClass == null) {
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
                    false,
                    new String[]{},
                    new String[]{},
                    null,
                    ""
                );
            }
            
            BaseCtoConverter ctoConverter = getCtoConverter(persistencePerspective, cto, ceilingEntityFullyQualifiedClassname, mergedProperties);
            PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, ceilingEntityFullyQualifiedClassname);
            totalRecords = getTotalRecords(ceilingEntityFullyQualifiedClassname, cto, ctoConverter);
            if (totalRecords > 1) {
                throw new ServiceException("Queries to retrieve an entity containing a MapStructure must return only 1 entity. Your query returned ("+totalRecords+") values.");
            }
            List<Serializable> records = persistenceManager.getDynamicEntityDao().query(queryCriteria, Class.forName(ceilingEntityFullyQualifiedClassname));
            payload = getMapRecords(records.get(0), mapStructure, valueMergedProperties, null);
        } catch (ServiceException e) {
            LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
            throw e;
        } catch (Exception e) {
            LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
            throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
        }
        
        DynamicResultSet results = new DynamicResultSet(null, payload, payload.length);
        
        return results;
    }
}
