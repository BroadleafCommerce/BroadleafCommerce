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
/**
 * 
 */
package org.broadleafcommerce.core.search.service.solr.index;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.broadleafcommerce.core.search.service.solr.SolrConfiguration;
import org.broadleafcommerce.core.search.service.solr.SolrHelperService;

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
    
    protected SolrConfiguration solrConfiguration;
    protected SolrIndexService indexService;
    protected SolrHelperService shs;
    
    public GlobalSolrFullReIndexOperation(SolrIndexService indexService, SolrConfiguration solrConfiguration, SolrHelperService shs, boolean errorOnConcurrentReindex) {
        this.solrConfiguration = solrConfiguration;
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
    public SolrClient getSolrServerForIndexing() {
        return solrConfiguration.getReindexServer();
    }

    @Override
    public void beforeCountIndexables() {
        // By default we want to do nothing here
    }

    @Override
    public void afterCountIndexables() {
        // By default we want to do nothing here
    }

    @Override
    public void beforeReadIndexables() {
        // By default we want to do nothing here
    }

    @Override
    public void afterReadIndexables() {
        // By default we want to do nothing here
    }

    @Override
    public void beforeBuildPage() {
        // By default we want to do nothing here
    }

    @Override
    public void afterBuildPage() {
        // By default we want to do nothing here
    }
}
