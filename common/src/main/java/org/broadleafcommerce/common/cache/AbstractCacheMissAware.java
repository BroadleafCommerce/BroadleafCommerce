/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.util.ClassUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.annotation.Resource;

/**
 * Support for any class that wishes to utilize a query miss cache. This cache is capable of caching a query miss
 * (the query returns no results). This is beneficial since standard level 2 cache does not maintain misses.
 *
 * NOTE, special cache invalidation support must be added to address this cache if a change is made to one or more of
 * the cached missed items.
 *
 * @author Jeff Fischer
 */
public abstract class AbstractCacheMissAware {

    @Resource(name="blStatisticsService")
    protected StatisticsService statisticsService;

    protected Cache cache;

    private Object nullObject = null;

    /**
     * Build the key representing this missed cache item. Will include sandbox information
     * if appropriate.
     *
     * @param params the appropriate params comprising a unique key for this cache item
     * @return the completed key
     */
    protected String buildKey(String... params) {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        SandBox sandBox = context.getSandBox();
        String key = StringUtils.join(params);
        if (sandBox != null) {
            key = sandBox.getId() + "_" + key;
        }
        return key;
    }

    /**
     * Retrieve the missed cache item from the specified cache.
     *
     * @param key the unique key for the cache item
     * @param cacheName the name of the cache - this is the cache region name from ehcache config
     * @param <T> the type of the cache item
     * @return the cache item instance
     */
    protected <T> T getObjectFromCache(String key, String cacheName) {
        Element cacheElement = getCache(cacheName).get(key);
        if (cacheElement != null) {
            return (T) cacheElement.getValue();
        }
        return null;
    }

    /**
     * Retrieve the underlying cache for this query miss cache. Presumably and Ehcache
     * region has been configured for this cacheName.
     *
     * @param cacheName the name of the cache - the ehcache region name
     * @return the underlying cache
     */
    protected Cache getCache(String cacheName) {
        if (cache == null) {
            cache = CacheManager.getInstance().getCache(cacheName);
        }
        return cache;
    }

    /**
     * Remove a specific cache item from the underlying cache
     *
     * @param cacheName the name of the cache - the ehcache region name
     * @param params the appropriate params comprising a unique key for this cache item
     */
    protected void removeItemFromCache(String cacheName, String... params) {
        String key = buildKey(params);
        getCache(cacheName).remove(key);
    }

    /**
     * Remove all items from the underlying cache - a complete clear
     *
     * @param cacheName the name of the cache - the ehcache region name
     */
    protected void clearCache(String cacheName) {
        getCache(cacheName).removeAll();
    }

    /**
     * Retrieve a null representation of the cache item. This representation is the same for
     * all cache misses and is used as the object representation to store in the cache for a
     * cache miss.
     *
     * @param responseClass the class representing the type of the cache item
     * @param <T> the type of the cache item
     * @return the null representation for the cache item
     */
    protected synchronized <T> T getNullObject(final Class<T> responseClass) {
        if (nullObject == null) {
            Class<?>[] interfaces = (Class<?>[]) ArrayUtils.add(ClassUtils.getAllInterfacesForClass(responseClass), Serializable.class);
            nullObject = Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, new InvocationHandler() {
                @Override
                public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                    if (method.getName().equals("equals")) {
                        return !(objects[0] == null) && objects[0].hashCode() == 31;
                    } else if (method.getName().equals("hashCode")) {
                        return 31;
                    } else if (method.getName().equals("toString")) {
                        return "Null_" + responseClass.getSimpleName();
                    }
                    throw new IllegalAccessException("Not a real object");
                }
            });
        }
        return (T) nullObject;
    }

    /**
     * This is the main entry point for retrieving an object from this cache.
     *
     * @see org.broadleafcommerce.common.cache.StatisticsService
     * @param responseClass the class representing the type of the cache item
     * @param cacheName the name of the cache - the ehcache region name
     * @param statisticsName the name to use for cache hit statistics
     * @param retrieval the block of code to execute if a cache miss is not found in this cache
     * @param params the appropriate params comprising a unique key for this cache item
     * @param <T> the type of the cache item
     * @return The object retrieved from the executiom of the PersistentRetrieval, or null if a cache miss was found in this cache
     */
    protected <T> T getCachedObject(Class<T> responseClass, String cacheName, String statisticsName, PersistentRetrieval<T> retrieval, String... params) {
        T nullResponse = getNullObject(responseClass);
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        String key = buildKey(params);
        T response = null;
        if (context.isProductionSandBox()) {
            response = getObjectFromCache(key, cacheName);
        }
        if (response == null) {
            response = retrieval.retrievePersistentObject();
            if (response == null) {
                response = nullResponse;
            }
            //only handle null, non-hits. Otherwise, let level 2 cache handle it
            if (context.isProductionSandBox() && response.equals(nullResponse)) {
                statisticsService.addCacheStat(statisticsName, false);
                getCache(cacheName).put(new Element(key, response));
            }
        } else {
            statisticsService.addCacheStat(statisticsName, true);
        }
        if (response.equals(nullResponse)) {
            return null;
        }
        return response;
    }
}
