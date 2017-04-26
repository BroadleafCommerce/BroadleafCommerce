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
package org.broadleafcommerce.common.persistence;

/**
 * Utility class for working with proxied entities.
 *
 * The {@link DefaultPostLoaderDao} in core delegates functionally to
 * {@link javax.persistence.EntityManager}, while more interesting
 * functionality is provided by the enterprise version.
 *
 * @see DefaultPostLoaderDao
 * @author Nathan Moore (nathanmoore).
 */
public interface PostLoaderDao {
    /**
     * Find the entity by primary key and class, and, if found in
     * the persistence context, return the deproxied version.
     *
     * @param clazz entity class
     * @param id primary key
     * @return deproxied entity or null if not found
     */
    <T> T find(Class<T> clazz, Object id);

    /**
     * If within the context of a sandbox, return the sandbox entity by primary key and class.
     *
     * This purposefully uses the Entity Manager in order to trigger the hibernate filters.
     *
     * @param clazz
     * @param id
     * @param <T>
     * @return
     */
    <T> T findSandboxEntity(Class<T> clazz, Object id);

}
