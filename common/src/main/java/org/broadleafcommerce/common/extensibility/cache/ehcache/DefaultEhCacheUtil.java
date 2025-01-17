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
package org.broadleafcommerce.common.extensibility.cache.ehcache;

import org.broadleafcommerce.common.extensibility.cache.DefaultJCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;

/**
 * Allows an encapsulated way to create caches programmatically from an EhCache {@link CacheManager}.  
 * The standard APIs do not provide enough control, so we encapsulate those details here.
 * 
 * @author Kelly Tisdell
 *
 */
@Component("blJCacheUtil")
@ConditionalOnEhCache
public class DefaultEhCacheUtil extends DefaultJCacheUtil {
    
    public DefaultEhCacheUtil(String uri) {
        super(uri);
    }
    
    @Autowired
    public DefaultEhCacheUtil(CacheManager cacheManager) {
        super(cacheManager);
    }
    
    public DefaultEhCacheUtil(URI uri) {
        super(uri);
    }

    @Override
    public <K,V> Cache<K,V> getCache(String cacheName) {
        return this.getCacheManager().getCache(cacheName);
    }

    @Override
    public synchronized Cache<Object, Object> createCache(String cacheName, int ttlSeconds, int maxElementsInMemory) {
        return this.createCache(cacheName, ttlSeconds, maxElementsInMemory, Object.class, Object.class);
    }

    @Override
    public synchronized <K, V> Cache<K, V> createCache(String cacheName, int ttlSeconds, int maxElementsInMemory, Class<K> key, Class<V> value) {
        Configuration<K, V> configuration = builder.buildConfiguration(ttlSeconds, maxElementsInMemory, key, value);
        Cache<K, V> cache = getCacheManager().createCache(cacheName, configuration);
        enableManagement(cache);
        enableStatistics(cache);
        return cache;
    }
    
}
