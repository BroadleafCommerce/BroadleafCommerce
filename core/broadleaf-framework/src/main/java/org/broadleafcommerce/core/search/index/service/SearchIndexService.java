package org.broadleafcommerce.core.search.index.service;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.catalog.domain.Indexable;

import java.util.List;

public interface SearchIndexService<I extends Indexable> {
    
    /**
     * Rebuilds an entire index.  Index is typically Solr or ElasticSearch, but could be any type of search-able index.
     * 
     * @throws ServiceException
     */
    public void rebuildIndex() throws ServiceException;
    
    /**
     * Reindex a specific list of indexable items.
     * @param items
     */
    public void reindexItems(List<I> items) throws ServiceException;
    
    /**
     * Indicates if this process is currently executing.
     * 
     * @return
     */
    public boolean isExecutingReindex();
    
    /**
     * Method to forceably stop a running index job.  If the job is not running, this will have no affect.
     * @return
     */
    public void stop();

}
