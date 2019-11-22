package org.broadleafcommerce.core.search.service.solr.indexer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.broadleafcommerce.core.search.service.solr.SolrConfiguration;
import org.broadleafcommerce.core.util.lock.ReentrantDistributedZookeeperLock;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Default component to obtain a command {@link Queue} and a {@link Lock} to access the command queue.
 * The queue should only be accessed by a single thread at a time, and so accessors must first 
 * obtain a lock.
 * 
 * This default implementation evaluates the {@link SolrClient} to determine whether to use a local {@link Queue} 
 * and {@link Lock}, or whether to use a distributed {@link Queue} and {@link Lock}.  If the SolrClient is an 
 * instance of {@link CloudSolrClient} then this will be Zookeeper to manage the lock and the queue.  Otherwise, 
 * it will use a local (non-distributed) lock and queue.
 * 
 * @author Kelly Tisdell
 *
 */
public class DefaultSolrIndexQueueProvider implements SolrIndexQueueProvider {
    
    private static final Log LOG = LogFactory.getLog(DefaultSolrIndexQueueProvider.class);
    private static final String LOCK_PATH = "/solr-index/locks";
    private static final Map<String, BlockingQueue<SolrUpdateCommand>> QUEUE_REGISTRY = Collections.synchronizedMap(new HashMap<String, BlockingQueue<SolrUpdateCommand>>());
    private static final Map<String, Lock> LOCK_REGISTRY = Collections.synchronizedMap(new HashMap<String, Lock>());
    
    protected final SolrConfiguration solrConfiguration;
    private final boolean distributed;
    
    public DefaultSolrIndexQueueProvider(SolrConfiguration solrConfiguration) {
        Assert.notNull(solrConfiguration, "SolrConfiguration cannot be null");
        Assert.notNull(solrConfiguration.getReindexServer(), "The Reindex SolrClient cannot be null.");
        
        if (solrConfiguration.isSolrCloudMode()) {
            this.distributed = true;
        } else {
            this.distributed = false;
        }
        
        this.solrConfiguration = solrConfiguration;
    }
    

    @Override
    public synchronized BlockingQueue<SolrUpdateCommand> createOrRetrieveCommandQueue(String queueName) {
        Assert.hasText(queueName, "Queue name must not be null.");
        BlockingQueue<SolrUpdateCommand> queue = QUEUE_REGISTRY.get(queueName);
        if (queue == null) {
            if (isDistributed()) {
                queue = createDistributedQueue(queueName);
            } else {
                queue = createLocalQueue(queueName);
            }
            QUEUE_REGISTRY.put(queueName, queue);
        }
        return queue;
    }

    @Override
    public synchronized Lock createOrRetrieveCommandLock(String lockName) {
        Assert.hasText(lockName, "Lock name must not be null.");
        Lock lock = LOCK_REGISTRY.get(lockName);
        if (lock == null) {
            if (isDistributed()) {
                lock = createDistributedLock(lockName);
            } else {
                lock = createLocalLock(lockName);
            }
            LOCK_REGISTRY.put(lockName, lock);
        }
        return lock;
    }

    @Override
    public boolean isDistributed() {
        return distributed;
    }
    
    protected BlockingQueue<SolrUpdateCommand> createLocalQueue(String queueName) {
        return new ArrayBlockingQueue<>(1000);
    }
    
    protected BlockingQueue<SolrUpdateCommand> createDistributedQueue(String queueName) {
        return null;
    }
    
    protected Lock createLocalLock(String lockName) {
        LOG.warn("Creating Local Lock for lock name " 
                + lockName 
                + ". This will be thread safe within a single JVM but is unsafe for multiple JVMs.  Use SolrCloud and CloudSolrClient to enable automatically enable a distributed lock.");
        return new ReentrantLock();
    }
    
    protected Lock createDistributedLock(String lockName) {
        return new ReentrantDistributedZookeeperLock(((CloudSolrClient)solrConfiguration.getReindexServer()).getZkStateReader().getZkClient(), LOCK_PATH, lockName);
    }
    
}
