/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.util;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelper;
import org.broadleafcommerce.common.util.dao.EJB3ConfigurationDao;
import org.hibernate.SessionFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class used primarily for retrieving fields on domain classes. Retrieval will include searching in the inheritance
 * hierarchy for the provided class. Retrieval will also honor traversal of multiple fields in the field name (delimited with
 * a period).
 *
 * @author Jeff Fischer
 */
public class BLCFieldUtils {

    private static final Log LOG = LogFactory.getLog(BLCFieldUtils.class);
    public static final Map<String,Object> FIELD_CACHE = new LRUMap<>(100000);
    public static final Object NULL_FIELD = new Object();

    protected SessionFactory sessionFactory;
    protected boolean includeUnqualifiedPolymorphicEntities;
    protected boolean useCache;
    protected EJB3ConfigurationDao ejb3ConfigurationDao;
    protected EntityConfiguration entityConfiguration;
    protected DynamicDaoHelper helper;

    /**
     * Initialize the utility with required resources
     *
     * @param sessionFactory provides metadata about a domain class
     * @param includeUnqualifiedPolymorphicEntities include polymorphic variations that were excluded with {@link org.broadleafcommerce.common.presentation.AdminPresentationClass#excludeFromPolymorphism()}
     * @param useCache use the polymorphic type list cache in {@link org.broadleafcommerce.common.util.dao.DynamicDaoHelperImpl}
     * @param ejb3ConfigurationDao provides additional metadata about a domain class
     * @param entityConfiguration contains any explicitly defined entity types for the system
     * @param helper helper class for retrieving polymorphic types for a ceiling domain class
     */
    public BLCFieldUtils(SessionFactory sessionFactory, boolean includeUnqualifiedPolymorphicEntities,
                         boolean useCache, EJB3ConfigurationDao ejb3ConfigurationDao,
                         EntityConfiguration entityConfiguration, DynamicDaoHelper helper) {
        this.sessionFactory = sessionFactory;
        this.includeUnqualifiedPolymorphicEntities = includeUnqualifiedPolymorphicEntities;
        this.useCache = useCache;
        this.ejb3ConfigurationDao = ejb3ConfigurationDao;
        this.entityConfiguration = entityConfiguration;
        this.helper = helper;
    }

    /**
     * Retrieve the field for the class. This method will also look in superclasses.
     *
     * @param clazz
     * @param fieldName
     * @return
     * @throws IllegalStateException
     */
    public static Field getSingleField(Class<?> clazz, String fieldName) throws IllegalStateException {
        String key = clazz.getName() + "#" + fieldName;
        Object response;
        synchronized (FIELD_CACHE) {
            response = FIELD_CACHE.get(key);
        }
        if (response == null) {
            Field found;
            try {
                found = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException nsf) {
                // Try superclass
                if (clazz.getSuperclass() != null) {
                    found = getSingleField(clazz.getSuperclass(), fieldName);
                } else {
                    found = null;
                }
            }
            if (found == null) {
                response = NULL_FIELD;
            } else {
                response = found;
            }
            synchronized (FIELD_CACHE) {
                FIELD_CACHE.put(key, response);
            }
        }
        if (response instanceof Field) {
            return (Field) response;
        } else {
            return null;
        }
    }

    /**
     * Retrieve the field for the class. This method will look in the entire inheritance hierarchy for the class (if applicable)
     * and will also honor multiple field traversals in the fieldName using a period delimeter.
     *
     * @param clazz
     * @param fieldName
     * @return
     * @throws IllegalStateException
     */
    public Field getField(Class<?> clazz, String fieldName) throws IllegalStateException {
        String[] tokens = fieldName.split("\\.");
        Field field = null;

        for (int j=0;j<tokens.length;j++) {
            String propertyName = tokens[j];
            Class<?>[] myEntities = helper.getUpDownInheritance(clazz, sessionFactory, includeUnqualifiedPolymorphicEntities, useCache, ejb3ConfigurationDao);
            Class<?> myClass;
            if (ArrayUtils.isEmpty(myEntities)) {
                myClass = clazz;
            } else {
                myClass = getClassForField(helper, propertyName, null, myEntities);
            }
            if (myClass == null) {
                String message = String.format("Unable to find the field (%s) anywhere in the inheritance hierarchy for (%s)", StringUtil.sanitize(propertyName), StringUtil.sanitize(clazz.getName()));
                LOG.debug(message);
                return null;
            }
            field = getSingleField(myClass, propertyName);
            if (field != null && j < tokens.length - 1) {
                Class<?>[] fieldEntities = helper.getUpDownInheritance(field.getType(), sessionFactory, includeUnqualifiedPolymorphicEntities, useCache, ejb3ConfigurationDao);
                if (!ArrayUtils.isEmpty(fieldEntities)) {
                    clazz = getClassForField(helper, tokens[j + 1], field, fieldEntities);
                    if (clazz == null) {
                        return null;
                    }
                } else {
                    //may be an embedded class - try the class directly
                    clazz = field.getType();
                }
            } else {
                break;
            }
        }

        if (field != null) {
            field.setAccessible(true);
        }
        return field;
    }

    protected Class<?> getClassForField(DynamicDaoHelper helper, String token, Field field, Class<?>[] entities) {
        Class<?> clazz;
        List<Class<?>> matchedClasses = new ArrayList<Class<?>>();
        for (Class<?> entity : entities) {
            Field peekAheadField = null;
            try {
                peekAheadField = entity.getDeclaredField(token);
            } catch (NoSuchFieldException nsf) {
                //do nothing
            }
            if (peekAheadField != null) {
                matchedClasses.add(entity);
            }
        }
        if (matchedClasses.size() > 1) {
            String message = "Found the property (" + StringUtil.sanitize(token) + ") in more than one class of an inheritance hierarchy. " +
                    "This may lead to unwanted behavior, as the system does not know which class was intended. Do not " +
                    "use the same property name in different levels of the inheritance hierarchy. Defaulting to the " +
                    "first class found (" + StringUtil.sanitize(matchedClasses.get(0).getName()) + ")";
            LOG.warn(message);
        }
        if (matchedClasses.isEmpty()) {
            //probably an artificial field (i.e. passwordConfirm on AdminUserImpl)
            return null;
        }
        Class<?> myClass = field != null?field.getType():entities[0];
        if (getSingleField(matchedClasses.get(0), token) != null) {
            clazz = matchedClasses.get(0);
            Class<?>[] entities2 = helper.getUpDownInheritance(clazz, sessionFactory, includeUnqualifiedPolymorphicEntities, useCache, ejb3ConfigurationDao);
            if (!ArrayUtils.isEmpty(entities2) && matchedClasses.size() == 1 && clazz.isInterface()) {
                try {
                    clazz = entityConfiguration.lookupEntityClass(myClass.getName());
                } catch (Exception e) {
                    // Do nothing - we'll use the matchedClass
                }
            }
        } else {
            clazz = myClass;
        }
        return clazz;
    }
}
