/*
 * #%L
 * broadleaf-theme
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
