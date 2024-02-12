/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2024 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.cache.engine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

/**
 * 
 * @author jfischer
 *
 */
@Deprecated
public class HydratedCacheManagerImpl implements CacheEventListener, HydratedCacheManager, HydratedAnnotationManager {

    private static final Log LOG = LogFactory.getLog(HydratedCacheManagerImpl.class);
    private static final HydratedCacheManagerImpl MANAGER = new HydratedCacheManagerImpl();

    public static HydratedCacheManagerImpl getInstance() {
        return MANAGER;
    }

    private HydratedCacheManagerImpl()  {}

    private Map<String, HydratedCache> hydratedCacheContainer = new Hashtable<String, HydratedCache>(100);
    private Map<String, HydrationDescriptor> hydrationDescriptors = new Hashtable<String, HydrationDescriptor>(100);

    public void addHydratedCache(final HydratedCache cache) {
        hydratedCacheContainer.put(cache.getCacheRegion() + "_" + cache.getCacheName(), cache);
    }

    public HydratedCache removeHydratedCache(final String cacheRegion, final String cacheName) {
        return hydratedCacheContainer.remove(cacheRegion + "_" + cacheName);
    }

    public  HydratedCache getHydratedCache(final String cacheRegion, final String cacheName) {
        if (!containsCache(cacheRegion, cacheName)) {
            HydratedCache cache = new HydratedCache(cacheRegion, cacheName);
            addHydratedCache(cache);
        }
        return hydratedCacheContainer.get(cacheRegion + "_" + cacheName);
    }
    
    public boolean containsCache(String cacheRegion, String cacheName) {
        return hydratedCacheContainer.containsKey(cacheRegion + "_" + cacheName);
    }
    
    @Override
    public HydrationDescriptor getHydrationDescriptor(Object entity) {
        if (hydrationDescriptors.containsKey(entity.getClass().getName())) {
            return hydrationDescriptors.get(entity.getClass().getName());
        }
        HydrationDescriptor descriptor = new HydrationDescriptor();
        Class<?> topEntityClass = getTopEntityClass(entity);
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
    
    public Class<?> getTopEntityClass(Object entity) {
        Class<?> myClass = entity.getClass();
        Class<?> superClass = entity.getClass().getSuperclass();
        while (superClass != null && superClass.getName().startsWith("org.broadleaf")) {
            myClass = superClass;
            superClass = superClass.getSuperclass();
        }
        return myClass;
    }
    
    @Override
    public Object getHydratedCacheElementItem(String cacheRegion, String cacheName, Serializable elementKey, String elementItemName) {
        Object response = null;
        HydratedCache hydratedCache = getHydratedCache(cacheRegion, cacheName);
        HydratedCacheElement element = hydratedCache.getCacheElement(cacheRegion, cacheName, elementKey);
        if (element != null) {
            response = element.getCacheElementItem(elementItemName, elementKey);
        }
        return response;
    }
    
    @Override
    public void addHydratedCacheElementItem(String cacheRegion, String cacheName, Serializable elementKey, String elementItemName, Object elementValue) {
        HydratedCache hydratedCache = getHydratedCache(cacheRegion, cacheName);
        HydratedCacheElement element = hydratedCache.getCacheElement(cacheRegion, cacheName, elementKey);
        if (element == null) {
            element = new HydratedCacheElement();
            hydratedCache.addCacheElement(cacheRegion, cacheName, elementKey, element);
        }
        element.putCacheElementItem(elementItemName, elementKey, elementValue);
    }

    @Override
    public void dispose() {
        if (LOG.isInfoEnabled()) {
            LOG.info("Disposing of all hydrated cache members");
        }
        hydratedCacheContainer.clear();
    }

    private void removeCache(String cacheRegion, Serializable key) {
        String cacheName = cacheRegion;
        if (key.getClass().getName().equals("org.hibernate.cache.internal.CacheKeyImplementation")) {
            // Since CacheKeyImplementation is a protected Class we can't cast it nor can we access the entityOrRoleName property
            // therefore, to match how this worked in pre Hibernate 5, we split the toString since it's comprised of the fields we need
            String[] keyPieces = key.toString().split("#");
            cacheName = keyPieces[0];
            key = keyPieces[1];
        }
        if (containsCache(cacheRegion, cacheName)) {
            HydratedCache cache = hydratedCacheContainer.get(cacheRegion + "_" + cacheName);
            String myKey = cacheRegion + "_" + cacheName + "_" + key;
            if (cache.containsKey(myKey)) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Clearing hydrated cache for cache name: " + cacheRegion + "_" + cacheName + "_" + key);
                }
                cache.removeCacheElement(cacheRegion, cacheName, key);
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

    @Override
    public void notifyElementEvicted(Ehcache arg0, Element arg1) {
        removeCache(arg0.getName(), arg1.getKey());
    }

    @Override
    public void notifyElementExpired(Ehcache arg0, Element arg1) {
        removeCache(arg0.getName(), arg1.getKey());
    }

    @Override
    public void notifyElementPut(Ehcache arg0, Element arg1) throws CacheException {
        //do nothing
    }

    @Override
    public void notifyElementRemoved(Ehcache arg0, Element arg1) throws CacheException {
        removeCache(arg0.getName(), arg1.getKey());
    }

    @Override
    public void notifyElementUpdated(Ehcache arg0, Element arg1) throws CacheException {
        removeCache(arg0.getName(), arg1.getKey());
    }

    @Override
    public void notifyRemoveAll(Ehcache arg0) {
        removeAll(arg0.getName());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return this;
    }

}
