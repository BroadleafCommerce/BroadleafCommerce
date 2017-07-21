/*
 * #%L
 * broadleaf-multitenant-singleschema
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.common.cache;

import org.broadleafcommerce.common.extension.StandardCacheItem;

import java.io.Serializable;
import java.util.List;

/**
 * Describes a service capable of maintaining a cache of standard site level overrides. This cache spans multiple
 * entity types. This is a multitenant concept. An override declaration and the actual overridden entity
 * are generally two different things. This cache is responsible for reviewing a list of overrides declarations and caching the
 * referenced overridden entities.
 *
 * @author Jeff Fischer
 */
public interface OverridePreCacheService {

    /**
     * Find any cached items matching the passed keys
     *
     * @param cacheKeys the keys to check
     * @return
     */
    List<StandardCacheItem> findElements(String... cacheKeys);

    /**
     * Assuming the passed in site is a standard site, determine whether or not the standard site has any
     * isolated values (i.e. not inherited) for the given type. This information is generally useful when making
     * optimized query determinations for whether or not the standard site should be included in the query.
     *
     * @param siteId
     * @param entityType
     * @return
     */
    boolean isActiveIsolatedSiteForType(Long siteId, String entityType);

    /**
     * Whether or not the cache is active for the specified type
     *
     * @param type the entity type to check
     * @return
     */
    boolean isActiveForType(String type);

    /**
     * Add or remove from the cache based on an override declaration.
     *
     * @param entityType
     * @param cloneId
     * @param isRemove
     */
    void groomCacheBySiteOverride(String entityType, Long cloneId, boolean isRemove);

    /**
     * Update an overridden entity value in the cache
     *
     * @param entityType
     * @param id
     */
    void groomCacheByTargetEntity(String entityType, Serializable id);

    /**
     * Refresh the entire cache structure. Presumably, this will clear and rebuild the structure.
     */
    void refreshCache();

}
