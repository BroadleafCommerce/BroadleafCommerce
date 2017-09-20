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
package org.broadleafcommerce.core.search.index;

import org.broadleafcommerce.common.exception.ServiceException;

public interface SearchIndexProcessLauncher<I> {
    
    /**
     * Rebuilds an entire index.  Index is typically Solr or ElasticSearch, but could be any type of search-able index. 
     * This is the entry point for rebuilding an index, and is often run in a background thread.
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
     * Returns the elapsed time in millis, or -1 if the system is not running or if this is not the master instance.
     * @return
     */
    public long getElapsedTime();

}
