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
package org.broadleafcommerce.common.extensibility.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.net.URI;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;

/**
 * Default utility to access and programmatically create JCache instances via the JCache {@link CacheManager}.
 * Note that this has limited features and functionality based on the fact that the generic JCache (JSR 107) APIs have limited
 * ability to specify max elements, overflow, etc.
 *
 * @author Kelly Tisdell
 */
@Component("blJCacheUtil")
@ConditionalOnEhCacheMissing
public class DefaultJCacheUtil implements JCacheUtil {

    @Autowired
    protected JCacheConfigurationBuilder builder;

    protected CacheManager cacheManager;

    @Autowired
    public DefaultJCacheUtil(CacheManager cacheManager) {
        Assert.notNull(cacheManager, "The CacheManager cannot be null.");
        this.cacheManager = cacheManager;
    }

    public DefaultJCacheUtil(String uri) {
        Assert.notNull(uri, "The URI is for the cache configuration and cannot be null.");
        this.cacheManager = Caching.getCachingProvider().getCacheManager(
                URI.create(uri), Caching.getCachingProvider().getDefaultClassLoader()
        );
    }

    public DefaultJCacheUtil(URI uri) {
        Assert.notNull(uri, "The URI is for the cache configuration and cannot be null.");
        this.cacheManager = Caching.getCachingProvider().getCacheManager(
                uri, Caching.getCachingProvider().getDefaultClassLoader()
        );
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
    public synchronized <K, V> Cache<K, V> createCache(
            String cacheName,
            int ttlSeconds,
            int maxElementsInMemory,
            Class<K> key,
            Class<V> value
    ) {
        Configuration<K, V> config = builder.buildConfiguration(ttlSeconds, maxElementsInMemory, key, value);
        Cache<K, V> cache = getCacheManager().createCache(cacheName, config);
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
     *
     * @param cache
     */
    protected void enableManagement(Cache<?, ?> cache) {
        getCacheManager().enableManagement(cache.getName(), false);
    }

    /**
     * By default this enables statistics for each cache that is created here.
     *
     * @param cache
     */
    protected void enableStatistics(Cache<?, ?> cache) {
        getCacheManager().enableStatistics(cache.getName(), true);
    }

}
