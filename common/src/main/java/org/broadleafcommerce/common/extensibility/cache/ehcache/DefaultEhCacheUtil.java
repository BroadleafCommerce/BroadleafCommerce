package org.broadleafcommerce.common.extensibility.cache.ehcache;

import org.broadleafcommerce.common.extensibility.cache.DefaultJCacheUtil;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.jsr107.Eh107Configuration;

import java.time.Duration;

import javax.cache.Cache;
import javax.cache.CacheManager;

/**
 * Allows an encapsulated way to create caches programmatically from an EhCache {@link CacheManager}.  The standard APIs do not provide enough control, so we encapsulate it here.
 * @author Kelly Tisdell
 *
 */
public class DefaultEhCacheUtil extends DefaultJCacheUtil {
    
    public DefaultEhCacheUtil() {
        super("ehcache:merged-xml-resource");
    }
    
    public DefaultEhCacheUtil(String uri) {
        super(uri);
    }
    
    public DefaultEhCacheUtil(CacheManager cacheManager) {
        super(cacheManager);
    }
    
    @Override
    public <K,V> Cache<K,V> getCache(String cacheName) {
        return getCacheManager().getCache(cacheName);
    }

    @Override
    public Cache<Object, Object> getOrCreateCache(String cacheName, int ttlSeconds, int maxElementsInMemory) {
        return getOrCreateCache(cacheName, ttlSeconds, maxElementsInMemory, Object.class, Object.class);
    }

    @Override
    public <K, V> Cache<K, V> getOrCreateCache(String cacheName, int ttlSeconds, int maxElementsInMemory, Class<K> key, Class<V> value) {
        Cache<K, V> cache = getCache(cacheName);
        if (cache == null) {
            synchronized (this) {
                cache = getCacheManager().getCache(cacheName, key, value);
                if (cache == null) {
                    ExpiryPolicy<Object, Object> policy;
                    if (ttlSeconds < 1) {
                        //Make it eternal
                        policy = ExpiryPolicyBuilder.noExpiration();
                    } else {
                        policy = ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds((long)ttlSeconds));
                    }
                    
                    CacheConfiguration<K, V> config = CacheConfigurationBuilder.
                            newCacheConfigurationBuilder(key, value, ResourcePoolsBuilder.heap((long)maxElementsInMemory))
                            .withExpiry(policy)
                            .build();
                    cache = getCacheManager().createCache(cacheName, Eh107Configuration.fromEhcacheCacheConfiguration(config));
                }
            }
        }
        
        return cache;
    }

}
