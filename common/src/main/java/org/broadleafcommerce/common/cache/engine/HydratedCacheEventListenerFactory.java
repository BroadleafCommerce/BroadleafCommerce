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

import org.broadleafcommerce.common.util.ApplicationContextHolder;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.event.CacheEntryListener;

/**
 * 
 * @author jfischer
 *
 */
@Component("blHydratedCacheEventListenerFactory")
public class HydratedCacheEventListenerFactory implements CacheEventListener, CacheEntryListener {

    private static HydratedCacheManager manager = null;

    public HydratedCacheEventListenerFactory() {
        //todo what should be actually here for new ehcache ??
    }

    @Autowired
    public HydratedCacheEventListenerFactory(@Qualifier("blHydratedCacheMangager") HydratedCacheManager hydratedCacheManager, 
                                             @Value("${cache.hydratedCache.names}") List<String> cacheNames,
                                             @Value("${cache.hydratedCache.require.old.value:false}") Boolean oldValue,
                                             @Value("${cache.hydratedCache.require.synchronous:true}") Boolean synchronous) {
        CacheManager cacheManager = ApplicationContextHolder.getApplicationContext().getBean("blCacheManager", CacheManager.class);
        for (String cacheName : cacheNames) {
            if(cacheName.startsWith("$")){
                continue;
            }
            Cache cache = cacheManager.getCache(cacheName);
            cache.registerCacheEntryListener(new MutableCacheEntryListenerConfiguration(FactoryBuilder.factoryOf(hydratedCacheManager), null, oldValue, synchronous));
        }
        manager = hydratedCacheManager;
    }

    public static HydratedCacheManager getConfiguredManager() {
        return manager;
    }

    @Override
    public void onEvent(CacheEvent cacheEvent) {

    }
}
