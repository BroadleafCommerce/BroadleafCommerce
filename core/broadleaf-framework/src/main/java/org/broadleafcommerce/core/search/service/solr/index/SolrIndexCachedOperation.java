/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.search.service.solr.index;

import org.apache.commons.collections4.MapUtils;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.search.dao.CatalogStructure;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a single cache while exposing a block of code for execution to
 * {@link org.broadleafcommerce.core.search.service.solr.index.SolrIndexService#performCachedOperation(org.broadleafcommerce.core.search.service.solr.SolrIndexCachedOperation.CacheOperation)}.
 * This serves to boost performance while executing multiple calls to {@link org.broadleafcommerce.core.search.service.solr.index.SolrIndexService#buildIncrementalIndex(int, int, boolean)}.
 *
 * @see org.broadleafcommerce.core.search.service.solr.index.SolrIndexService
 * @author Jeff Fischer
 */
public class SolrIndexCachedOperation {

    public static final Long DEFAULT_CATALOG_CACHE_KEY = 0l;
    
    private static final ThreadLocal<Map<Long, CatalogStructure>> CACHE = new ThreadLocal<Map<Long, CatalogStructure>>();

    /**
     * Retrieve the cache bound to the current thread.
     *
     * @return The cache for the current thread, or null if not set
     */
    public static CatalogStructure getCache() {
        BroadleafRequestContext ctx = BroadleafRequestContext.getBroadleafRequestContext();
        Catalog currentCatalog = ctx == null ? null : ctx.getCurrentCatalog();
        if (currentCatalog != null) {
            return MapUtils.getObject(CACHE.get(), currentCatalog.getId());
        } else {
            return MapUtils.getObject(CACHE.get(), DEFAULT_CATALOG_CACHE_KEY);
        }
    }

    /**
     * Set the cache on the current thread
     *
     * @param cache the cache object (usually an empty map)
     */
    public static void setCache(CatalogStructure cache) {
        BroadleafRequestContext ctx = BroadleafRequestContext.getBroadleafRequestContext();
        Catalog currentCatalog = ctx == null ? null : ctx.getCurrentCatalog();
        Map<Long, CatalogStructure> catalogCaches = CACHE.get();
        if (catalogCaches == null) {
            catalogCaches = new HashMap<Long, CatalogStructure>();
            CACHE.set(catalogCaches);
        }
        if (currentCatalog != null) {
            catalogCaches.put(currentCatalog.getId(), cache);
        } else {
            catalogCaches.put(DEFAULT_CATALOG_CACHE_KEY, cache);
        }
    }

    /**
     * Clear the thread local cache from the thread
     */
    public static void clearCache() {
        CACHE.remove();
    }

    /**
     * Basic interface representing a block of work to perform with a single cache instance
     */
    public interface CacheOperation {

        /**
         * Execute the block of work
         *
         * @throws ServiceException
         */
        void execute() throws ServiceException;

    }
}
