/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2019 Broadleaf Commerce
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
package org.broadleafcommerce.common.extensibility.cache;

import org.springframework.util.Assert;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.Duration;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.expiry.ModifiedExpiryPolicy;

/**
 * Default utility to access and programmatically create JCache instances via the JCache {@link CacheManager}.
 * Note that this has limited features and functionality based on the fact that the generic JCache (JSR 107) APIs have limited 
 * ability to specify max elements, overflow, etc.
 * 
 * @author Kelly Tisdell
 *
 */
public class DefaultJCacheUtil implements JCacheUtil {
    
    protected CacheManager cacheManager;
    
    public DefaultJCacheUtil(CacheManager cacheManager) {
        Assert.notNull(cacheManager, "The CacheManager cannot be null.");
        this.cacheManager = cacheManager;
    }
    
    public DefaultJCacheUtil(String uri) {
        Assert.notNull(uri, "The URI is for the cache configuration and cannot be null.");
        this.cacheManager = Caching.getCachingProvider().getCacheManager(URI.create(uri), getClass().getClassLoader());
    }
    
    public DefaultJCacheUtil(URI uri) {
        Assert.notNull(uri, "The URI is for the cache configuration and cannot be null.");
        this.cacheManager = Caching.getCachingProvider().getCacheManager(uri, getClass().getClassLoader());
    }

    @Override
    public CacheManager getCacheManager() {
        return cacheManager;
    }

    @Override
    public synchronized Cache<Object, Object> createCache(String cacheName, int ttlSeconds, int maxElementsInMemory) {
        return createCache(cacheName, ttlSeconds, maxElementsInMemory, Object.class, Object.class);
    }

    @Override
    public synchronized <K, V> Cache<K, V>  createCache(String cacheName, int ttlSeconds, int maxElementsInMemory, Class<K> key, Class<V> value) {
        Factory<ExpiryPolicy> expiryPolicy;
        if (ttlSeconds < 1) {
            //Eternal
            expiryPolicy = EternalExpiryPolicy.factoryOf();
        } else {
            //Number of seconds since created or updated in cache
            expiryPolicy = ModifiedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, (long) ttlSeconds));
        }
        
        MutableConfiguration<K, V> config = new MutableConfiguration<>();
        config.setTypes(key, value);
        config.setExpiryPolicyFactory(expiryPolicy);
        Cache<K,V> cache = getCacheManager().createCache(cacheName, config);
        enableManagement(cache);
        enableStatistics(cache);
        return cache;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) {
        return getCacheManager().getCache(cacheName);
    }

    /**
     * By default this disables management of each cache that is created here.
     * @param cache
     */
    protected void enableManagement(Cache<?,?> cache) {
        getCacheManager().enableManagement(cache.getName(), false);
    }
    
    /**
     * By default this enables statistics for each cache that is created here.
     * @param cache
     */
    protected void enableStatistics(Cache<?,?> cache) {
        getCacheManager().enableStatistics(cache.getName(), true);
    }
}
