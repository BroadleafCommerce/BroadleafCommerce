/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.util.dao;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang3.ArrayUtils;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;


public class DynamicDaoHelperImpl implements DynamicDaoHelper {
    
    public static final Object LOCK_OBJECT = new Object();
    public static final Map<Class<?>, Class<?>[]> POLYMORPHIC_ENTITY_CACHE = new LRUMap(100, 1000);
    public static final Map<Class<?>, Class<?>[]> POLYMORPHIC_ENTITY_CACHE_WO_EXCLUSIONS = new LRUMap(100, 1000);
    
    @Override
    public Class<?>[] getAllPolymorphicEntitiesFromCeiling(Class<?> ceilingClass, SessionFactory sessionFactory,
            boolean includeUnqualifiedPolymorphicEntities, boolean useCache) {
        Class<?>[] cache = null;
        synchronized(LOCK_OBJECT) {
            if (useCache) {
                if (includeUnqualifiedPolymorphicEntities) {
                    cache = POLYMORPHIC_ENTITY_CACHE.get(ceilingClass);
                } else {
                    cache = POLYMORPHIC_ENTITY_CACHE_WO_EXCLUSIONS.get(ceilingClass);
                }
            }
            if (cache == null) {
                List<Class<?>> entities = new ArrayList<Class<?>>();
                for (Object item : sessionFactory.getAllClassMetadata().values()) {
                    ClassMetadata metadata = (ClassMetadata) item;
                    Class<?> mappedClass = metadata.getMappedClass();
                    if (mappedClass != null && ceilingClass.isAssignableFrom(mappedClass)) {
                        entities.add(mappedClass);
                    }
                }
                Class<?>[] sortedEntities = sortEntities(ceilingClass, entities);

                List<Class<?>> filteredSortedEntities = new ArrayList<Class<?>>();

                for (int i = 0; i < sortedEntities.length; i++) {
                    Class<?> item = sortedEntities[i];
                    if (includeUnqualifiedPolymorphicEntities) {
                        filteredSortedEntities.add(sortedEntities[i]);
                    } else {
                        if (isExcludeClassFromPolymorphism(item)) {
                            continue;
                        } else {
                            filteredSortedEntities.add(sortedEntities[i]);
                        }
                    }
                }

                Class<?>[] filteredEntities = new Class<?>[filteredSortedEntities.size()];
                filteredEntities = filteredSortedEntities.toArray(filteredEntities);
                cache = filteredEntities;
                if (includeUnqualifiedPolymorphicEntities) {
                    POLYMORPHIC_ENTITY_CACHE.put(ceilingClass, filteredEntities);
                } else {
                    POLYMORPHIC_ENTITY_CACHE_WO_EXCLUSIONS.put(ceilingClass, filteredEntities);
                }
            }
        }

        return cache;
    }
    
    @Override
    public Class<?>[] sortEntities(Class<?> ceilingClass, List<Class<?>> entities) {
        /*
         * Sort entities with the most derived appearing first
         */
        Class<?>[] sortedEntities = new Class<?>[entities.size()];
        List<Class<?>> stageItems = new ArrayList<Class<?>>();
        stageItems.add(ceilingClass);
        int j = 0;
        while (j < sortedEntities.length) {
            List<Class<?>> newStageItems = new ArrayList<Class<?>>();
            boolean topLevelClassFound = false;
            for (Class<?> stageItem : stageItems) {
                Iterator<Class<?>> itr = entities.iterator();
                while(itr.hasNext()) {
                    Class<?> entity = itr.next();
                    checkitem: {
                        if (ArrayUtils.contains(entity.getInterfaces(), stageItem) || entity.equals(stageItem)) {
                            topLevelClassFound = true;
                            break checkitem;
                        }

                        if (topLevelClassFound) {
                            continue;
                        }

                        if (entity.getSuperclass().equals(stageItem) && j > 0) {
                            break checkitem;
                        }

                        continue;
                    }
                    sortedEntities[j] = entity;
                    itr.remove();
                    j++;
                    newStageItems.add(entity);
                }
            }
            if (newStageItems.isEmpty()) {
                throw new IllegalArgumentException("There was a gap in the inheritance hierarchy for (" + ceilingClass.getName() + ")");
            }
            stageItems = newStageItems;
        }
        ArrayUtils.reverse(sortedEntities);
        return sortedEntities;
    }
    
    @Override
    public boolean isExcludeClassFromPolymorphism(Class<?> clazz) {
        //We filter out abstract classes because they can't be instantiated.
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return true;
        }

        //We filter out classes that are marked to exclude from polymorphism
        AdminPresentationClass adminPresentationClass = clazz.getAnnotation(AdminPresentationClass.class);
        if (adminPresentationClass == null) {
            return false;
        } else if (adminPresentationClass.excludeFromPolymorphism()) {
            return true;
        }
        return false;
    }
    
    @Override
    public Map<String, Object> getIdMetadata(Class<?> entityClass, HibernateEntityManager entityManager) {
        Map<String, Object> response = new HashMap<String, Object>();
        SessionFactory sessionFactory = entityManager.getSession().getSessionFactory();
        
        ClassMetadata metadata = sessionFactory.getClassMetadata(entityClass);
        if (metadata == null) {
            return null;
        }
        
        String idProperty = metadata.getIdentifierPropertyName();
        response.put("name", idProperty);
        Type idType = metadata.getIdentifierType();
        response.put("type", idType);

        return response;
    }

    @Override
    public List<String> getPropertyNames(Class<?> entityClass, HibernateEntityManager entityManager) {
        ClassMetadata metadata = getSessionFactory(entityManager).getClassMetadata(entityClass);
        List<String> propertyNames = new ArrayList<String>();
        Collections.addAll(propertyNames, metadata.getPropertyNames());
        return propertyNames;
    }

    @Override
    public List<Type> getPropertyTypes(Class<?> entityClass, HibernateEntityManager entityManager) {
        ClassMetadata metadata = getSessionFactory(entityManager).getClassMetadata(entityClass);
        List<Type> propertyTypes = new ArrayList<Type>();
        Collections.addAll(propertyTypes, metadata.getPropertyTypes());
        return propertyTypes;
    }

    @Override
    public SessionFactory getSessionFactory(HibernateEntityManager entityManager) {
        return entityManager.getSession().getSessionFactory();
    }

    @Override
    public Serializable getIdentifier(Object entity, EntityManager em) {
        if (entity.getClass().getAnnotation(Entity.class) != null) {
            Field idField = getIdField(entity.getClass(), em);
            try {
                return (Serializable) idField.get(entity);
            } catch (IllegalAccessException e) {
                throw ExceptionHelper.refineException(e);
            }
        }
        return null;
    }

    @Override
    public Field getIdField(Class<?> clazz, EntityManager em) {
        ClassMetadata metadata = em.unwrap(Session.class).getSessionFactory().getClassMetadata(clazz);
        Field idField = ReflectionUtils.findField(clazz, metadata.getIdentifierPropertyName());
        idField.setAccessible(true);
        return idField;
    }
}
