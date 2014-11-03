/*
 * #%L
 * BroadleafCommerce Profile Web
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

package org.broadleafcommerce.common.service;

import java.io.Serializable;
import java.util.List;

/**
 * CRUD methods for generic entities
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface GenericEntityService {

    /**
     * Finds a generic entity by a classname and id
     * 
     * @param className
     * @param id
     * @return the entity
     */
    public Object readGenericEntity(String className, Object id);

    <T> T readGenericEntity(Class<T> clazz, Object id);

    /**
     * Saves a generic entity
     * 
     * @param object
     * @return the persisted version of the entity
     */
    public <T> T save(T object);

    /**
     * Persist the new object
     *
     * @param object
     */
    void persist(Object object);

    /**
     * Finds how many of the given entity class are persisted
     * 
     * @param clazz
     * @return the count of the generic entity
     */
    public <T> Long readCountGenericEntity(Class<T> clazz);

    /**
     * Finds all generic entities for a given classname, with pagination options.
     * 
     * @param clazz
     * @param limit
     * @param offset
     * @return the entities
     */
    public <T> List<T> readAllGenericEntity(Class<T> clazz, int limit, int offset);

    List<Long> readAllGenericEntityId(Class<?> clazz);

    /**
     * Retrieve the identifier from the Hibernate entity (the entity must reside in the current session)
     *
     * @param entity
     * @return
     */
    Serializable getIdentifier(Object entity);

    /**
     * Flush changes to the persistence store
     */
    void flush();

    void clearAutoFlushMode();

    void enableAutoFlushMode();

    /**
     * Clear level 1 cache
     */
    void clear();

    /**
     * Whether or not the current hibernate session (level 1) contains the object
     *
     * @param object
     * @return
     */
    boolean sessionContains(Object object);

    /**
     * Whether or not this object is an {@link javax.persistence.Entity} and whether or not it already has an id assigned
     * @param object
     * @return
     */
    boolean idAssigned(Object object);

    /**
     * Return the ceiling implementation class for an entity
     *
     * @param className
     * @return
     */
    Class<?> getCeilingImplClass(String className);
}
