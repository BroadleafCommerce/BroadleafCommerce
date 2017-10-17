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
package org.broadleafcommerce.core.catalog.index;

import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.index.BatchMarker;
import org.broadleafcommerce.core.search.index.BatchReader;
import org.broadleafcommerce.core.search.index.LockService;
import org.broadleafcommerce.core.search.index.QueueEntryProcessor;
import org.broadleafcommerce.core.search.index.QueueManager;
import org.broadleafcommerce.core.search.index.SingleJvmBlockingQueueManager;
import org.broadleafcommerce.core.search.service.solr.index.AbstractSolrIndexProcessLauncherImpl;
import org.broadleafcommerce.core.search.service.solr.index.SolrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Concrete implementation of AbstractSolrIndexProcessLauncherImpl, specifically for indexing or 
 * reindexing products in Solr.
 * 
 * @author Kelly Tisdell
 *
 */
@Component("blProductSolrSearchIndexProcessLauncher")
public class ProductSolrSearchIndexProcessLauncherImpl extends AbstractSolrIndexProcessLauncherImpl<BatchMarker> {
    
    protected static final String DEFAULT_CATALOG_PRIMARY_ALIAS = "catalog";
    protected static final String DEFAULT_CATALOG_SECONDARY_ALIAS = "catalog_reindex";
    protected static final String DEFAULT_QUEUE_NAME = "productQueue";
    
    @Resource(name="blSearchIndexLockService")
    protected LockService lockService;
    
    @Autowired(required=false)
    @Qualifier("blSearchIndexTaskExecutor")
    protected ThreadPoolTaskExecutor taskExecutor;
    
    @Resource(name="blSolrUtil")
    protected SolrUtil solrUtil;
    
    @Resource(name="blProductIndexBatchReader")
    protected BatchReader<BatchMarker> batchReader;
    
    @Resource(name="blProductSolrSearchIndexProcessor")
    protected QueueEntryProcessor<BatchMarker> processor;

    @Override
    protected FieldEntity determineFieldEntity() {
        return FieldEntity.PRODUCT;
    }
    
    @Override
    protected String getPrimaryAliasName() {
        return DEFAULT_CATALOG_PRIMARY_ALIAS;
    }

    @Override
    protected String getSecondaryAliasName() {
        return DEFAULT_CATALOG_SECONDARY_ALIAS;
    }
    
    protected String getQueueName() {
        return DEFAULT_QUEUE_NAME;
    }

    @Override
    protected SolrUtil getSolrUtil() {
        return solrUtil;
    }
    
    @Override
    protected LockService getLockService() {
        return lockService;
    }

    @Override
    protected QueueManager<BatchMarker> createQueueManager(String processId) {
        return new SingleJvmBlockingQueueManager<>(processId, getQueueName(), batchReader);
    }

    @Override
    protected QueueEntryProcessor<BatchMarker> getQueueEntryProcessor() {
        return processor;
    }

    @Override
    protected ThreadPoolTaskExecutor getTaskExecutor() {
        return taskExecutor;
    }
}
