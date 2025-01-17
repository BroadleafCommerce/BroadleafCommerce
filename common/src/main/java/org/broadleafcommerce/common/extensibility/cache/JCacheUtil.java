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

import javax.cache.Cache;
import javax.cache.CacheManager;

/**
 * Generic utility to allow one access to the JCache {@link CacheManager}.  This also allows for the encapsulation of implementation-specific cache configurations that may 
 * not be supported by the generic JCache (JSR 107) APIs.
 * 
 * @author Kelly Tisdell
 *
 */
public interface JCacheUtil {

    public CacheManager getCacheManager();
    
    /**
     * Returns a new Cache with the appropriate cache name, TTL, and maxElements.  If the cache exists, an exception will be thrown.
     * 
     * The ttl and maxElementsInMemory settings may be implementation specific and so implementors may choose to ignore these arguments.
     * 
     * If ttlSeconds is less than 1 then the cache will not expire.
     * 
     * @param cacheName
     * @param ttlSeconds
     * @param maxElementsInMemory
     * @return
     */
    public Cache<Object, Object> createCache(String cacheName, int ttlSeconds, int maxElementsInMemory);
    
    /**
     * Returns a new Cache with the appropriate cache name, TTL, and maxElements.  If the cache exists, an exception will be thrown.
     * 
     * The ttl and maxElementsInMemory settings may be implementation specific and so implementors may choose to ignore these arguments.
     * 
     * If ttlSeconds is less than 0 then the cache will not expire.
     * 
     * @param cacheName
     * @param ttlSeconds
     * @param maxElementsInMemory
     * @param key
     * @param value
     * @return
     */
    public <K,V> Cache<K, V> createCache(String cacheName, int ttlSeconds, int maxElementsInMemory, Class<K> key, Class<V> value);
    
    /**
     * Returns the cache associated with the name or null if no cache exists.
     * 
     * @param cacheName
     * @return
     */
    public <K,V> Cache<K, V> getCache(String cacheName);
    
    
}
