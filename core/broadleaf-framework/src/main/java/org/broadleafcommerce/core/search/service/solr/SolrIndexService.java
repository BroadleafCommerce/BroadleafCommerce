/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.search.service.solr;

import org.apache.solr.client.solrj.SolrServer;
import org.broadleafcommerce.common.exception.ServiceException;

import java.io.IOException;

/**
 * @author Andre Azzolini (apazzolini)
 */
public interface SolrIndexService {

    /**
     * Rebuilds the current index. 
     * 
     * @throws IOException 
     * @throws ServiceException
     */
    public void rebuildIndex() throws ServiceException, IOException;
    
    /**
     * The internal method for building indexes. This is exposed via this interface in case someone would like to 
     * more granularly control the indexing strategy.
     * 
     * @see #restoreState(Object[])
     * @param page
     * @param pageSize
     * @param useReindexServer - if set to false will index directly on the primary server
     * @throws ServiceException
     */
    public void buildIncrementalIndex(int page, int pageSize, boolean useReindexServer) throws ServiceException;

    /**
     * Saves some global context that might be altered during indexing.
     * 
     * @return
     */
    public Object[] saveState();

    /**
     * Restores state that was saved prior to indexing that might have been altered.
     * 
     * @see #saveState()
     * @param pack
     */
    public void restoreState(Object[] pack);

    /**
     * Triggers the Solr optimize index function on the given server
     * 
     * @param server
     * @throws ServiceException
     * @throws IOException
     */
    public void optimizeIndex(SolrServer server) throws ServiceException, IOException;

}