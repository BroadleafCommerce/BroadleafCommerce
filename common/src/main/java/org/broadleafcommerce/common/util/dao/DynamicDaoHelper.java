
/*
 * Copyright 2008-2013 the original author or authors.
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

package org.broadleafcommerce.common.util.dao;


import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

/**
 * Provides utility methods for interacting with dynamic entities
 *
 * @author Andre Azzolini (apazzolini)
 * @author Jeff Fischer
 */
public interface DynamicDaoHelper {

    /**
     * Retrieve information about the id property for the entity class and the data type of that property. This will
     * return a Map containing two members. The first will be keyed with the String "name" and will be the name of
     * the id property on the entity class. The second will be keyed with the String "type" and will be the Hibernate
     * {@link Type} instance for the property (e.g. {@link org.hibernate.type.LongType}).
     *
     * @param entityClass
     * @param entityManager
     * @return
     */
    Map<String, Object> getIdMetadata(Class<?> entityClass, EntityManager entityManager);

    /**
     * Retrieve the list of property names known to Hibernate for the entity class.
     *
     * @param entityClass
     * @param entityManager
     * @return
     */
    List<String> getPropertyNames(Class<?> entityClass);

    /**
     * Retrieve the list of property types ({@link Type} known to Hibernate for the entity class.
     *
     * @param entityClass
     * @param entityManager
     * @return
     */
    List<Type> getPropertyTypes(Class<?> entityClass);

    /**
     * Get all the polymorphic types known to Hibernate for the ceiling class provided. The ceiling class should be an
     * entity class registered in Hibernate (or a specific interface unique to that entity class). The returned array
     * is sorted with the most derived entities appearing first in the list.
     *
     * @param ceilingClass
     * @param includeUnqualifiedPolymorphicEntities Some entities may be excluded from polymorphism (Abstract class and those marked with {@link AdminPresentationClass#excludeFromPolymorphism()}). Override that exlusion behavior.
     * @param useCache Cache the polymorphic types discovered for the ceilingClass.
     * @return The list of Hibernate registered entities that derive from the ceilingClass (including the ceilingClass)
     */
    Class<?>[] getAllPolymorphicEntitiesFromCeiling(Class<?> ceilingClass, boolean includeUnqualifiedPolymorphicEntities, boolean useCache);

    /**
     * Sort a list of polymorphic types with the most derived appearing first.
     *
     * @param ceilingClass
     * @param entities
     * @return
     */
    Class<?>[] sortEntities(Class<?> ceilingClass, List<Class<?>> entities);

    /**
     * Discover is a class should be excluded from a polymorphic list. Exclusion is generally enforced if the class is
     * abstract or if the class is marked with {@link AdminPresentationClass#excludeFromPolymorphism()}.
     *
     * @param clazz
     * @return
     */
    boolean isExcludeClassFromPolymorphism(Class<?> clazz);

    /**
     * The value of the Hibernate registered identifier property for the entity instance.
     *
     * @param entity
     * @return
     */
    Serializable getIdentifier(Object entity);

    /**
     * The Field that represents the Hibernate registered identifier property for the entity class.
     *
     * @param clazz
     * @return
     */
    Field getIdField(Class<?> clazz);

    /**
     * Retrieve a complete polymorphic type list for an entity class, even if the entity class is not the ceiling class (or
     * root of the hierarchy). This allows you to possibly provide a mid-level entity class and get back a list of entity
     * classes both above and below the testClass. The type list passed back is ordered with the ceiling class appearing
     * first and the most derived classes appearing last.
     *
     * @param testClass An entity class to look for polymorphic types both above and below
     * @param includeUnqualifiedPolymorphicEntities Some entities may be excluded from polymorphism (Abstract class and those marked with {@link AdminPresentationClass#excludeFromPolymorphism()}). Override that exlusion behavior.
     * @param useCache Cache the polymorphic types discovered for the ceilingClass.
     * @return The list of Hibernate registered entities that appear above and below the testClass in an entity inheritance hierarchy
     */
    Class<?>[] getUpDownInheritance(Class<?> testClass, boolean includeUnqualifiedPolymorphicEntities, boolean useCache);

}