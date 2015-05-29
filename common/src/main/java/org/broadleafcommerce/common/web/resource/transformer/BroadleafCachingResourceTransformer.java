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
package org.broadleafcommerce.common.web.resource.transformer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.web.resource.resolver.BroadleafResourceTransformerOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.CachingResourceResolver;
import org.springframework.web.servlet.resource.CachingResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;

import java.io.IOException;

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
@Component("blCachingResourceTransformer")
public class BroadleafCachingResourceTransformer extends CachingResourceTransformer implements Ordered {

    protected static final Log LOG = LogFactory.getLog(BroadleafCachingResourceTransformer.class);
    private int order = BroadleafResourceTransformerOrder.BLC_CACHE_RESOURCE_TRANSFORMER;
    
    @javax.annotation.Resource(name = "blSpringCacheManager")
    private CacheManager cacheManager;
    
    private static final String DEFAULT_CACHE_NAME = "blResourceTransformerCacheElements";

    @Value("${resource.transformer.caching.enabled:true}")
    protected boolean resourceTransformerCachingEnabled;

    @Autowired
    public BroadleafCachingResourceTransformer(@Qualifier("blSpringCacheManager") CacheManager cacheManager) {
        super(cacheManager, DEFAULT_CACHE_NAME);
    }

    // Allows for an implementor to override the default cache settings.
    public BroadleafCachingResourceTransformer(Cache cache) {
        super(cache);
    }

    @Override
    public Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain transformerChain)
            throws IOException {
        if (resourceTransformerCachingEnabled) {
            return super.transform(request, resource, transformerChain);
        } else {
            return transformerChain.transform(request, resource);
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
