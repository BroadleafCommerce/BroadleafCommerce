/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.service.solr;

import org.broadleafcommerce.common.exception.ServiceException;

import java.util.List;
import java.util.Map;

/**
 * Provides a single cache while exposing a block of code for execution to
 * {@link org.broadleafcommerce.core.search.service.solr.SolrIndexService#performCachedOperation(org.broadleafcommerce.core.search.service.solr.SolrIndexCachedOperation.CacheOperation)}.
 * This serves to boost performance while executing multiple calls to {@link org.broadleafcommerce.core.search.service.solr.SolrIndexService#buildIncrementalIndex(int, int, boolean)}.
 *
 * @see org.broadleafcommerce.core.search.service.solr.SolrIndexService
 * @author Jeff Fischer
 */
public class SolrIndexCachedOperation {

    private static final ThreadLocal<Map<Long, List<Long>>> CACHE = new ThreadLocal<Map<Long, List<Long>>>();

    /**
     * Retrieve the cache bound to the current thread.
     *
     * @return The cache for the current thread, or null if not set
     */
    public static Map<Long, List<Long>> getCache() {
        return CACHE.get();
    }

    /**
     * Set the cache on the current thread
     *
     * @param cache the cache object (usually an empty map)
     */
    public static void setCache(Map<Long, List<Long>> cache) {
        CACHE.set(cache);
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
