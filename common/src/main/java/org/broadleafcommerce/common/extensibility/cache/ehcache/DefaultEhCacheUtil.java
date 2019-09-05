package org.broadleafcommerce.common.extensibility.cache.ehcache;

import org.broadleafcommerce.common.extensibility.cache.DefaultJCacheUtil;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.jsr107.Eh107Configuration;

import java.net.URI;
import java.time.Duration;

import javax.cache.Cache;
import javax.cache.CacheManager;

/**
 * Allows an encapsulated way to create caches programmatically from an EhCache {@link CacheManager}.  The standard APIs do not provide enough control, so we encapsulate it here.
 * @author Kelly Tisdell
 *
 */
public class DefaultEhCacheUtil extends DefaultJCacheUtil {
    
    public static final String EH_CACHE_MERGED_XML_RESOUCE_URI = "ehcache:merged-xml-resource";
    
    public DefaultEhCacheUtil() {
        super(EH_CACHE_MERGED_XML_RESOUCE_URI);
    }
    
    public DefaultEhCacheUtil(String uri) {
        super(uri);
    }
    
    public DefaultEhCacheUtil(CacheManager cacheManager) {
        super(cacheManager);
    }
    
    public DefaultEhCacheUtil(URI uri) {
        super(uri);
    }

    @Override
    public <K,V> Cache<K,V> getCache(String cacheName) {
        return getCacheManager().getCache(cacheName);
    }

    @Override
    public synchronized Cache<Object, Object> createCache(String cacheName, int ttlSeconds, int maxElementsInMemory) {
        return createCache(cacheName, ttlSeconds, maxElementsInMemory, Object.class, Object.class);
    }

    @Override
    public synchronized <K, V> Cache<K, V> createCache(String cacheName, int ttlSeconds, int maxElementsInMemory, Class<K> key, Class<V> value) {
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
        
        Cache<K,V> cache = getCacheManager().createCache(cacheName, Eh107Configuration.fromEhcacheCacheConfiguration(config));
        enableManagement(cache);
        enableStatistics(cache);
        return cache;
    }
    
}
