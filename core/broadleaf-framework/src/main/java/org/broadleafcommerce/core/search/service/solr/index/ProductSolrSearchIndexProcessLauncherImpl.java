package org.broadleafcommerce.core.search.service.solr.index;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.index.BatchMarker;
import org.broadleafcommerce.core.search.index.BatchReader;
import org.broadleafcommerce.core.search.index.LockService;
import org.broadleafcommerce.core.search.index.QueueManager;
import org.broadleafcommerce.core.search.index.SingleJvmBlockingQueueManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
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
public class ProductSolrSearchIndexProcessLauncherImpl extends AbstractSolrIndexProcessLauncherImpl<Product> {
    
    protected static final String DEFAULT_QUEUE_NAME = "productQueue";
    
    /*
     * This is likely an alias. It could be a shared LockService.
     * But giving it its own name is nice because it adds flexibility if there are more than one 
     * LockServices defined for various indexing tasks.
     */
    @Resource(name="blProductSearchIndexLockService")
    protected LockService lockService;
    
    /*
     * This is likely an alias. It could be a shared TaskExecutor.
     * But giving it its own name is nice because it adds flexibility if there are more than one 
     * TaskExecutor defined for various indexing tasks.
     */
    @Autowired(required=false)
    @Qualifier("blProductSearchIndexTaskExecutor") 
    protected TaskExecutor taskExecutor;
    
    @Resource(name="blSolrUtil")
    protected SolrUtil solrUtil;
    
    @Value("${org.broadleafcommerce.core.search.service.solr.index.productPrimaryAliasName:catalog}")
    protected String primaryAliasName;
    
    @Value("${org.broadleafcommerce.core.search.service.solr.index.productSecondaryAliasName:catalog_reindex}")
    protected String secondaryAliasName;
    
    @Resource(name="blProductIndexBatchReader")
    protected BatchReader<BatchMarker> batchReader;

    @Override
    protected FieldEntity determineFieldEntity() {
        return FieldEntity.PRODUCT;
    }

    @Override
    protected SolrUtil getSolrUtil() {
        return solrUtil;
    }

    @Override
    protected String getPrimaryAliasName() {
        return primaryAliasName;
    }

    @Override
    protected String getSecondaryAliasName() {
        return secondaryAliasName;
    }

    @Override
    protected LockService getLockService() {
        return lockService;
    }
    
    protected String getQueueName() {
        return DEFAULT_QUEUE_NAME;
    }

    @Override
    protected QueueManager<?> createQueueManager(String processId) {
        return new SingleJvmBlockingQueueManager<>(getQueueName(), batchReader);
    }
    
}
