/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
import org.broadleafcommerce.common.extensibility.cache.JCacheUtil;
import org.broadleafcommerce.common.util.ApplicationContextHolder;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.cache.Cache;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListenerException;

/**
 * 
 * @author jfischer
 *
 */
@Component("blHydratedCacheMangager")
public class EhcacheHydratedCacheManagerImpl extends AbstractHydratedCacheManager<Serializable, Object> {

    private static final long serialVersionUID = 1L;
    
    private static final Log LOG = LogFactory.getLog(EhcacheHydratedCacheManagerImpl.class);
    private static final EhcacheHydratedCacheManagerImpl MANAGER = new EhcacheHydratedCacheManagerImpl();
    
    public static final String HYDRATED_CACHE_NAME = "hydrated-cache";

    public static EhcacheHydratedCacheManagerImpl getInstance() {
        return MANAGER;
    }

    private Map<String, List<String>> cacheMembersByEntity = Collections.synchronizedMap(new HashMap<String, List<String>>(100));
    private Cache<String, Object> heap = null;

    private synchronized Cache<String, Object> getHeap() {
        if (heap == null) {
            JCacheUtil cacheUtil = ApplicationContextHolder.getApplicationContext().getBean("blJCacheUtil", JCacheUtil.class);
            Cache<String, Object> cache = cacheUtil.getCache(getHydratedCacheName());
            if (cache != null) {
                heap = cache;
            } else {
                //We want this to never expire, so we use a TTL of -1. Max elements are 100000.
                heap = cacheUtil.createCache(getHydratedCacheName(), -1, 100000, String.class, Object.class);
            }
        }
        return heap;
    }

    protected String getHydratedCacheName() {
        return HYDRATED_CACHE_NAME;
    }

    @Override
    public Object getHydratedCacheElementItem(String cacheRegion, String cacheName, Serializable elementKey, String elementItemName) {
        String myKey = cacheRegion + '_' + cacheName + '_' + elementItemName + '_' + elementKey;
        return getHeap().get(myKey);
    }

    @Override
    public void addHydratedCacheElementItem(String cacheRegion, String cacheName, Serializable elementKey, String elementItemName, Object elementValue) {
        String heapKey = cacheRegion + '_' + cacheName + '_' + elementItemName + '_' + elementKey;
        String nameKey = cacheRegion + '_' + cacheName + '_' + elementKey;
        if (!cacheMembersByEntity.containsKey(nameKey)) {
            List<String> myMembers = new ArrayList<String>(50);
            myMembers.add(elementItemName);
            cacheMembersByEntity.put(nameKey, myMembers);
        } else {
            List<String> myMembers = cacheMembersByEntity.get(nameKey);
            myMembers.add(elementItemName);
        }
        getHeap().put(heapKey, elementValue);
    }

    protected void removeCache(String cacheRegion, Serializable key) {
        String cacheName = cacheRegion;
        // TODO 6.1 ehcache 3 Make sure this is adding correctly
        if (key.getClass().getName().equals("org.hibernate.cache.internal.CacheKeyImplementation")) {
            // Since CacheKeyImplementation is a protected Class we can't cast it nor can we access the entityOrRoleName property
            // therefore, to match how this worked in pre Hibernate 5, we split the toString since it's comprised of the fields we need
            String[] keyPieces = key.toString().split("#");
            cacheName = keyPieces[0];
            key = keyPieces[1];
        }
        String nameKey = cacheRegion + '_' + cacheName + '_' + key;
        if (cacheMembersByEntity.containsKey(nameKey)) {
            String[] members = new String[cacheMembersByEntity.get(nameKey).size()];
            members = cacheMembersByEntity.get(nameKey).toArray(members);
            for (String myMember : members) {
                String itemKey = cacheRegion + '_' + cacheName + '_' + myMember + '_' + key;
                getHeap().remove(itemKey);
            }
            cacheMembersByEntity.remove(nameKey);
        }
    }
    
    @Override
    public void onExpired(Iterable<CacheEntryEvent<? extends Serializable, ? extends Object>> events) throws CacheEntryListenerException {
        removeCache(events);
    }

    @Override
    public void onRemoved(Iterable<CacheEntryEvent<? extends Serializable, ? extends Object>> events) throws CacheEntryListenerException {
        removeCache(events);
    }

    @Override
    public void onUpdated(Iterable<CacheEntryEvent<? extends Serializable, ? extends Object>> events) throws CacheEntryListenerException {
        removeCache(events);
    }

    protected void removeCache(Iterable<CacheEntryEvent<? extends Serializable, ? extends Object>> events) {
        if (events != null) {
            for (CacheEntryEvent<? extends Serializable, ? extends Object> event : events) {
                @SuppressWarnings("unchecked")
                Cache<String, Object> cache = event.getSource();
                String region = cache.getName();
                Serializable key = event.getKey();
                removeCache(region, key);
            }
        }
    }

}
