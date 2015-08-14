/*
 * #%L
 * BroadleafCommerce Framework
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
/**
 * 
 */
package org.broadleafcommerce.core.search.service.solr.index;

import org.apache.solr.client.solrj.SolrServer;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.catalog.domain.Indexable;

import java.io.IOException;
import java.util.List;


/**
 *  Defines the lifecylce of an indexing operation used in {@link SolrIndexService}. Each of the methods in this interface
 *  are executed in order during different phases of {@link SolrIndexService#rebuildIndex(SolrIndexOperation)}.
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface SolrIndexOperation {

    /**
     * Grab some sort of lock so that nothing else can index items at the same time
     */
    public boolean obtainLock();
    
    /**
     * Which {@link SolrServer} the index should be built on
     */
    public SolrServer getSolrServerForIndexing();

    /**
     * Any processing that is required to be performed prior to the index operation executing
     */
    public void beforeIndexOperation() throws ServiceException;
    
    /**
     * The count of all of the {@link Indexable} items about to be indexed. Used to determine paging used by
     * {@link #readIndexables(int, int)}
     * @throws ServiceException
     */
    public Long countIndexables() throws ServiceException;
    
    /**
     * Perform the a read of the {@link Indexable} items for a particular page and pageSize
     * @throws ServiceException
     */
    public List<? extends Indexable> readIndexables(int page, int pageSize) throws ServiceException;

    /**
     * Build a page from {@link #readIndexables(int, int)} on the {@link #getSolrServerForIndexing()}. This is used as a
     * wrapper extension around {@link SolrIndexService#buildIncrementalIndex(List, SolrServer)}.
     */
    public void buildPage(List<? extends Indexable> indexables) throws ServiceException;
    
    /**
     * After this index operation is complete, usually does a {@link SolrIndexService#optimizeIndex(SolrServer)}
     */
    public void afterIndexOperation() throws ServiceException, IOException;
    
    /**
     * If a lock was obtained in {@link #obtainLock()} this releases it
     */
    public void releaseLock();
    
}
