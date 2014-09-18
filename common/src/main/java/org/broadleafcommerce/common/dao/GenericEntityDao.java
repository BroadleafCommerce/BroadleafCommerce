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

package org.broadleafcommerce.common.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;

import java.util.List;

public interface GenericEntityDao {

    /**
     * Finds a generic entity by a classname and id
     * 
     * @param className
     * @param id
     * @return the entity
     */
    public Object readGenericEntity(Class<?> clazz, Object id);

    /**
     * For a given className, finds the parent implementation class as defined in the {@link EntityConfiguration}
     * 
     * @param className
     * @return the impl class object
     */
    public Class<?> getImplClass(String className);

    /**
     * Saves a generic entity
     * 
     * @param object
     * @return the persisted version of the entity
     */
    public <T> T save(T object);

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

}
