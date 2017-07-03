/*
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.server.service;

import org.broadleafcommerce.openadmin.dto.FieldMetadata;

import java.util.Map;

/**
 * @author Jerry Ocanas (jocanas)
 */
public interface SkuMetadataCacheService {

    /**
     * Whether or not to use the cache. If they cache is configured to be used but is
     * past the metadata TTL, the cache is cleared out from this method
     */
    boolean useCache();
    
    /**
     * Not generally used but could be useful in some scenarios if you need to invalidate the entire cache
     * @return the cache, keyed by {@link #buildCacheKey(String)}
     */
    Map<String, Map<String, FieldMetadata>> getEntireCache();

    /**
     * Builds out the cache key to use for the other methods
     * @param productId optional productId, can be null
     * @return
     */
    String buildCacheKey(String productId);
    
    Map<String, FieldMetadata> getFromCache(String cacheKey);

    boolean addToCache(String cacheKey, Map<String, FieldMetadata> metadata);
    
    /**
     * Allows for outside sources to invalidate entries from the Sku Metadata cache that
     * is being used during sku inspects.
     *
     * @param cacheKey from {@link #buildCacheKey(String)}
     */
    void invalidateFromCache(String cacheKey);
}
