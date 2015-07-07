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
package org.broadleafcommerce.common.cache.engine;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * 
 * @author jfischer
 *
 */
@Deprecated
public class HydratedCache extends Hashtable<String, Object> {

    private static final long serialVersionUID = 1L;

    private String cacheName;
    private String cacheRegion;

    public HydratedCache(String cacheRegion, String cacheName) {
        this.cacheRegion = cacheRegion;
        this.cacheName = cacheName;
    }

    public String getCacheName() {
        return cacheName;
    }

    public String getCacheRegion() {
        return cacheRegion;
    }

    public void setCacheRegion(String cacheRegion) {
        this.cacheRegion = cacheRegion;
    }

    public HydratedCacheElement getCacheElement(String cacheRegion, String cacheName, Serializable key) {
        return (HydratedCacheElement) get(cacheRegion + "_" + cacheName + "_" + key);
    }
    
    public HydratedCacheElement removeCacheElement(String cacheRegion, String cacheName, Serializable key) {
        String myKey = cacheRegion + "_" + cacheName + "_" + key;
        return (HydratedCacheElement) remove(myKey);
    }
    
    public void addCacheElement(String cacheRegion, String cacheName, Serializable key, Object value) {
        String myKey = cacheRegion + "_" + cacheName + "_" + key;
        put(myKey, value);
    }
}
