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
