package org.broadleafcommerce.core.search.index;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.catalog.domain.Indexable;

public interface SearchIndexProcessLauncher<I extends Indexable> {
    
    /**
     * Rebuilds an entire index.  Index is typically Solr or ElasticSearch, but could be any type of search-able index.
     * 
     * @throws ServiceException
     */
    public void rebuildIndex() throws ServiceException;
    
    /**
     * Indicates if this process is currently executing.
     * 
     * @return
     */
    public boolean isExecuting();
    
    /**
     * Method to forceably stop a running index job.  If the job is not running, this will have no affect.
     * @return
     */
    public void forceStop();
    
    /**
     * Returns the elapsed time in millis, or -1 if the system is not running.
     * @return
     */
    public long getElapsedTime();

}
