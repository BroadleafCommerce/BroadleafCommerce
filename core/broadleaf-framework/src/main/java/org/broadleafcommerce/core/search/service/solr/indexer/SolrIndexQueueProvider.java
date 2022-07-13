/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2019 Broadleaf Commerce
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

import org.broadleafcommerce.core.util.lock.DistributedLock;
import org.broadleafcommerce.core.util.lock.ReentrantDistributedZookeeperLock;
import org.broadleafcommerce.core.util.queue.ZookeeperDistributedQueue;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;

/**
 * Provides a FIFO {@link Queue} to hold a series of commands to be executed in sequence.  This also provides a 
 * {@link Lock} so that only one thread can execute commands at any given time.
 * 
 * Note that it's safer to use a distributed Queue and a distributed {@link Lock} so that multiple nodes (JVMs) can't simultaneously 
 * execute commands, potentially overriding or corrupting each other.  A local {@link Queue} and a local {@link Lock} are satisfactory 
 * if users can guarantee that only a single node (JVM) will ever update Solr or receive events (e.g. from the admin) to update Solr.
 * 
 * Additionally, using a distributed lock with a local queue will work as well.
 * 
 * Note that it is ALWAYS better to use a distributed lock such as {@link ReentrantDistributedZookeeperLock}.  Since we 
 * use Solr and therefore Zookeeper, this is a reasonable solution.  Distributed locks help prevent 2 different servers 
 * or threads from 2 or more servers from stepping on each others toes.
 * 
 * Distributed {@link BlockingQueue} instances are great too.  However, they can be more complicated, and may require more 
 * infrastructure (JMS, AMQP, or Kafka, for example).  We provide a {@link BlockingQueue} implementation for Zookeeper called 
 * {@link ZookeeperDistributedQueue} that can be used.  There are some limitations with message size, number of messages, etc. 
 * In other words, Zookeeper might not be the best backing store for a general purpose, high volume Queue implementation, 
 * but it works for smaller, more focused command queue for Solr indexing commands.
 * 
 * As a general rule, we recommend ALWAYS using a {@link DistributedLock} such as {@link ReentrantDistributedZookeeperLock}. 
 * For a {@link BlockingQueue} implementation you can use a local queue such as an {@link ArrayBlockingQueue}, a distributed 
 * {@link ZookeeperDistributedQueue}, or a custom implementation of a distributed {@link BlockingQueue}.  Note that 
 * if you use an {@link ArrayBlockingQueue}, for example, you should still use a distributed lock.
 * 
 * The only time(s) that you should a local lock and local queue is if you can guarantee that only 1 server node will 
 * ever process Solr indexing commands.
 * 
 * When using an {@link ArrayBlockingQueue} with a {@link ReentrantDistributedZookeeperLock}, for example, be aware that 
 * Solr indexing commands may not be executed in order.  This is usually not a problem because they are isolated commands 
 * against specific collections.
 * 
 * @author Kelly Tisdell
 *
 */
public interface SolrIndexQueueProvider {
    
    public static final String COMMAND_LOCK_NAME = "_commandLock";
    public static final String COMMAND_QUEUE_NAME = "_commandQueue";

    /**
     * Returns a {@link BlockingQueue} implementation for queuing Solr index commands.
     * 
     * For the given queueName parameter, this method should return the same {@link BlockingQueue} instance 
     * or an instance that operates on the same (distributed) backing queue store, essentially causing it to act like it's 
     * one instance.
     * 
     * @param queueName
     * @return
     */
    public BlockingQueue<? super SolrUpdateCommand> createOrRetrieveCommandQueue(String queueName);
    
    /**
     * Returns a {@link Lock} implementation locking or serializing specific actions such as processing 
     * Solr index commands.
     * 
     * For the given lockName parameter, this method should return the same {@link Lock} instance 
     * or an instance that operates on the same (distributed) backing lock store, essentially causing it to act like it's 
     * one instance.
     * 
     * @param lockName
     * @return
     */
    public Lock createOrRetrieveCommandLock(String lockName);
    
}
