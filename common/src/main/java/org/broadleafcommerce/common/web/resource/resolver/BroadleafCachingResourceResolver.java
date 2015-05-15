/*
 * #%L
 * broadleaf-theme
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.common.web.resource.resolver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.CachingResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * Wraps Spring's {@link CachingResourceResolver} but adds in support to disable with 
 * environment properties.
 * 
 *  {@code }
 * 
 * @author Brian Polster
 * @since Broadleaf 4.0
 */
@Component("blCacheResourceResolver")
public class BroadleafCachingResourceResolver extends CachingResourceResolver implements Ordered {

    public static final String RESOLVED_RESOURCE_CACHE_KEY_PREFIX_NULL = "resolvedResourceNull:";
    public static final String RESOLVED_URL_PATH_CACHE_KEY_PREFIX_NULL = "resolvedUrlPathNull:";
    private static final Object NULL_REFERENCE = new Object();
    protected static final Log LOG = LogFactory.getLog(BroadleafCachingResourceResolver.class);
    private int order = BroadleafResourceResolverOrder.BLC_CACHE_RESOURCE_RESOLVER;
    
    @javax.annotation.Resource(name = "blSpringCacheManager")
    private CacheManager cacheManager;
    
    private static final String DEFAULT_CACHE_NAME = "blResourceCacheElements";

    @Value("${resource.caching.enabled:true}")
    protected boolean resourceCachingEnabled;

    @Autowired
    public BroadleafCachingResourceResolver(@Qualifier("blSpringCacheManager") CacheManager cacheManager) {
        super(cacheManager, DEFAULT_CACHE_NAME);
    }

    // Allows for an implementor to override the default cache settings.
    public BroadleafCachingResourceResolver(Cache cache) {
        super(cache);
    }

    @Override
    protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath,
            List<? extends Resource> locations, ResourceResolverChain chain) {
        if (resourceCachingEnabled) {
            String key = RESOLVED_RESOURCE_CACHE_KEY_PREFIX_NULL + requestPath;
            Object nullResource = getCache().get(key, Object.class);
            if (nullResource != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace(String.format("Found null reference resource match for '%s'", requestPath));
                }
                return null;
            } else {
                Resource response = super.resolveResourceInternal(request, requestPath, locations, chain);
                if (response == null) {
                    if (logger.isTraceEnabled()) {
                        logger.trace(String.format("Putting resolved null reference resource in cache for '%s'", requestPath));
                    }
                    getCache().put(key, NULL_REFERENCE);
                }
                return response;
            }
        } else {
            return chain.resolveResource(request, requestPath, locations);
        }
    }

    @Override
    protected String resolveUrlPathInternal(String resourceUrlPath,
            List<? extends Resource> locations, ResourceResolverChain chain) {
        if (resourceCachingEnabled) {
            String key = RESOLVED_URL_PATH_CACHE_KEY_PREFIX_NULL + resourceUrlPath;
            Object nullResource = getCache().get(key, Object.class);
            if (nullResource != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace(String.format("Found null reference url path match for '%s'", resourceUrlPath));
                }
                return null;
            } else {
                String response = super.resolveUrlPathInternal(resourceUrlPath, locations, chain);
                if (response == null) {
                    if (logger.isTraceEnabled()) {
                        logger.trace(String.format("Putting resolved null reference url path in cache for '%s'", resourceUrlPath));
                    }
                    getCache().put(key, NULL_REFERENCE);
                }
                return response;
            }
        } else {
            return chain.resolveUrlPath(resourceUrlPath, locations);
        }
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
