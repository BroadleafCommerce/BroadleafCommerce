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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServer;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.search.service.solr.SolrContext;
import org.broadleafcommerce.core.search.service.solr.SolrHelperService;

import java.io.IOException;


/**
 * Rebuilds the entire Solr index. This type of indexing operation prevents other threads from performing any other global
 * reindex operation. The rebuild operation is done on {@link SolrContext#getReindexServer()} and then at the end, the
 * Solr cores are swapped. Prior to building the index, all documents from {@link SolrContext#getReindexServer()} are
 * deleted.
 * 
 * @see {@link SolrHelperService#swapActiveCores()}
 * @author Phillip Verheyden (phillipuniverse)
 */
public abstract class GlobalSolrFullReIndexOperation implements SolrIndexOperation {

    private static final Log LOG = LogFactory.getLog(GlobalSolrFullReIndexOperation.class);
    
    protected final static Object LOCK_OBJECT = new Object();
    protected static boolean IS_LOCKED;
    protected boolean errorOnConcurrentReIndex;
    
    protected SolrIndexService indexService;
    protected SolrHelperService shs;
    
    public GlobalSolrFullReIndexOperation(SolrIndexService indexService, SolrHelperService shs, boolean errorOnConcurrentReindex) {
        this.indexService = indexService;
        this.shs = shs;
        this.errorOnConcurrentReIndex = errorOnConcurrentReindex;
    }
    
    @Override
    public boolean obtainLock() {
        synchronized (LOCK_OBJECT) {
            if (IS_LOCKED) {
                if (errorOnConcurrentReIndex) {
                    throw new IllegalStateException("More than one thread attempting to concurrently reindex Solr.");
                } else {
                    LOG.warn("There is more than one thread attempting to concurrently "
                            + "reindex Solr. Failing additional threads gracefully. Check your configuration.");
                    return false;
                }
            } else {
                IS_LOCKED = true;
                return IS_LOCKED;
            }
        }
    }
    
    @Override
    public void releaseLock() {
        synchronized (LOCK_OBJECT) {
            IS_LOCKED = false;
        }
    }

    @Override
    public SolrServer getSolrServerForIndexing() {
        return SolrContext.getReindexServer();
    }

    @Override
    public void beforeIndexOperation() throws ServiceException {
        LOG.info("Deleting the reindex core prior to rebuilding the index");
        indexService.deleteAllNamespaceDocuments(getSolrServerForIndexing());
    }

    @Override
    public void afterIndexOperation() throws ServiceException, IOException {
        indexService.optimizeIndex(getSolrServerForIndexing());
        // Swap the active and the reindex cores
        shs.swapActiveCores();
    }
}
