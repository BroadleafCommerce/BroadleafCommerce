/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.service.solr.indexer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.zookeeper.ZooKeeper;
import org.broadleafcommerce.core.util.lock.ReentrantDistributedZookeeperLock;
import org.broadleafcommerce.core.util.queue.ZookeeperDistributedQueue;
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
 * obtain a lock. This is to prevent two threads, even across nodes, from issuing commands to Solr that might interfere with 
 * one another (e.g. re-aliasing or committing).
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
    protected static final Map<String, BlockingQueue<? super SolrUpdateCommand>> QUEUE_REGISTRY = Collections.synchronizedMap(new HashMap<String, BlockingQueue<? super SolrUpdateCommand>>());
    protected static final Map<String, Lock> LOCK_REGISTRY = Collections.synchronizedMap(new HashMap<String, Lock>());
    
    public static final int MAX_QUEUE_SIZE = 500;
    public static final String LOCK_PATH = "/solr-index/command-lock";
    public static final String QUEUE_PATH = "/solr-index/command-queue";
    
    private final ZooKeeper zk;
    private final boolean distributed;
    private final Environment env;
    
    public DefaultSolrIndexQueueProvider() {
        this.distributed = false;
        this.zk = null;
        this.env = null;
    }
    
    public DefaultSolrIndexQueueProvider(SolrClient solrClient, Environment env) {
        if (solrClient != null && CloudSolrClient.class.isAssignableFrom(solrClient.getClass())) {
            this.distributed = true;
            this.zk = ((CloudSolrClient)solrClient).getZkStateReader().getZkClient().getSolrZooKeeper();
        } else {
            this.distributed = false;
            this.zk = null;
        }
        
        this.env = env;
    }
    
    public DefaultSolrIndexQueueProvider(ZooKeeper zookeeper, Environment env) {
        this.zk = zookeeper;
        this.env = env;
        if (zk == null) {
            this.distributed = false;
        } else {
            this.distributed = true;
        }
    }

    @Override
    public synchronized BlockingQueue<? super SolrUpdateCommand> createOrRetrieveCommandQueue(String queueName) {
        Assert.hasText(queueName, "Queue name must not be null.");
        BlockingQueue<? super SolrUpdateCommand> queue = QUEUE_REGISTRY.get(queueName);
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

    /**
     * Indicates if this is a distributed environment (e.g. the Lock and Queue are distributed, e.g. backed by Zookeeper.)
     */
    @Override
    public boolean isDistributed() {
        return distributed;
    }
    
    /**
     * Returns the {@link Environment} object, which is used in a distributed situation to determine if the current node or application 
     * can obtain a lock.  This may return null.
     * 
     * @return
     */
    protected Environment getEnvironment() {
        return env;
    }
    
    /**
     * Returns the {@link ZooKeeper} instance that distributes the Lock and the Queue.  This may return null in non-distributed situation.
     * 
     * @return
     */
    protected ZooKeeper getZookeeper() {
        return zk;
    }
    
    protected BlockingQueue<? super SolrUpdateCommand> createLocalQueue(String queueName) {
        LOG.warn("Creating Local Queue for Solr update commands with the name " 
                + queueName 
                + ". This will be thread safe within a single JVM but is unsafe for multiple JVMs.  "
                + "Use SolrCloud and CloudSolrClient to automatically enable a distributed Queue.  "
                + "With CloudSolrClient, Zookeeper will be used as the shared Queue store.");
        return new ArrayBlockingQueue<>(MAX_QUEUE_SIZE);
    }
    
    protected BlockingQueue<? super SolrUpdateCommand> createDistributedQueue(String queueName) {
        return new ZookeeperDistributedQueue<>(QUEUE_PATH + '/' + queueName, getZookeeper(), MAX_QUEUE_SIZE);
    }
    
    protected Lock createLocalLock(String lockName) {
        LOG.warn("Creating Local Lock for lock name " 
                + lockName 
                + ". This will be thread safe within a single JVM but is unsafe for multiple JVMs.  "
                + "Use SolrCloud and CloudSolrClient to automatically enable a distributed lock.  "
                + "With CloudSolrClient, Zookeeper will be used as the shared Lock store.");
        return new ReentrantLock();
    }
    
    protected Lock createDistributedLock(String lockName) {
        return new ReentrantDistributedZookeeperLock(getZookeeper(), LOCK_PATH, lockName, getEnvironment(), null);
    }
    
}
