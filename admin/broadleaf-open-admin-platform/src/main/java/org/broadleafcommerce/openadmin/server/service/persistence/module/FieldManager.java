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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.util.BLCFieldUtils;
import org.broadleafcommerce.common.util.HibernateUtils;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManagerFactory;
import org.broadleafcommerce.openadmin.server.service.persistence.TargetModeType;
import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManager;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;

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
        PersistenceManager persistenceManager = getPersistenceManager();
        SessionFactory sessionFactory = persistenceManager.getDynamicEntityDao().getDynamicDaoHelper().
                getSessionFactory((HibernateEntityManager) persistenceManager.getDynamicEntityDao().getStandardEntityManager());
        BLCFieldUtils fieldUtils = new BLCFieldUtils(sessionFactory, true, persistenceManager.getDynamicEntityDao().useCache(),
                persistenceManager.getDynamicEntityDao().getEjb3ConfigurationDao(), entityConfiguration,
                persistenceManager.getDynamicEntityDao().getDynamicDaoHelper());
        return fieldUtils.getField(clazz, fieldName);
    }

    public Object getFieldValue(Object bean, String fieldName) throws IllegalAccessException, FieldNotAvailableException {
        StringTokenizer tokens = new StringTokenizer(fieldName, ".");
        Class<?> componentClass = bean.getClass();
        Field field;
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
                if (value != null && mapKey != null) {
                    value = ((Map) value).get(mapKey);
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
                    Map map = (Map) field.get(value);
                    if (newValue == null) {
                        map.remove(mapKey);
                    } else {
                        map.put(mapKey, newValue);
                    }
                } else {
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
                        PersistenceManager persistenceManager = getPersistenceManager();
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
                            LOG.info("Unable to find a reference to ("+field.getType().getName()+") in the EntityConfigurationManager. " +
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
            PersistenceManager persistenceManager = getPersistenceManager();
            Class<?>[] entities = persistenceManager.getUpDownInheritance(field.getType());
            if (!ArrayUtils.isEmpty(entities)) {
                response = entities[entities.length-1];
                LOG.info("Unable to find a reference to ("+field.getType().getName()+") in the EntityConfigurationManager. " +
                        "Using the most extended form of this class identified as ("+entities[0].getName()+")");
            } else {
                //Just use the field type
                response = field.getType();
                LOG.info("Unable to find a reference to ("+field.getType().getName()+") in the EntityConfigurationManager. " +
                        "Using the type of this class.");
            }
        }
        return response;
    }
    
    public Map<String, Serializable> persistMiddleEntities() throws InstantiationException, IllegalAccessException {
        Map<String, Serializable> persistedEntities = new HashMap<String, Serializable>();
        
        Collections.sort(middleFields);
        for (SortableValue val : middleFields) {
            Serializable s = entityManager.merge(val.entity);
            persistedEntities.put(val.getContainingPropertyName(), s);
            setFieldValue(val.getBean(), val.getContainingPropertyName(), s);
        }
        
        return persistedEntities;
    }

    public EntityConfiguration getEntityConfiguration() {
        return entityConfiguration;
    }

    protected PersistenceManager getPersistenceManager() {
        PersistenceManager persistenceManager;
        try {
            persistenceManager = PersistenceManagerFactory.getPersistenceManager();
        } catch (IllegalStateException e) {
            persistenceManager = PersistenceManagerFactory.getPersistenceManager(TargetModeType.SANDBOX);
        }
        return persistenceManager;
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
