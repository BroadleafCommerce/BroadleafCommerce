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
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.search.service.solr.SolrContext;

import java.io.IOException;
import java.util.HashSet;


/**
 *  Builds an index for the current {@link Site} from {@link BroadleafRequestContext#getNonPersistentSite()}. This builds
 *  the index directly on the primary {@link SolrServer} (obtained via {@link SolrContext#getServer()}) so this operation
 *  should only be done once to avoid duplicate documents in the index.
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public abstract class SiteSolrIndexOperation implements SolrIndexOperation {

    private static final Log LOG = LogFactory.getLog(SiteSolrIndexOperation.class);
    
    protected SolrIndexService indexService;
    
    //Holds site ids that are currently being indexed
    protected HashSet<Long> currentReindexProcesses;
    
    protected boolean errorOnConcurrentReindex;
    
    public SiteSolrIndexOperation(SolrIndexService indexService, HashSet<Long> currentReindexProcesses, boolean errorOnConcurrentReindex) {
        this.indexService = indexService;
        this.currentReindexProcesses = currentReindexProcesses;
        this.errorOnConcurrentReindex = errorOnConcurrentReindex;
    }
    
    @Override
    public boolean obtainLock() {
        synchronized (currentReindexProcesses) {
            Long siteId = null;
            BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
            if (brc != null && brc.getNonPersistentSite() != null) {
                siteId = brc.getNonPersistentSite().getId();
            }
            
            // even if siteId is null (no active site) then this should be ok, still locking
            if (currentReindexProcesses.add(siteId)) {
                // successfully indicated that I'm now indexing the current site, all good
                return true;
            } else if (errorOnConcurrentReindex) {
                throw new IllegalStateException("More than one thread attempting to concurrently create an initial Solr index for site ID "
                        + siteId);
            } else {
                LOG.warn("There is more than one thread attempting to concurrently create an initial Solr index for site ID "
                        + siteId
                        + ". Failing additional threads gracefully. Check your configuration.");
                return false;
            }
        }
    }
    
    @Override
    public void releaseLock() {
        synchronized (currentReindexProcesses) {
            Long siteId = null;
            BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
            if (brc != null && brc.getNonPersistentSite() != null) {
                siteId = brc.getNonPersistentSite().getId();
            }
            currentReindexProcesses.remove(siteId);
        }
    }

    @Override
    public SolrServer getSolrServerForIndexing() {
        return SolrContext.getServer();
    }

    @Override
    public void beforeIndexOperation() {
        // unimplemented
    }

    @Override
    public void afterIndexOperation() throws ServiceException, IOException {
        indexService.optimizeIndex(getSolrServerForIndexing());
    }
}
