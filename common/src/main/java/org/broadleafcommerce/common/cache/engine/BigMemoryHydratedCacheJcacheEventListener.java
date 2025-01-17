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

import java.io.Serializable;

import javax.cache.Cache;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.event.CacheEntryUpdatedListener;

/**
 * Jcache cache event listener for monitoring cache changes using the Big memory hydrated cache.
 * 
 * Cache changes need to be monitored so that if an entity's field is populated via the hydrated cache manager
 * and that entity is modified we need to evict that cached value for that entity's field from our cache so that
 * we don't return stale data.
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
public class BigMemoryHydratedCacheJcacheEventListener extends BigMemoryHydratedCacheManagerImpl implements CacheEntryExpiredListener<Serializable, Object>, CacheEntryRemovedListener<Serializable, Object>, CacheEntryUpdatedListener<Serializable, Object> {

    private static final long serialVersionUID = 1L;

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
