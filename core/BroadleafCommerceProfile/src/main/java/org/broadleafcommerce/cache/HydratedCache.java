/*
 * Copyright 2008-2009 the original author or authors.
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
package org.broadleafcommerce.cache;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * 
 * @author jfischer
 *
 */
public class HydratedCache extends Hashtable<String, Object> {

    private static final long serialVersionUID = 1L;

    private String cacheName;

    public HydratedCache(String cacheName) {
        this.cacheName = cacheName;
    }

    public String getCacheName() {
        return cacheName;
    }

    public HydratedCacheElement getCacheElement(String cacheName, Serializable key) {
        return (HydratedCacheElement) get(cacheName + "_" + key);
    }
    
    public HydratedCacheElement removeCacheElement(String cacheName, Serializable key) {
        String myKey = cacheName + "_" + key;
        return (HydratedCacheElement) remove(myKey);
    }
    
    public void addCacheElement(String cacheName, Serializable key, Object value) {
        String myKey = cacheName + "_" + key;
        put(myKey, value);
    }
}
