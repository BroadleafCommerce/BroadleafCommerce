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
package org.broadleafcommerce.util;

import java.io.Serializable;
import java.util.Hashtable;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cache.CacheKey;


public class HydratedCacheManagerImpl implements CacheEventListener, HydratedCacheManager {

    private static final Log LOG = LogFactory.getLog(HydratedCacheManagerImpl.class);
    private static HydratedCacheManagerImpl manager = null;

    public static synchronized HydratedCacheManagerImpl getInstance() {
        if (manager == null) {
            manager = new HydratedCacheManagerImpl();
        }
        return manager;
    }

    private HydratedCacheManagerImpl()  {}

    private final Hashtable<String, HydratedCache> hydratedCacheContainer = new Hashtable<String, HydratedCache>();

    public void addHydratedCache(HydratedCache cache) {
    	hydratedCacheContainer.put(cache.getCacheName(), cache);
    }

    public HydratedCache removeHydratedCache(String cacheName) {
        return hydratedCacheContainer.remove(cacheName);
    }

    public  HydratedCache getHydratedCache(String cacheName) {
    	return hydratedCacheContainer.get(cacheName);
    }
    
    public Object getHydratedCacheElementItem(String cacheName, Serializable elementKey, String elementItemName) {
    	Object response = null;
    	HydratedCache hydratedCache = getHydratedCache(cacheName);
        if (hydratedCache != null) {
        	HydratedCacheElement element = hydratedCache.getCacheElement(cacheName, elementKey);
        	if (element != null) {
        		response = element.getCacheElementItem(elementItemName, elementKey);
        	}
        }
        return response;
    }
    
    public void addHydratedCacheElementItem(String cacheName, Serializable elementKey, String elementItemName, Object elementValue) {
    	HydratedCache hydratedCache = getHydratedCache(cacheName);
        if (hydratedCache != null) {
        	HydratedCacheElement element = hydratedCache.getCacheElement(cacheName, elementKey);
        	if (element == null) {
        		element = new HydratedCacheElement();
        		hydratedCache.addCacheElement(cacheName, elementKey, element);
        	}
        	element.putCacheElementItem(elementItemName, elementKey, elementValue);
        }
    }

    public void dispose() {
        if (LOG.isInfoEnabled()) {
            LOG.info("Disposing of all hydrated cache members");
        }
        hydratedCacheContainer.clear();
    }

    private void removeCache(String cacheName, Serializable key) {
    	if (key instanceof CacheKey) {
    		key = ((CacheKey) key).getKey();
    	}
        if (hydratedCacheContainer.containsKey(cacheName)) {
        	HydratedCache cache = hydratedCacheContainer.get(cacheName);
        	String myKey = cacheName + "_" + key;
        	if (cache.containsKey(myKey)) {
	            if (LOG.isInfoEnabled()) {
	                LOG.info("Clearing hydrated cache for cache name: " + cacheName + "_" + key);
	            }
	            cache.removeCacheElement(cacheName, key);
        	}
        }
    }
    
    private void removeAll(String cacheName) {
    	if (hydratedCacheContainer.containsKey(cacheName)) {
    		if (LOG.isInfoEnabled()) {
                LOG.info("Clearing all hydrated caches for cache name: " + cacheName);
            }
    		hydratedCacheContainer.remove(cacheName);
    	}
    }

	public void notifyElementEvicted(Ehcache arg0, Element arg1) {
		removeCache(arg0.getName(), arg1.getKey());
	}

	public void notifyElementExpired(Ehcache arg0, Element arg1) {
		removeCache(arg0.getName(), arg1.getKey());
	}

	public void notifyElementPut(Ehcache arg0, Element arg1) throws CacheException {
		//do nothing
	}

	public void notifyElementRemoved(Ehcache arg0, Element arg1) throws CacheException {
		removeCache(arg0.getName(), arg1.getKey());
	}

	public void notifyElementUpdated(Ehcache arg0, Element arg1) throws CacheException {
		removeCache(arg0.getName(), arg1.getKey());
	}

	public void notifyRemoveAll(Ehcache arg0) {
		removeAll(arg0.getName());
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return this;
	}

}
