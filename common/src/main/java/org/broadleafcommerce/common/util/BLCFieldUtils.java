/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.common.util;

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

/**
 * Utility class used primarily for retrieving fields on domain classes. Retrieval will include searching in the inheritance
 * hierarchy for the provided class. Retrieval will also honor traversal of multiple fields in the field name (delimited with
 * a period).
 *
 * @author Jeff Fischer
 */
public class BLCFieldUtils {

    private static final Log LOG = LogFactory.getLog(BLCFieldUtils.class);

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
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException nsf) {
            // Try superclass
            if (clazz.getSuperclass() != null) {
                return getSingleField(clazz.getSuperclass(), fieldName);
            }

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
                LOG.debug(String.format("Unable to find the field (%s) anywhere in the inheritance hierarchy for (%s)", propertyName, clazz.getName()));
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
            LOG.warn("Found the property (" + token + ") in more than one class of an inheritance hierarchy. " +
                    "This may lead to unwanted behavior, as the system does not know which class was intended. Do not " +
                    "use the same property name in different levels of the inheritance hierarchy. Defaulting to the " +
                    "first class found (" + matchedClasses.get(0).getName() + ")");
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
