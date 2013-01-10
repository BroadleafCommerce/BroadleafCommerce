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
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cache.CacheKey;

/**
 * 
 * @author jfischer
 *
 */
public class HydratedCacheManagerImpl implements CacheEventListener, HydratedCacheManager, HydratedAnnotationManager {

    private static final Log LOG = LogFactory.getLog(HydratedCacheManagerImpl.class);
    private static final HydratedCacheManagerImpl MANAGER = new HydratedCacheManagerImpl();

    public static HydratedCacheManagerImpl getInstance() {
        return MANAGER;
    }

    private HydratedCacheManagerImpl()  {}

    private Map<String, HydratedCache> hydratedCacheContainer = new Hashtable<String, HydratedCache>();
    private Map<String, HydrationDescriptor> hydrationDescriptors = new Hashtable<String, HydrationDescriptor>();

    public void addHydratedCache(final HydratedCache cache) {
        hydratedCacheContainer.put(cache.getCacheName(), cache);
    }

    public HydratedCache removeHydratedCache(final String cacheName) {
        return hydratedCacheContainer.remove(cacheName);
    }

    public  HydratedCache getHydratedCache(final String cacheName) {
        if (!containsCache(cacheName)) {
            HydratedCache cache = new HydratedCache(cacheName);
            addHydratedCache(cache);
        }
        return hydratedCacheContainer.get(cacheName);
    }
    
    public boolean containsCache(String cacheName) {
        return hydratedCacheContainer.containsKey(cacheName);
    }
    
    @SuppressWarnings("unchecked")
    public HydrationDescriptor getHydrationDescriptor(Object entity) {
        if (hydrationDescriptors.containsKey(entity.getClass().getName())) {
            return hydrationDescriptors.get(entity.getClass().getName());
        }
        HydrationDescriptor descriptor = new HydrationDescriptor();
        Class topEntityClass = getTopEntityClass(entity);
        HydrationScanner scanner = new HydrationScanner(topEntityClass, entity.getClass());
        scanner.init();
        descriptor.setHydratedMutators(scanner.getCacheMutators());
        Map<String, Method[]> mutators = scanner.getIdMutators();
        if (mutators.size() != 1) {
            throw new RuntimeException("Broadleaf Commerce Hydrated Cache currently only supports entities with a single @Id annotation.");
        }
        Method[] singleMutators = mutators.values().iterator().next();
        descriptor.setIdMutators(singleMutators);
        String cacheRegion = scanner.getCacheRegion();
        if (cacheRegion == null || "".equals(cacheRegion)) {
            cacheRegion = topEntityClass.getName();
        }
        descriptor.setCacheRegion(cacheRegion);
        hydrationDescriptors.put(entity.getClass().getName(), descriptor);
        return descriptor;
    }
    
    @SuppressWarnings("unchecked")
    public Class getTopEntityClass(Object entity) {
        Class myClass = entity.getClass();
        Class superClass = entity.getClass().getSuperclass();
        while (superClass != null && superClass.getName().startsWith("org.broadleaf")) {
            myClass = superClass;
            superClass = superClass.getSuperclass();
        }
        return myClass;
    }
    
    public Object getHydratedCacheElementItem(String cacheName, Serializable elementKey, String elementItemName) {
        Object response = null;
        HydratedCache hydratedCache = getHydratedCache(cacheName);
        HydratedCacheElement element = hydratedCache.getCacheElement(cacheName, elementKey);
        if (element != null) {
            response = element.getCacheElementItem(elementItemName, elementKey);
        }
        return response;
    }
    
    public void addHydratedCacheElementItem(String cacheName, Serializable elementKey, String elementItemName, Object elementValue) {
        HydratedCache hydratedCache = getHydratedCache(cacheName);
        HydratedCacheElement element = hydratedCache.getCacheElement(cacheName, elementKey);
        if (element == null) {
            element = new HydratedCacheElement();
            hydratedCache.addCacheElement(cacheName, elementKey, element);
        }
        element.putCacheElementItem(elementItemName, elementKey, elementValue);
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
