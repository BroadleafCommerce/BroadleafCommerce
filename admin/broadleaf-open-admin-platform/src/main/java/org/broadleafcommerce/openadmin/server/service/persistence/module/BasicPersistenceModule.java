/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.server.service.persistence.module;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.presentation.client.ForeignKeyRestrictionType;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.DOMException;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.criteria.AssociationPathElement;
import com.anasoft.os.daofusion.criteria.FilterCriterion;
import com.anasoft.os.daofusion.criteria.NestedPropertyCriteria;
import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.criteria.SimpleFilterCriterionProvider;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.server.CriteriaTransferObjectCountWrapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.persistence.Embedded;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

/**
 * @author jfischer
 */
@Component("blBasicPersistenceModule")
@Scope("prototype")
public class BasicPersistenceModule implements PersistenceModule, RecordHelper, ApplicationContextAware {
    
    private static final Log LOG = LogFactory.getLog(BasicPersistenceModule.class);

    protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");
    protected DecimalFormat decimalFormat;
    protected ApplicationContext applicationContext;
    protected PersistenceManager persistenceManager;

    public BasicPersistenceModule() {
        decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        decimalFormat.applyPattern("0.########");
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public boolean isCompatible(OperationType operationType) {
        return OperationType.BASIC == operationType || OperationType.NONDESTRUCTIVEREMOVE == operationType;
    }

    public FieldManager getFieldManager() {
        return persistenceManager.getDynamicEntityDao().getFieldManager();
    }
    
    protected Date parseDate(String value) throws ParseException {
        return dateFormat.parse(value);
    }

    public SimpleDateFormat getDateFormatter() {
        return dateFormat;
    }

    public DecimalFormat getDecimalFormatter() {
        return decimalFormat;
    }

    protected Map<String, FieldMetadata> filterOutCollectionMetadata(Map<String, FieldMetadata> metadata) {
        if (metadata == null) {
            return null;
        }
        Map<String, FieldMetadata> newMap = new HashMap<String, FieldMetadata>();
        for (Map.Entry<String, FieldMetadata> entry : metadata.entrySet()) {
            if (entry.getValue() instanceof BasicFieldMetadata) {
                newMap.put(entry.getKey(), entry.getValue());
            }
        }

        return newMap;
    }

    public Serializable createPopulatedInstance(Serializable instance, Entity entity, Map<String, FieldMetadata> unfilteredProperties, Boolean setId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException, NumberFormatException, InstantiationException, ClassNotFoundException {
        Map<String, FieldMetadata> mergedProperties = filterOutCollectionMetadata(unfilteredProperties);
        FieldManager fieldManager = getFieldManager();
        for (Property property : entity.getProperties()) {
            Field field = fieldManager.getField(instance.getClass(), property.getName());
            if (field == null) {
                LOG.debug("Unable to find a bean property for the reported property: " + property.getName() + ". Ignoring property.");
                continue;
            }
            Class<?> returnType = field.getType();
            String value = property.getValue();
            BasicFieldMetadata metadata = (BasicFieldMetadata) mergedProperties.get(property.getName());
            if (metadata != null) {
                Boolean mutable = metadata.getMutable();
                Boolean readOnly = metadata.getReadOnly();
                if ((mutable == null || mutable) && (readOnly == null || !readOnly)) {
                    if (value != null) {
                        switch (metadata.getFieldType()) {
                            case BOOLEAN:
                                if (Character.class.isAssignableFrom(returnType)) {
                                    fieldManager.setFieldValue(instance, property.getName(), Boolean.valueOf(value) ? 'Y' : 'N');
                                } else {
                                    fieldManager.setFieldValue(instance, property.getName(), Boolean.valueOf(value));
                                }
                                break;
                            case DATE:
                                fieldManager.setFieldValue(instance, property.getName(), parseDate(value));
                                break;
                            case DECIMAL:
                                if (BigDecimal.class.isAssignableFrom(returnType)) {
                                    fieldManager.setFieldValue(instance, property.getName(), new BigDecimal(new Double(value)));
                                } else {
                                    fieldManager.setFieldValue(instance, property.getName(), new Double(value));
                                }
                                break;
                            case MONEY:
                                if (BigDecimal.class.isAssignableFrom(returnType)) {
                                    fieldManager.setFieldValue(instance, property.getName(), new BigDecimal(new Double(value)));
                                } else if (Double.class.isAssignableFrom(returnType)) {
                                    fieldManager.setFieldValue(instance, property.getName(), new Double(value));
                                } else {
                                    fieldManager.setFieldValue(instance, property.getName(), new Money(new Double(value)));
                                }
                                break;
                            case INTEGER:
                                if (int.class.isAssignableFrom(returnType) || Integer.class.isAssignableFrom(returnType)) {
                                    fieldManager.setFieldValue(instance, property.getName(), Integer.valueOf(value));
                                } else if (byte.class.isAssignableFrom(returnType) || Byte.class.isAssignableFrom(returnType)) {
                                    fieldManager.setFieldValue(instance, property.getName(), Byte.valueOf(value));
                                } else if (short.class.isAssignableFrom(returnType) || Short.class.isAssignableFrom(returnType)) {
                                    fieldManager.setFieldValue(instance, property.getName(), Short.valueOf(value));
                                } else if (long.class.isAssignableFrom(returnType) || Long.class.isAssignableFrom(returnType)) {
                                    fieldManager.setFieldValue(instance, property.getName(), Long.valueOf(value));
                                }
                                break;
                            default:
                                fieldManager.setFieldValue(instance, property.getName(), value);
                                break;
                            case EMAIL:
                                fieldManager.setFieldValue(instance, property.getName(), value);
                                break;
                            case FOREIGN_KEY: {
                                Serializable foreignInstance;
                                if (StringUtils.isEmpty(value)) {
                                    foreignInstance = null;
                                } else {
                                    if (SupportedFieldType.INTEGER.toString().equals(metadata.getSecondaryType().toString())) {
                                        foreignInstance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(metadata.getForeignKeyClass()), Long.valueOf(value));
                                    } else {
                                        foreignInstance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(metadata.getForeignKeyClass()), value);
                                    }
                                }

                                if (Collection.class.isAssignableFrom(returnType)) {
                                    Collection collection;
                                    try {
                                        collection = (Collection) fieldManager.getFieldValue(instance, property.getName());
                                    } catch (FieldNotAvailableException e) {
                                        throw new IllegalArgumentException(e);
                                    }
                                    if (!collection.contains(foreignInstance)) {
                                        collection.add(foreignInstance);
                                    }
                                } else if (Map.class.isAssignableFrom(returnType)) {
                                    throw new RuntimeException("Map structures are not supported for foreign key fields.");
                                } else {
                                    fieldManager.setFieldValue(instance, property.getName(), foreignInstance);
                                }
                                break;
                            }
                            case ADDITIONAL_FOREIGN_KEY: {
                                Serializable foreignInstance;
                                if (StringUtils.isEmpty(value)) {
                                    foreignInstance = null;
                                } else {
                                    if (SupportedFieldType.INTEGER.toString().equals(metadata.getSecondaryType().toString())) {
                                        foreignInstance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(metadata.getForeignKeyClass()), Long.valueOf(value));
                                    } else {
                                        foreignInstance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(metadata.getForeignKeyClass()), value);
                                    }
                                }

                                if (Collection.class.isAssignableFrom(returnType)) {
                                    Collection collection = null;
                                    try {
                                        collection = (Collection) fieldManager.getFieldValue(instance, property.getName());
                                    } catch (FieldNotAvailableException e) {
                                        throw new IllegalArgumentException(e);
                                    }
                                    if (!collection.contains(foreignInstance)) {
                                        collection.add(foreignInstance);
                                    }
                                } else if (Map.class.isAssignableFrom(returnType)) {
                                    throw new RuntimeException("Map structures are not supported for foreign key fields.");
                                } else {
                                    fieldManager.setFieldValue(instance, property.getName(), foreignInstance);
                                }
                                break;
                            }
                            case ID:
                                if (setId) {
                                    switch (metadata.getSecondaryType()) {
                                        case INTEGER:
                                            fieldManager.setFieldValue(instance, property.getName(), Long.valueOf(value));
                                            break;
                                        case STRING:
                                            fieldManager.setFieldValue(instance, property.getName(), value);
                                            break;
                                    }
                                }
                                break;
                        }
                    } else {
                        try {
                            if (fieldManager.getFieldValue(instance, property.getName()) != null && (metadata.getFieldType() != SupportedFieldType.ID || setId)) {
                                fieldManager.setFieldValue(instance, property.getName(), null);
                            }
                        } catch (FieldNotAvailableException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                }
            }
        }
        fieldManager.persistMiddleEntities();
        return instance;
    }

    public Entity getRecord(Map<String, FieldMetadata> primaryMergedProperties, Serializable record, Map<String, FieldMetadata> alternateMergedProperties, String pathToTargetObject) throws ParserConfigurationException, DOMException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, TransformerFactoryConfigurationError, IllegalArgumentException, TransformerException, SecurityException, ClassNotFoundException {
        List<Serializable> records = new ArrayList<Serializable>(1);
        records.add(record);
        Entity[] productEntities = getRecords(primaryMergedProperties, records, alternateMergedProperties, pathToTargetObject);
        return productEntities[0];
    }

    public Entity getRecord(Class<?> ceilingEntityClass, PersistencePerspective persistencePerspective, Serializable record) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, DOMException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, NoSuchFieldException {
        Map<String, FieldMetadata> mergedProperties = getSimpleMergedProperties(ceilingEntityClass.getName(), persistencePerspective);
        return getRecord(mergedProperties, record, null, null);
    }

    public Entity[] getRecords(Class<?> ceilingEntityClass, PersistencePerspective persistencePerspective, List<? extends Serializable> records) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, DOMException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, NoSuchFieldException {
        Map<String, FieldMetadata> mergedProperties = getSimpleMergedProperties(ceilingEntityClass.getName(), persistencePerspective);
        return getRecords(mergedProperties, records, null, null);
    }

    public Map<String, FieldMetadata> getSimpleMergedProperties(String entityName, PersistencePerspective persistencePerspective) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        return persistenceManager.getDynamicEntityDao().getSimpleMergedProperties(entityName, persistencePerspective);
    }

    public Entity[] getRecords(Map<String, FieldMetadata> primaryMergedProperties, List<? extends Serializable> records) throws ParserConfigurationException, DOMException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, TransformerFactoryConfigurationError, IllegalArgumentException, TransformerException, SecurityException, ClassNotFoundException {
        return getRecords(primaryMergedProperties, records, null, null);
    }

    public Entity[] getRecords(Map<String, FieldMetadata> primaryUnfilteredMergedProperties, List<? extends Serializable> records, Map<String, FieldMetadata> alternateUnfilteredMergedProperties, String pathToTargetObject) throws ParserConfigurationException, DOMException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, TransformerFactoryConfigurationError, IllegalArgumentException, TransformerException, SecurityException, ClassNotFoundException {
        Map<String, FieldMetadata> primaryMergedProperties = filterOutCollectionMetadata(primaryUnfilteredMergedProperties);
        Map<String, FieldMetadata> alternateMergedProperties = filterOutCollectionMetadata(alternateUnfilteredMergedProperties);
        Entity[] entities = new Entity[records.size()];
        int j = 0;
        for (Serializable recordEntity : records) {
            Serializable entity;
            if (pathToTargetObject != null) {
                try {
                    entity = (Serializable) getFieldManager().getFieldValue(recordEntity, pathToTargetObject);
                } catch (FieldNotAvailableException e) {
                    throw new IllegalArgumentException(e);
                }
            } else {
                entity = recordEntity;
            }
            Entity entityItem = new Entity();
            entityItem.setType(new String[]{entity.getClass().getName()});
            entities[j] = entityItem;

            List<Property> props = new ArrayList<Property>(primaryMergedProperties.size());
            extractPropertiesFromPersistentEntity(primaryMergedProperties, entity, props);
            if (alternateMergedProperties != null) {
                extractPropertiesFromPersistentEntity(alternateMergedProperties, recordEntity, props);
            }
            Property[] properties = new Property[props.size()];
            properties = props.toArray(properties);
            entityItem.setProperties(properties);
            j++;
        }

        return entities;
    }

    protected void extractPropertiesFromPersistentEntity(Map<String, FieldMetadata> mergedProperties, Serializable entity, List<Property> props) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException, IllegalArgumentException, ClassNotFoundException {
        FieldManager fieldManager = getFieldManager();
        for (Map.Entry<String, FieldMetadata> entry : mergedProperties.entrySet()) {
            String property = entry.getKey();
            BasicFieldMetadata metadata = (BasicFieldMetadata) entry.getValue();
            if (Class.forName(metadata.getInheritedFromType()).isAssignableFrom(entity.getClass()) || entity.getClass().isAssignableFrom(Class.forName(metadata.getInheritedFromType()))) {
                boolean proceed = true;
                if (property.contains(".")) {
                    StringTokenizer tokens = new StringTokenizer(property, ".");
                    Object testObject = entity;
                    while (tokens.hasMoreTokens()) {
                        String token = tokens.nextToken();
                        if (tokens.hasMoreTokens()) {
                            try {
                                testObject = fieldManager.getFieldValue(testObject, token);
                            } catch (FieldNotAvailableException e) {
                                proceed = false;
                                break;
                            }
                            if (testObject == null) {
                                Property propertyItem = new Property();
                                propertyItem.setName(property);
                                if (props.contains(propertyItem)) {
                                    proceed = false;
                                    break;
                                }
                                propertyItem.setValue(null);
                                props.add(propertyItem);
                                proceed = false;
                                break;
                            }
                        }
                    }
                }
                if (!proceed) {
                    continue;
                }

                boolean isFieldAccessible = true;
                Object value = null;
                try {
                    value = fieldManager.getFieldValue(entity, property);
                } catch (FieldNotAvailableException e) {
                    isFieldAccessible = false;
                }
                String strVal;
                checkField:
                {
                    if (isFieldAccessible) {
                        Property propertyItem = new Property();
                        propertyItem.setName(property);
                        if (props.contains(propertyItem)) {
                            continue;
                        }
                        props.add(propertyItem);
                        String displayVal = null;
                        if (value != null) {
                            if (metadata.getForeignKeyCollection()) {
                                ((BasicFieldMetadata) propertyItem.getMetadata()).setFieldType(metadata.getFieldType());
                                strVal = null;
                            } else if (Date.class.isAssignableFrom(value.getClass())) {
                                strVal = dateFormat.format((Date) value);
                            } else if (Timestamp.class.isAssignableFrom(value.getClass())) {
                                strVal = dateFormat.format(new Date(((Timestamp) value).getTime()));
                            } else if (Calendar.class.isAssignableFrom(value.getClass())) {
                                strVal = dateFormat.format(((Calendar) value).getTime());
                            } else if (Double.class.isAssignableFrom(value.getClass())) {
                                strVal = decimalFormat.format(value);
                            } else if (BigDecimal.class.isAssignableFrom(value.getClass())) {
                                strVal = decimalFormat.format(((BigDecimal) value).doubleValue());
                            } else if (metadata.getForeignKeyClass() != null) {
                                try {
                                    strVal = fieldManager.getFieldValue(value, metadata.getForeignKeyProperty()).toString();
                                    //see if there's a name property and use it for the display value
                                    Object temp = null;
                                    try {
                                        temp = fieldManager.getFieldValue(value, metadata.getForeignKeyDisplayValueProperty());
                                    } catch (FieldNotAvailableException e) {
                                        //do nothing
                                    }
                                    if (temp != null) {
                                        displayVal = temp.toString();
                                    }
                                } catch (FieldNotAvailableException e) {
                                    throw new IllegalArgumentException(e);
                                }
                            } else {
                                strVal = value.toString();
                            }
                            propertyItem.setValue(strVal);
                            propertyItem.setDisplayValue(displayVal);
                            break checkField;
                        }
                    }
                    //try a direct property acquisition via reflection
                    try {
                        Method method;
                        try {
                            //try a 'get' prefixed mutator first
                            String temp = "get" + property.substring(0, 1).toUpperCase() + property.substring(1, property.length());
                            method = entity.getClass().getMethod(temp, new Class[]{});
                        } catch (NoSuchMethodException e) {
                            method = entity.getClass().getMethod(property, new Class[]{});
                        }
                        value = method.invoke(entity, new String[]{});
                        Property propertyItem = new Property();
                        propertyItem.setName(property);
                        if (props.contains(propertyItem)) {
                            continue;
                        }
                        props.add(propertyItem);
                        if (value == null) {
                            strVal = null;
                        } else {
                            if (Date.class.isAssignableFrom(value.getClass())) {
                                strVal = dateFormat.format((Date) value);
                            } else if (Timestamp.class.isAssignableFrom(value.getClass())) {
                                strVal = dateFormat.format(new Date(((Timestamp) value).getTime()));
                            } else if (Calendar.class.isAssignableFrom(value.getClass())) {
                                strVal = dateFormat.format(((Calendar) value).getTime());
                            } else if (Double.class.isAssignableFrom(value.getClass())) {
                                strVal = decimalFormat.format(value);
                            } else if (BigDecimal.class.isAssignableFrom(value.getClass())) {
                                strVal = decimalFormat.format(((BigDecimal) value).doubleValue());
                            } else {
                                strVal = value.toString();
                            }
                        }
                        propertyItem.setValue(strVal);
                    } catch (NoSuchMethodException e) {
                        LOG.debug("Unable to find a specified property in the entity: " + property);
                        //do nothing - this property is simply not in the bean
                    }
                }
            }
        }
    }

    protected Entity update(PersistencePackage persistencePackage, Object primaryKey) throws ServiceException {
        try {
            Entity entity = persistencePackage.getEntity();
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Class<?>[] entities = persistenceManager.getPolymorphicEntities(persistencePackage.getCeilingEntityFullyQualifiedClassname());
            Map<String, FieldMetadata> mergedProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                persistencePackage.getCeilingEntityFullyQualifiedClassname(),
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
            if (primaryKey == null) {
                primaryKey = getPrimaryKey(entity, mergedProperties);
            }
            Serializable instance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(entity.getType()[0]), primaryKey);
            instance = createPopulatedInstance(instance, entity, mergedProperties, false);
            instance = persistenceManager.getDynamicEntityDao().merge(instance);

            List<Serializable> entityList = new ArrayList<Serializable>(1);
            entityList.add(instance);

            return getRecords(mergedProperties, entityList, null, null)[0];
        } catch (Exception e) {
            LOG.error("Problem editing entity", e);
            throw new ServiceException("Problem updating entity : " + e.getMessage(), e);
        }
    }

    public Object getPrimaryKey(Entity entity, Map<String, FieldMetadata> mergedUnfilteredProperties) throws RuntimeException {
        Map<String, FieldMetadata> mergedProperties = filterOutCollectionMetadata(mergedUnfilteredProperties);
        Object primaryKey = null;
        String idPropertyName = null;
        BasicFieldMetadata metaData = null;
        for (String property : mergedProperties.keySet()) {
            BasicFieldMetadata temp = (BasicFieldMetadata) mergedProperties.get(property);
            if (temp.getFieldType() == SupportedFieldType.ID && !property.contains(".")) {
                idPropertyName = property;
                metaData = temp;
                break;
            }
        }
        if (idPropertyName == null) {
            throw new RuntimeException("Could not find a primary key property in the passed entity with type: " + entity.getType()[0]);
        }
        for (Property property : entity.getProperties()) {
            if (property.getName().equals(idPropertyName)) {
                switch(metaData.getSecondaryType()) {
                case INTEGER:
                    primaryKey = Long.valueOf(property.getValue());
                    break;
                case STRING:
                    primaryKey = property.getValue();
                    break;
                }
                break;
            }
        }
        if (primaryKey == null) {
            throw new RuntimeException("Could not find the primary key property (" + idPropertyName + ") in the passed entity with type: " + entity.getType()[0]);
        }
        return primaryKey;
    }

    public BaseCtoConverter getCtoConverter(PersistencePerspective persistencePerspective, CriteriaTransferObject cto, String ceilingEntityFullyQualifiedClassname, Map<String, FieldMetadata> mergedUnfilteredProperties) throws ClassNotFoundException {
        Map<String, FieldMetadata> mergedProperties = filterOutCollectionMetadata(mergedUnfilteredProperties);
        BaseCtoConverter ctoConverter = (BaseCtoConverter) applicationContext.getBean("blBaseCtoConverter");
        for (String propertyId : cto.getPropertyIdSet()) {
            if (mergedProperties.containsKey(propertyId)) {
                buildProperty(persistencePerspective, cto, ceilingEntityFullyQualifiedClassname, mergedProperties, ctoConverter, propertyId);
            } else {
                ctoConverter.addEmptyMapping(ceilingEntityFullyQualifiedClassname, propertyId);
            }
        }
        if (cto.getPropertyIdSet().isEmpty()) {
            ctoConverter.addEmptyMapping(ceilingEntityFullyQualifiedClassname, "");
        }
        return ctoConverter;
    }

    protected void buildProperty(PersistencePerspective persistencePerspective, CriteriaTransferObject cto, String ceilingEntityFullyQualifiedClassname, Map<String, FieldMetadata> mergedProperties, BaseCtoConverter ctoConverter, String propertyName) throws ClassNotFoundException {
        AssociationPath associationPath;
        int dotIndex = propertyName.lastIndexOf('.');
        StringBuilder property;
        Class clazz = Class.forName(mergedProperties.get(propertyName).getInheritedFromType());
        Field field = getFieldManager().getField(clazz, propertyName);
        Class<?> targetType = null;
        if (field != null) {
            targetType = field.getType();
        }
        if (dotIndex >= 0) {
            property = new StringBuilder(propertyName.substring(dotIndex + 1, propertyName.length()));
            String prefix = propertyName.substring(0, dotIndex);
            StringTokenizer tokens = new StringTokenizer(prefix, ".");
            List<AssociationPathElement> elementList = new ArrayList<AssociationPathElement>(20);
            StringBuilder sb = new StringBuilder(150);
            StringBuilder pathBuilder = new StringBuilder(150);
            while (tokens.hasMoreElements()) {
                String token = tokens.nextToken();
                sb.append(token);
                pathBuilder.append(token);
                field = getFieldManager().getField(clazz, pathBuilder.toString());
                Embedded embedded = field.getAnnotation(Embedded.class);
                if (embedded != null) {
                    sb.append('.');
                } else {
                    elementList.add(new AssociationPathElement(sb.toString()));
                    sb = new StringBuilder(150);
                }
                pathBuilder.append('.');
            }
            if (!elementList.isEmpty()) {
                AssociationPathElement[] elements = elementList.toArray(new AssociationPathElement[elementList.size()]);
                associationPath = new AssociationPath(elements);
            } else {
                property = property.insert(0, sb.toString());
                associationPath = AssociationPath.ROOT;
            }
        } else {
            property = new StringBuilder(propertyName);
            associationPath = AssociationPath.ROOT;
        }
        String convertedProperty = property.toString();
        BasicFieldMetadata metadata = (BasicFieldMetadata) mergedProperties.get(propertyName);
        switch (metadata.getFieldType()) {
            case BOOLEAN:
                if (targetType == null || targetType.equals(Boolean.class) || targetType.equals(boolean.class)) {
                    ctoConverter.addBooleanMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                } else {
                    ctoConverter.addCharacterMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                }
                break;
            case DATE:
                ctoConverter.addDateMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                break;
            case DECIMAL:
                ctoConverter.addDecimalMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                break;
            case MONEY:
                ctoConverter.addDecimalMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                break;
            case INTEGER:
                ctoConverter.addLongMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                break;
            default:
                ctoConverter.addStringLikeMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                break;
            case EMAIL:
                ctoConverter.addStringLikeMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                break;
            case FOREIGN_KEY:
                if (cto.get(propertyName).getFilterValues().length > 0) {
                    ForeignKey foreignKey = (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
                    if (metadata.getForeignKeyCollection()) {
                        if (ForeignKeyRestrictionType.COLLECTION_SIZE_EQ.toString().equals(foreignKey.getRestrictionType().toString())) {
                            ctoConverter.addCollectionSizeEqMapping(ceilingEntityFullyQualifiedClassname, propertyName, AssociationPath.ROOT, propertyName);
                        } else {
                            AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
                            ctoConverter.addLongMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, metadata.getForeignKeyProperty());
                        }
                    } else if (cto.get(propertyName).getFilterValues()[0] == null || "null".equals(cto.get(propertyName).getFilterValues()[0])) {
                        ctoConverter.addNullMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, propertyName);
                    } else if (metadata.getSecondaryType() == SupportedFieldType.STRING) {
                        AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
                        ctoConverter.addStringEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, metadata.getForeignKeyProperty());
                    } else {
                        AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
                        ctoConverter.addLongEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, metadata.getForeignKeyProperty());
                    }
                } else {
                    ctoConverter.addEmptyMapping(ceilingEntityFullyQualifiedClassname, propertyName);
                }
                break;
            case ADDITIONAL_FOREIGN_KEY:
                if (cto.get(propertyName).getFilterValues().length > 0) {
                    int additionalForeignKeyIndexPosition = Arrays.binarySearch(persistencePerspective.getAdditionalForeignKeys(), new ForeignKey(propertyName, null, null), new Comparator<ForeignKey>() {
                        public int compare(ForeignKey o1, ForeignKey o2) {
                            return o1.getManyToField().compareTo(o2.getManyToField());
                        }
                    });
                    ForeignKey foreignKey = persistencePerspective.getAdditionalForeignKeys()[additionalForeignKeyIndexPosition];
                    if (metadata.getForeignKeyCollection()) {
                        if (ForeignKeyRestrictionType.COLLECTION_SIZE_EQ.toString().equals(foreignKey.getRestrictionType().toString())) {
                            ctoConverter.addCollectionSizeEqMapping(ceilingEntityFullyQualifiedClassname, propertyName, AssociationPath.ROOT, propertyName);
                        } else {
                            AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
                            ctoConverter.addLongMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, metadata.getForeignKeyProperty());
                        }
                    } else if (cto.get(propertyName).getFilterValues()[0] == null || "null".equals(cto.get(propertyName).getFilterValues()[0])) {
                        ctoConverter.addNullMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, propertyName);
                    } else if (metadata.getSecondaryType() == SupportedFieldType.STRING) {
                        AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
                        ctoConverter.addStringEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, metadata.getForeignKeyProperty());
                    } else {
                        AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
                        ctoConverter.addLongEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, metadata.getForeignKeyProperty());
                    }
                } else {
                    ctoConverter.addEmptyMapping(ceilingEntityFullyQualifiedClassname, propertyName);
                }
                break;
            case ID:
                switch (metadata.getSecondaryType()) {
                    case INTEGER:
                        ctoConverter.addLongEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                        break;
                    case STRING:
                        ctoConverter.addStringLikeMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                        break;
                }
                break;
        }
    }

    public int getTotalRecords(PersistencePackage persistencePackage, CriteriaTransferObject cto, BaseCtoConverter ctoConverter) throws ClassNotFoundException {
        PersistentEntityCriteria countCriteria = ctoConverter.convert(new CriteriaTransferObjectCountWrapper(cto).wrap(), persistencePackage.getCeilingEntityFullyQualifiedClassname());
        Class<?>[] entities = persistenceManager.getDynamicEntityDao().getAllPolymorphicEntitiesFromCeiling(Class.forName(persistencePackage.getCeilingEntityFullyQualifiedClassname()));
        boolean isArchivable = false;
        for (Class<?> entity : entities) {
            if (Status.class.isAssignableFrom(entity)) {
                isArchivable = true;
                break;
            }
        }
        if (isArchivable && !persistencePackage.getPersistencePerspective().getShowArchivedFields()) {
            SimpleFilterCriterionProvider criterionProvider = new  SimpleFilterCriterionProvider(SimpleFilterCriterionProvider.FilterDataStrategy.NONE, 0) {
                public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
                    return Restrictions.or(Restrictions.eq(targetPropertyName, 'N'), Restrictions.isNull(targetPropertyName));
                }
            };
            FilterCriterion filterCriterion = new FilterCriterion(AssociationPath.ROOT, "archiveStatus.archived", criterionProvider);
            ((NestedPropertyCriteria) countCriteria).add(filterCriterion);
        }
        return persistenceManager.getDynamicEntityDao().count(countCriteria, Class.forName(StringUtils.isEmpty(persistencePackage.getFetchTypeFullyQualifiedClassname()) ? persistencePackage.getCeilingEntityFullyQualifiedClassname() : persistencePackage.getFetchTypeFullyQualifiedClassname()));
    }

    public void extractProperties(Class<?>[] inheritanceLine, Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties, List<Property> properties) throws NumberFormatException {
        extractPropertiesFromMetadata(inheritanceLine, mergedProperties.get(MergedPropertyType.PRIMARY), properties, false, MergedPropertyType.PRIMARY);
    }

    protected void extractPropertiesFromMetadata(Class<?>[] inheritanceLine, Map<String, FieldMetadata> mergedProperties, List<Property> properties, Boolean isHiddenOverride, MergedPropertyType type) throws NumberFormatException {
        for (Map.Entry<String, FieldMetadata> entry : mergedProperties.entrySet()) {
            String property = entry.getKey();
            Property prop = new Property();
            FieldMetadata metadata = mergedProperties.get(property);
            prop.setName(property);
            Comparator<Property> comparator = new Comparator<Property>() {
                @Override
                public int compare(Property o1, Property o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            };
            Collections.sort(properties, comparator);
            int pos = Collections.binarySearch(properties, prop, comparator);
            if (pos >= 0 && MergedPropertyType.MAPSTRUCTUREKEY != type && MergedPropertyType.MAPSTRUCTUREVALUE != type) {
                logWarn: {
                    if ((metadata instanceof BasicFieldMetadata) && SupportedFieldType.ID.equals(((BasicFieldMetadata) metadata).getFieldType())) {
                        //don't warn for id field collisions, but still ignore the colliding fields
                        break logWarn;
                    }
                    LOG.warn("Detected a field name collision (" + metadata.getTargetClass() + "." + property + ") during inspection for the inheritance line starting with (" + inheritanceLine[0].getName() + "). Ignoring the additional field. This can occur most commonly when using the @AdminPresentationAdornedTargetCollection and the collection type and target class have field names in common. This situation should be avoided, as the system will strip the repeated fields, which can cause unpredictable behavior.");
                }
                continue;
            }
            properties.add(prop);
            prop.setMetadata(metadata);
            if (isHiddenOverride && prop.getMetadata() instanceof BasicFieldMetadata) {
                //this only makes sense for non collection types
                ((BasicFieldMetadata) prop.getMetadata()).setVisibility(VisibilityEnum.HIDDEN_ALL);
            }
        }
    }

    public void updateMergedProperties(PersistencePackage persistencePackage, Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties) throws ServiceException {
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
            allMergedProperties.put(MergedPropertyType.PRIMARY, mergedProperties);
        } catch (Exception e) {
            LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
            throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
        }
    }

    public Entity update(PersistencePackage persistencePackage) throws ServiceException {
        return update(persistencePackage, null);
    }

    public Entity add(PersistencePackage persistencePackage) throws ServiceException {
        try {
            Entity entity = persistencePackage.getEntity();
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Class<?>[] entities = persistenceManager.getPolymorphicEntities(persistencePackage.getCeilingEntityFullyQualifiedClassname());
            Map<String, FieldMetadata> mergedUnfilteredProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                persistencePackage.getCeilingEntityFullyQualifiedClassname(),
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
            Map<String, FieldMetadata> mergedProperties = filterOutCollectionMetadata(mergedUnfilteredProperties);

            String idProperty = null;
            for (String property : mergedProperties.keySet()) {
                if (((BasicFieldMetadata) mergedProperties.get(property)).getFieldType() == SupportedFieldType.ID) {
                    idProperty = property;
                    break;
                }
            }
            if (idProperty == null) {
                throw new RuntimeException("Could not find a primary key property in the passed entity with type: " + entity.getType()[0]);
            }
            Object primaryKey = null;
            try {
                primaryKey = getPrimaryKey(entity, mergedProperties);
            } catch (Exception e) {
                //do nothing
            }
            if (primaryKey == null) {
                Serializable instance = (Serializable) Class.forName(entity.getType()[0]).newInstance();
                instance = createPopulatedInstance(instance, entity, mergedProperties, false);
                instance = persistenceManager.getDynamicEntityDao().merge(instance);
                List<Serializable> entityList = new ArrayList<Serializable>(1);
                entityList.add(instance);

                return getRecords(mergedProperties, entityList, null, null)[0];
            } else {
                return update(persistencePackage, primaryKey);
            }
        } catch (ServiceException e) {
            LOG.error("Problem adding new entity", e);
            throw e;
        } catch (Exception e) {
            LOG.error("Problem adding new entity", e);
            throw new ServiceException("Problem adding new entity : " + e.getMessage(), e);
        }
    }

    public void remove(PersistencePackage persistencePackage) throws ServiceException {
        try {
            Entity entity = persistencePackage.getEntity();
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Class<?>[] entities = persistenceManager.getPolymorphicEntities(persistencePackage.getCeilingEntityFullyQualifiedClassname());
            Map<String, FieldMetadata> mergedUnfilteredProperties = persistenceManager.getDynamicEntityDao().getMergedProperties(
                persistencePackage.getCeilingEntityFullyQualifiedClassname(),
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
            Map<String, FieldMetadata> mergedProperties = filterOutCollectionMetadata(mergedUnfilteredProperties);
            Object primaryKey = getPrimaryKey(entity, mergedProperties);
            Serializable instance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(entity.getType()[0]), primaryKey);

            switch (persistencePerspective.getOperationTypes().getRemoveType()) {
                case NONDESTRUCTIVEREMOVE:
                    for (Property property : entity.getProperties()) {
                        String originalPropertyName = property.getName();
                        FieldManager fieldManager = getFieldManager();
                        if (fieldManager.getField(instance.getClass(), property.getName()) == null) {
                            LOG.debug("Unable to find a bean property for the reported property: " + originalPropertyName + ". Ignoring property.");
                            continue;
                        }
                        if (SupportedFieldType.FOREIGN_KEY == ((BasicFieldMetadata) mergedProperties.get(originalPropertyName)).getFieldType()) {
                            String value = property.getValue();
                            ForeignKey foreignKey = (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
                            Serializable foreignInstance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(foreignKey.getForeignKeyClass()), Long.valueOf(value));
                            Collection collection = (Collection) fieldManager.getFieldValue(instance, property.getName());
                            collection.remove(foreignInstance);
                            break;
                        }
                    }
                    break;
                case BASIC:
                    persistenceManager.getDynamicEntityDao().remove(instance);
                    break;
            }
        } catch (Exception e) {
            LOG.error("Problem removing entity", e);
            throw new ServiceException("Problem removing entity : " + e.getMessage(), e);
        }
    }

    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException {
        Entity[] payload;
        int totalRecords;
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        if (StringUtils.isEmpty(persistencePackage.getFetchTypeFullyQualifiedClassname())) {
            persistencePackage.setFetchTypeFullyQualifiedClassname(ceilingEntityFullyQualifiedClassname);
        }
        PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
        try {
            Class<?>[] entities = persistenceManager.getDynamicEntityDao().getAllPolymorphicEntitiesFromCeiling(Class.forName(ceilingEntityFullyQualifiedClassname));
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
            BaseCtoConverter ctoConverter = getCtoConverter(persistencePerspective, cto, ceilingEntityFullyQualifiedClassname, mergedProperties);
            PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, ceilingEntityFullyQualifiedClassname);
            boolean isArchivable = false;
            for (Class<?> entity : entities) {
                if (Status.class.isAssignableFrom(entity)) {
                    isArchivable = true;
                    break;
                }
            }
            if (isArchivable && !persistencePerspective.getShowArchivedFields()) {
                SimpleFilterCriterionProvider criterionProvider = new  SimpleFilterCriterionProvider(SimpleFilterCriterionProvider.FilterDataStrategy.NONE, 0) {
                    public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
                        return Restrictions.or(Restrictions.eq(targetPropertyName, 'N'), Restrictions.isNull(targetPropertyName));
                    }
                };
                FilterCriterion filterCriterion = new FilterCriterion(AssociationPath.ROOT, "archiveStatus.archived", criterionProvider);
                ((NestedPropertyCriteria) queryCriteria).add(filterCriterion);
            }
            List<Serializable> records = persistenceManager.getDynamicEntityDao().query(queryCriteria, Class.forName(persistencePackage.getFetchTypeFullyQualifiedClassname()));

            payload = getRecords(mergedProperties, records, null, null);
            totalRecords = getTotalRecords(persistencePackage, cto, ctoConverter);
        } catch (Exception e) {
            LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
            throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
        }

        return new DynamicResultSet(null, payload, totalRecords);
    }

    public void setPersistenceManager(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    @Override
    public PersistenceModule getCompatibleModule(OperationType operationType) {
        return ((InspectHelper) persistenceManager).getCompatibleModule(operationType);
    }
}
