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

import org.apache.solr.client.solrj.SolrClient;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import java.util.List;


/**
 *  Defines the lifecylce of an indexing operation used in {@link SolrIndexService}. Each of the methods in this interface
 *  are executed in order during different phases of {@link SolrIndexService#executeSolrIndexOperation(SolrIndexOperation)}.
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface SolrIndexOperation {

    /**
     * Grab some sort of lock so that nothing else can index items at the same time
     */
    public boolean obtainLock();
    
    /**
     * Which {@link SolrClient} the index should be built on
     */
    public SolrClient getSolrServerForIndexing();

    /**
     * Executes before the count, this is where any filters or setup for counting can be taken care of
     */
    public void beforeCountIndexables();
    
    /**
     * The count of all of the {@link Indexable} items about to be indexed. Used to determine paging used by
     * {@link #readIndexables(int, int)}
     * @throws ServiceException
     */
    public Long countIndexables() throws ServiceException;

    /**
     * Executes after the count, this is where any filters or cleanup for counting can be taken care of
     */
    public void afterCountIndexables();

    /**
     * Executes before the read, this is where any filters or setup for reading can be taken care of
     */
    public void beforeReadIndexables();

    /**
     * Perform the a read of the {@link Indexable} items for a particular page and pageSize
     * @throws ServiceException
     */
    public List<? extends Indexable> readIndexables(int pageSize, Long lastId) throws ServiceException;

    /**
     * Executes after the read, this is where any filters or cleanup for reading can be taken care of
     */
    public void afterReadIndexables();


    /**
     * Executes before building each page, this is where any filters or setup for building can be taken care of
     */
    public void beforeBuildPage();

    /**
     * Build a page from {@link #readIndexables(int, Long)} on the {@link #getSolrServerForIndexing()}. This is used as a
     * wrapper extension around {@link SolrIndexService#buildIncrementalIndex(List, SolrClient)}.
     */
    public void buildPage(List<? extends Indexable> indexables) throws ServiceException;

    /**
     * Executes after building each page, this is where any filters or cleanup for building can be taken care of
     */
    public void afterBuildPage();
    
    /**
     * If a lock was obtained in {@link #obtainLock()} this releases it
     */
    public void releaseLock();
    
}
