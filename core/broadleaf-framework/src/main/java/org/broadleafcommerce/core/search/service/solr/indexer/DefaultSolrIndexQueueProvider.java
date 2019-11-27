package org.broadleafcommerce.core.search.service.solr.indexer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.broadleafcommerce.core.search.service.solr.SolrConfiguration;
import org.broadleafcommerce.core.util.lock.ReentrantDistributedZookeeperLock;
import org.springframework.core.env.Environment;
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
 * instance of {@link CloudSolrClient} then this will use the associated Zookeeper to manage the lock and the queue.  
 * Otherwise, it will use a local (non-distributed) lock and queue.
 * 
 * This can be extended to provide different queue and lock implementations.  If you override them, then they both need 
 * to be distributed or local.  Having a local lock and a distributed queue or vice versa will not work.
 * 
 * @author Kelly Tisdell
 *
 */
public class DefaultSolrIndexQueueProvider implements SolrIndexQueueProvider {
    
    private static final Log LOG = LogFactory.getLog(DefaultSolrIndexQueueProvider.class);
    private static final Map<String, BlockingQueue<SolrUpdateCommand>> QUEUE_REGISTRY = Collections.synchronizedMap(new HashMap<String, BlockingQueue<SolrUpdateCommand>>());
    private static final Map<String, Lock> LOCK_REGISTRY = Collections.synchronizedMap(new HashMap<String, Lock>());
    
    protected static final int MAX_QUEUE_SIZE = 500;
    protected static final String LOCK_PATH = "/solr-index/command-lock";
    protected static final String QUEUE_PATH = "/solr-index/command-queue";
    
    private final SolrConfiguration solrConfiguration;
    private final boolean distributed;
    private final Environment env;
    
    public DefaultSolrIndexQueueProvider(SolrConfiguration solrConfiguration, Environment env) {
        Assert.notNull(solrConfiguration, "SolrConfiguration cannot be null");
        Assert.notNull(solrConfiguration.getReindexServer(), "The Reindex SolrClient cannot be null.");
        
        if (solrConfiguration.isSolrCloudMode()) {
            this.distributed = true;
        } else {
            this.distributed = false;
        }
        
        this.solrConfiguration = solrConfiguration;
        this.env = env;
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
    
    protected SolrConfiguration getSolrConfiguration() {
        return solrConfiguration;
    }
    
    protected Environment getEnvironment() {
        return env;
    }
    
    protected BlockingQueue<SolrUpdateCommand> createLocalQueue(String queueName) {
        LOG.warn("Creating Local Queue for Solr update commands with the name " 
                + queueName 
                + ". This will be thread safe within a single JVM but is unsafe for multiple JVMs.  "
                + "Use SolrCloud and CloudSolrClient to automatically enable a distributed Queue.  Zookeeper will be used as the shared Queue store.");
        return new ArrayBlockingQueue<>(MAX_QUEUE_SIZE);
    }
    
    protected BlockingQueue<SolrUpdateCommand> createDistributedQueue(String queueName) {
        return null;
    }
    
    protected Lock createLocalLock(String lockName) {
        LOG.warn("Creating Local Lock for lock name " 
                + lockName 
                + ". This will be thread safe within a single JVM but is unsafe for multiple JVMs.  "
                + "Use SolrCloud and CloudSolrClient to automatically enable a distributed lock.  Zookeeper will be used as the shared lock store.");
        return new ReentrantLock();
    }
    
    protected Lock createDistributedLock(String lockName) {
        return new ReentrantDistributedZookeeperLock(((CloudSolrClient)getSolrConfiguration().getReindexServer()).getZkStateReader().getZkClient(), LOCK_PATH, lockName, getEnvironment());
    }
    
}
