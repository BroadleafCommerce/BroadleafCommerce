/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.EternalExpiryPolicy;

/**
 * 
 * @author jfischer
 *
 */
public class BigMemoryHydratedCacheManagerImpl extends AbstractHydratedCacheManager {

    private static final long serialVersionUID = 1L;
    
    private static final Log LOG = LogFactory.getLog(BigMemoryHydratedCacheManagerImpl.class);
    private static final BigMemoryHydratedCacheManagerImpl MANAGER = new BigMemoryHydratedCacheManagerImpl();
    private static final String BIG_MEMORY_HYDRATED_CACHE_NAME = "hydrated-offheap-cache";

    public static BigMemoryHydratedCacheManagerImpl getInstance() {
        return MANAGER;
    }

    private Map<String, List<String>> cacheMemberNamesByEntity = Collections.synchronizedMap(new HashMap<String, List<String>>(100));
    private List<String> removeKeys = Collections.synchronizedList(new ArrayList<String>(100));
    private Cache<String, Object> offHeap = null;

    private synchronized Cache<String, Object> getHeap() {
        if (offHeap == null) {
            JCacheUtil util = ApplicationContextHolder.getApplicationContext().getBean("blJCacheUtil", JCacheUtil.class);
            Cache<String, Object> cache = util.getCache(getBigMemoryHydratedCacheName());
            if (cache != null) {
                offHeap = cache;
            } else {
                Configuration<String, Object> config = getBigMemoryHydratedCacheConfiguration();
                offHeap = util.getCacheManager().createCache(getBigMemoryHydratedCacheName(), config);
            }
        }
        return offHeap;
    }

    protected String getBigMemoryHydratedCacheName() {
        return BIG_MEMORY_HYDRATED_CACHE_NAME;
    }

    protected Configuration<String, Object> getBigMemoryHydratedCacheConfiguration() {
        // TODO 6.1 ehcache 3 Not able to configure this with enough complexity
        MutableConfiguration<String, Object> config = new MutableConfiguration<>();
        config.setExpiryPolicyFactory(EternalExpiryPolicy.factoryOf());
        return config;
    }

    @Override
    public Object getHydratedCacheElementItem(String cacheRegion, String cacheName, Serializable elementKey, String elementItemName) {
        String myKey = createHeapKey(cacheRegion, cacheName, elementItemName, elementKey);
        if (removeKeys.contains(myKey)) {
            return null;
        }
        return getHeap().get(myKey);
    }

    @Override
    public void addHydratedCacheElementItem(String cacheRegion, String cacheName, Serializable elementKey, String elementItemName, Object elementValue) {
        String heapKey = createHeapKey(cacheRegion, cacheName, elementItemName, elementKey);
        String nameKey = createNameKey(cacheRegion, cacheName, elementKey);
        removeKeys.remove(nameKey);
        if (!cacheMemberNamesByEntity.containsKey(nameKey)) {
            List<String> myMembers = new ArrayList<String>(50);
            myMembers.add(elementItemName);
            cacheMemberNamesByEntity.put(nameKey, myMembers);
        } else {
            List<String> myMembers = cacheMemberNamesByEntity.get(nameKey);
            myMembers.add(elementItemName);
        }
        getHeap().put(heapKey, elementValue);
    }

    protected void removeCache(Serializable key) {
        removeCache("", key);
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
        String nameKey = createNameKey(cacheRegion, cacheName, key);
        if (cacheMemberNamesByEntity.containsKey(nameKey)) {
            String[] members = new String[cacheMemberNamesByEntity.get(nameKey).size()];
            members = cacheMemberNamesByEntity.get(nameKey).toArray(members);
            for (String myMember : members) {
                String itemKey = createItemKey(cacheRegion, myMember, nameKey);
                removeKeys.add(itemKey);
            }
            cacheMemberNamesByEntity.remove(nameKey);
        }
    }
    
    private String createItemKey(String cacheRegion, String myMember, Serializable key) {
        String itemKey = "";
        if (useCacheRegionInKey()) {
            itemKey += cacheRegion + '_';
        }
        itemKey += myMember + '_' + key;
        return itemKey;
    }

}
