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

import java.util.Hashtable;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Status;
import net.sf.ehcache.event.CacheManagerEventListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class HydratedCacheManager implements CacheManagerEventListener {

    private static final Log LOG = LogFactory.getLog(HydratedCacheManager.class);
    private static HydratedCacheManager manager = null;

    public static synchronized HydratedCacheManager getInstance() {
        if (manager == null) {
            manager = new HydratedCacheManager();
        }
        return manager;
    }

    private HydratedCacheManager()  {}

    private final Hashtable<String, HydratedCache> hydratedCacheManager = new Hashtable<String, HydratedCache>();

    public void addHydratedCache(HydratedCache cache) {
    	CacheManager cacheManager = CacheManager.getInstance();
    	if (cacheManager.cacheExists(cache.getCacheName())) {
    		cache.setCacheGuid(cacheManager.getCache(cache.getCacheName()).getGuid());
    	}
    	hydratedCacheManager.put(cache.getCacheName(), cache);
    }

    public HydratedCache removeHydratedCache(String cacheName) {
        return hydratedCacheManager.remove(cacheName);
    }

    public  HydratedCache getHydratedCache(String cacheName) {
    	//TODO test that the cache is evicted when the ehcache item expires
    	HydratedCache myCache = hydratedCacheManager.get(cacheName);
    	if (myCache != null) {
	    	CacheManager cacheManager = CacheManager.getInstance();
	    	if (myCache.getCacheGuid() == null) {
	    		myCache.setCacheGuid(cacheManager.getCache(cacheName).getGuid());
	    	} else if (!cacheManager.getCache(cacheName).getGuid().equals(myCache.getCacheGuid())) {
	    		notifyCacheRemoved(cacheName);
	    		myCache.setCacheGuid(cacheManager.getCache(cacheName).getGuid());
	    	}
    	}
    	return myCache;
    }

    public void dispose() throws CacheException {
        if (LOG.isInfoEnabled()) {
            LOG.info("Disposing of all hydrated cache members");
        }
        hydratedCacheManager.clear();
    }

    public Status getStatus() {
        return Status.STATUS_ALIVE;
    }

    public void init() throws CacheException {
        //do nothing
    }

    public void notifyCacheAdded(String cacheName) {
        //do nothing
    }

    public synchronized void notifyCacheRemoved(String cacheName) {
        if (hydratedCacheManager.containsKey(cacheName)) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Clearing hydrated cache for cache name: " + cacheName);
            }
            hydratedCacheManager.get(cacheName).clear();
        }
    }

}
