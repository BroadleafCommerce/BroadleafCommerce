/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.openadmin.server.service.persistence.module;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.util.BLCFieldUtils;
import org.broadleafcommerce.common.util.HibernateUtils;
import org.broadleafcommerce.common.value.ValueAssignable;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManagerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;

/**
 *
 * @author jfischer
 *
 */
public class FieldManager {

    private static final Log LOG = LogFactory.getLog(FieldManager.class);

    public static final String MAPFIELDSEPARATOR = "---";

    protected EntityConfiguration entityConfiguration;
    protected EntityManager entityManager;
    protected List<SortableValue> middleFields = new ArrayList<SortableValue>(5);

    public FieldManager(EntityConfiguration entityConfiguration, EntityManager entityManager) {
        this.entityConfiguration = entityConfiguration;
        this.entityManager = entityManager;
    }

    public static Field getSingleField(Class<?> clazz, String fieldName) throws IllegalStateException {
        return BLCFieldUtils.getSingleField(clazz, fieldName);
    }

    public Field getField(Class<?> clazz, String fieldName) throws IllegalStateException {
        DynamicEntityDao dynamicEntityDao = getPersistenceManager(clazz).getDynamicEntityDao();
        BLCFieldUtils fieldUtils = new BLCFieldUtils(true, dynamicEntityDao.useCache(),
                entityConfiguration, dynamicEntityDao.getDynamicDaoHelper());
        return fieldUtils.getField(clazz, fieldName);
    }

    public Object getFieldValue(Object bean, String fieldName) throws IllegalAccessException, FieldNotAvailableException {
        StringTokenizer tokens = new StringTokenizer(fieldName, ".");
        Class<?> componentClass = bean.getClass();
        Field field = null;
        Object value = HibernateUtils.deproxy(bean);

        while (tokens.hasMoreTokens()) {
            String fieldNamePart = tokens.nextToken();
            String mapKey = null;
            if (fieldNamePart.contains(FieldManager.MAPFIELDSEPARATOR)) {
                mapKey = fieldNamePart.substring(fieldNamePart.indexOf(FieldManager.MAPFIELDSEPARATOR) + FieldManager.MAPFIELDSEPARATOR.length(), fieldNamePart.length());
                fieldNamePart = fieldNamePart.substring(0, fieldNamePart.indexOf(FieldManager.MAPFIELDSEPARATOR));
            }
            field = getSingleField(componentClass, fieldNamePart);

            if (field != null) {
                field.setAccessible(true);
                value = field.get(value);

                if (mapKey != null) {
                    value = handleMapFieldExtraction(bean, fieldName, componentClass, value, fieldNamePart, mapKey);
                }

                if (value != null) {
                    componentClass = value.getClass();
                } else {
                    break;
                }
            } else {
                throw new FieldNotAvailableException("Unable to find field (" + fieldNamePart + ") on the class (" + componentClass + ")");
            }
        }

        FieldModifierManager modifierManager = FieldModifierManager.getFieldModifierManager();
        if (modifierManager != null) {
            value = modifierManager.getModifiedReadValue(field, value, entityManager);
        }
        return value;

    }

    public Object setFieldValue(Object bean, String fieldName, Object newValue) throws IllegalAccessException, InstantiationException {
        StringTokenizer tokens = new StringTokenizer(fieldName, ".");
        Class<?> componentClass = bean.getClass();
        Field field;
        bean = HibernateUtils.deproxy(bean);
        Object value = bean;

        int count = tokens.countTokens();
        int j=0;
        StringBuilder sb = new StringBuilder();
        while (tokens.hasMoreTokens()) {
            String fieldNamePart = tokens.nextToken();
            sb.append(fieldNamePart);
            String mapKey = null;
            if (fieldNamePart.contains(FieldManager.MAPFIELDSEPARATOR)) {
                mapKey = fieldNamePart.substring(fieldNamePart.indexOf(FieldManager.MAPFIELDSEPARATOR) + FieldManager.MAPFIELDSEPARATOR.length(), fieldNamePart.length());
                fieldNamePart = fieldNamePart.substring(0, fieldNamePart.indexOf(FieldManager.MAPFIELDSEPARATOR));
            }

            field = getSingleField(componentClass, fieldNamePart);
            field.setAccessible(true);
            if (j == count - 1) {
                if (mapKey != null) {
                    handleMapFieldPopulation(bean, fieldName, newValue, componentClass, field, value, fieldNamePart, mapKey);
                } else {
                    FieldModifierManager modifierManager = FieldModifierManager.getFieldModifierManager();
                    if (modifierManager != null) {
                        newValue = modifierManager.getModifiedWriteValue(field, value, newValue, entityManager);
                    }
                    field.set(value, newValue);
                }
            } else {
                Object myValue = field.get(value);
                if (myValue != null) {
                    componentClass = myValue.getClass();
                    value = myValue;
                } else {
                    //consult the entity configuration manager to see if there is a user
                    //configured entity for this class
                    try {
                        Object newEntity = entityConfiguration.createEntityInstance(field.getType().getName());
                        SortableValue val = new SortableValue(bean, (Serializable) newEntity, j, sb.toString());
                        middleFields.add(val);
                        field.set(value, newEntity);
                        componentClass = newEntity.getClass();
                        value = newEntity;
                    } catch (Exception e) {
                        //Use the most extended type based on the field type
                        PersistenceManager persistenceManager = getPersistenceManager(field.getType());
                        Class<?>[] entities = persistenceManager.getUpDownInheritance(field.getType());
                        if (!ArrayUtils.isEmpty(entities)) {
                            Object newEntity = entities[entities.length-1].newInstance();
                            SortableValue val = new SortableValue(bean, (Serializable) newEntity, j, sb.toString());
                            middleFields.add(val);
                            field.set(value, newEntity);
                            componentClass = newEntity.getClass();
                            value = newEntity;
                            LOG.info("Unable to find a reference to ("+field.getType().getName()+") in the EntityConfigurationManager. " +
                                    "Using the most extended form of this class identified as ("+entities[0].getName()+")");
                        } else {
                            //Just use the field type
                            Object newEntity = field.getType().newInstance();
                            field.set(value, newEntity);
                            componentClass = newEntity.getClass();
                            value = newEntity;
                            LOG.debug("Unable to find a reference to ("+field.getType().getName()+") in the EntityConfigurationManager. " +
                                    "Using the type of this class.");
                        }
                    }
                }
            }
            sb.append(".");
            j++;
        }

        return value;

    }

    public Class<?> getFieldType(Field field) {
        //consult the entity configuration manager to see if there is a user
        //configured entity for this class
        Class<?> response;
        try {
            response = entityConfiguration.lookupEntityClass(field.getType().getName());
        } catch (Exception e) {
            //Use the most extended type based on the field type
            PersistenceManager persistenceManager = getPersistenceManager(field.getType());
            Class<?>[] entities = persistenceManager.getUpDownInheritance(field.getType());
            if (!ArrayUtils.isEmpty(entities)) {
                response = entities[entities.length-1];
                LOG.info("Unable to find a reference to ("+field.getType().getName()+") in the EntityConfigurationManager. " +
                        "Using the most extended form of this class identified as ("+entities[0].getName()+")");
            } else {
                //Just use the field type
                response = field.getType();
                LOG.debug("Unable to find a reference to ("+field.getType().getName()+") in the EntityConfigurationManager. " +
                        "Using the type of this class.");
            }
        }
        return response;
    }

    public Map<String, Serializable> persistMiddleEntities() throws InstantiationException, IllegalAccessException {
        Map<String, Serializable> persistedEntities = new HashMap<>();

        Collections.sort(middleFields);

        for (SortableValue val : middleFields.toArray(new SortableValue[0])) {
            Serializable s = entityManager.merge(val.entity);
            persistedEntities.put(val.getContainingPropertyName(), s);
            setFieldValue(val.getBean(), val.getContainingPropertyName(), s);
        }

        return persistedEntities;
    }

    public EntityConfiguration getEntityConfiguration() {
        return entityConfiguration;
    }

    protected PersistenceManager getPersistenceManager(Class entityClass) {
        if (!isPersistentClass(entityClass)) {
            return PersistenceManagerFactory.getDefaultPersistenceManager();
        }

        try {
            return PersistenceManagerFactory.getPersistenceManager(entityClass);
        } catch (RuntimeException e) {
            return PersistenceManagerFactory.getDefaultPersistenceManager();
        }
    }

    protected boolean isPersistentClass(Class entityClass) {
        if (entityManager != null) {
            Set<EntityType<?>> managedEntities = entityManager.getMetamodel().getEntities();
            for (EntityType managedEntity : managedEntities) {
                if (managedEntity.getJavaType().equals(entityClass)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected Object handleMapFieldExtraction(Object bean, String fieldName, Class<?> componentClass, Object value,
                                              String fieldNamePart, String mapKey) throws IllegalAccessException, FieldNotAvailableException {
        String fieldNamePrefix = fieldName.substring(0, fieldName.indexOf(fieldNamePart));
        String multiValueMapFullFieldName = fieldNamePrefix + "multiValue" + fieldNamePart.substring(0, 1).toUpperCase() + fieldNamePart.substring(1);
        String standardMapFullFieldName = null;
        if (!StringUtils.isEmpty(fieldNamePrefix)) {
            standardMapFullFieldName = fieldNamePrefix + fieldNamePart.substring(0, 1).toUpperCase() + fieldNamePart.substring(1);
        }

        if (value instanceof List) {
            try {
                value = PropertyUtils.getProperty(bean, multiValueMapFullFieldName);
            } catch (InvocationTargetException | NoSuchMethodException e) {
                if (!StringUtils.isEmpty(standardMapFullFieldName)) {
                    try {
                        value = PropertyUtils.getProperty(bean, standardMapFullFieldName);
                    } catch (InvocationTargetException | NoSuchMethodException n) {
                        throw new FieldNotAvailableException("Unable to find field (" + fieldNamePart + ") on the class (" + componentClass + ")");

                    }
                }
            }
        }

        if (value != null && !(value instanceof Map)) {
            List<String> names = Arrays.asList(fieldNamePart, multiValueMapFullFieldName);
            if (!StringUtils.isEmpty(standardMapFullFieldName)) {
                names.add(standardMapFullFieldName);
            }
            String combined = StringUtils.join(names, ",");
            throw new IllegalArgumentException(String.format("A field containing a map field separator was requested " +
                    "(%s), but no Map type field or method returning a Map was found using the following tests (%s)",
                    fieldName, combined));
        }

        if (value != null) {
            value = ((Map) value).get(mapKey);
            // This handles gathering the first element of a list that came from a MultiValue Map
            // used for single-value CustomFields
            if (value instanceof List && !((List) value).isEmpty()) {
                value = ((List) value).get(0);
            }
        }
        return value;
    }

    protected void handleMapFieldPopulation(Object bean, String fieldName, Object newValue, Class<?> componentClass,
                                            Field field, Object value, String fieldNamePart, String mapKey) throws IllegalAccessException {
        String fieldNamePrefix = fieldName.substring(0, fieldName.indexOf(fieldNamePart));
        String multiValueMapFullFieldName = fieldNamePrefix + "multiValue" + fieldNamePart.substring(0, 1).toUpperCase() + fieldNamePart.substring(1);
        String standardMapFullFieldName = null;
        if (!StringUtils.isEmpty(fieldNamePrefix)) {
            standardMapFullFieldName = fieldNamePrefix + fieldNamePart.substring(0, 1).toUpperCase() + fieldNamePart.substring(1);
        }

        Map<String, Object> map = null;
        Object fieldValue = field.get(value);
        if (fieldValue instanceof List) {
            try {
                map = (Map<String, Object>) PropertyUtils.getProperty(bean, multiValueMapFullFieldName);
            } catch (InvocationTargetException |NoSuchMethodException e) {
                if (!StringUtils.isEmpty(standardMapFullFieldName)) {
                    try {
                        map = (Map<String, Object>) PropertyUtils.getProperty(bean, standardMapFullFieldName);
                    } catch (InvocationTargetException | NoSuchMethodException n) {
                        LOG.info("Unable to find a reference to (" + field.getType().getName() + ") in the EntityConfigurationManager. " +
                                "Using the type of this class.");
                        throw new IllegalAccessException("Unable to save field (" + fieldNamePart + ") on" +
                                " the class (" + componentClass + ")");
                    }
                }
            }
        } else {
            map = (Map<String, Object>) fieldValue;
        }
        if (fieldValue != null && map == null) {
            List<String> names = Arrays.asList(fieldNamePart, multiValueMapFullFieldName);
            if (!StringUtils.isEmpty(standardMapFullFieldName)) {
                names.add(standardMapFullFieldName);
            }
            String combined = StringUtils.join(names, ",");
            throw new IllegalArgumentException(String.format("A field containing a map field separator was requested " +
                    "(%s), but no Map type field or method returning a Map was found using the following tests (%s)",
                    fieldName, combined));
        }
        if (newValue == null) {
            Object currentValue = map.get(mapKey);
            if (currentValue != null && currentValue instanceof ValueAssignable) {
                ((ValueAssignable) currentValue).setValue(null);
            } else {
                map.remove(mapKey);
            }
        } else {
            map.put(mapKey, newValue);
        }
    }

    public void clearMiddleFields() {
        middleFields.clear();
    }

    private class SortableValue implements Comparable<SortableValue> {

        private Integer pos;
        private Serializable entity;
        private Class<?> entityClass;
        private String containingPropertyName;
        private Object bean;

        public SortableValue(Object bean, Serializable entity, Integer pos, String containingPropertyName) {
            this.bean = bean;
            this.entity = entity;
            this.pos = pos;
            this.entityClass = entity.getClass();
            this.containingPropertyName = containingPropertyName;
        }

        @Override
        public int compareTo(SortableValue o) {
            return pos.compareTo(o.pos) * -1;
        }

        public String getContainingPropertyName() {
            return containingPropertyName;
        }

        private Object getBean() {
            return bean;
        }

        @Override
        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + (entityClass == null ? 0 : entityClass.hashCode());
            result = prime * result + (pos == null ? 0 : pos.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!getClass().isAssignableFrom(obj.getClass()))
                return false;
            SortableValue other = (SortableValue) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (entityClass == null) {
                if (other.entityClass != null)
                    return false;
            } else if (!entityClass.equals(other.entityClass))
                return false;
            if (pos == null) {
                if (other.pos != null)
                    return false;
            } else if (!pos.equals(other.pos))
                return false;
            return true;
        }

        private FieldManager getOuterType() {
            return FieldManager.this;
        }

    }

}
