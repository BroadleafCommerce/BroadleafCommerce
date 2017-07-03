/*
 * class#%L
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.EfficientLRUMap;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Jerry Ocanas (jocanas)
 */
@Service("blSkuMetadataCacheService")
public class SkuMetadataCacheServiceImpl implements SkuMetadataCacheService {

    private static final Log LOG = LogFactory.getLog(SkuMetadataCacheServiceImpl.class);

    @Value("${cache.entity.dao.metadata.ttl}")
    protected int cacheEntityMetaDataTtl;

    protected long lastCacheFlushTime = System.currentTimeMillis();

    protected static final Map<String, Map<String, FieldMetadata>> METADATA_CACHE = new EfficientLRUMap<>(1000);

    @Override
    public Map<String, Map<String, FieldMetadata>> getEntireCache() {
        return METADATA_CACHE;
    }
    
    @Override
    public boolean useCache() {
        if (cacheEntityMetaDataTtl < 0) {
            return true;
        }
        if (cacheEntityMetaDataTtl == 0) {
            return false;
        } else {
            if ((System.currentTimeMillis() - lastCacheFlushTime) > cacheEntityMetaDataTtl) {
                lastCacheFlushTime = System.currentTimeMillis();
                METADATA_CACHE.clear();
                return true;
            } else {
                return true;
            }
        }
    }
    
    @Override
    public Map<String, FieldMetadata> getFromCache(String cacheKey) {
        if (useCache()) {
            return METADATA_CACHE.get(cacheKey);
        } else {
            return null;
        }
    }
    
    @Override
    public boolean addToCache(String cacheKey, Map<String, FieldMetadata> metadata) {
        if (useCache()) {
            METADATA_CACHE.put(cacheKey, metadata);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void invalidateFromCache(String cacheKey) {
        LOG.debug("Invalidating Sku metadata cache for: " + cacheKey);
        METADATA_CACHE.remove(cacheKey);
    }
    
    @Override
    public String buildCacheKey(String productId) {
        String key = SkuImpl.class.getName();
        if (productId != null) {
            key += "_" + productId;
        }
        return key;
    }

}
