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
package org.broadleafcommerce.core.search.index;

import org.springframework.core.task.TaskExecutor;

/**
 * There are 2 sides to a queue: an entry and an exit.  As a result, for every Queue concept there typically needs to 
 * be something adding to the queue and something removing from it.  We're not trying to reinvent JMS or AMQP here.  Rather, 
 * we are simply trying to provide some simple abstractions that can be used for the purpose of reindexing.  The underlying 
 * Queuing system may be backed by JMS, AMQP, Apache Kafka, a java.util.concurrent.ArrayBlockingQueue, etc.  For efficiency 
 * reasons, though, the there needs to be something that can read batch data, add to a queue independently of the control 
 * thread or the consumers.
 * 
 * In particular, we need a single-threaded data reader that fills a Queue until there is nothing left to put in the Queue, 
 * and then potentially multiple Queue consumer threads that read and process the data.  Implementors of this interface 
 * provide access to both ends of that contract - the QueueReader and the QueueLoader.
 * 
 * Components implementing this interface are NOT meant to be singletons.  There should be one instance per queue name 
 * or a new instance for every queue.  Each QueueManager can only be used by a single index process at a time.
 * 
 * @author Kelly Tisdell
 *
 */
public interface QueueManager<T> extends QueueReader<T> {

    /**
     * Returns the QueueLoader associated with this QueueManager
     * @return
     */
    public QueueLoader<T> getQueueLoader();
    
    /**
     * Returns the Queue name associated with both the QueueLoader and QueueReader.
     * 
     * @return
     */
    public String getQueueName();
    
    /**
     * Lifecycle method to initialize this QueueManager.  This should initialize connections to the underlying queue, 
     * if needed, and should make instances of the QueueLoader and QueueReader available. This method MUST NOT cause 
     * the queue to be loaded.  However, after a call to this method, the QueueReader should be full initialized and 
     * ready to begin consuming data from the Queue.  A call to startQueueProducer() will be required to begin loading 
     * the Queue with data.
     * 
     */
    public void initialize();
    
    /**
     * This starts the QueueProducer in a new Thread.  Only the 
     * process manager or thread that has the key and reference can start the QueueProducer.
     * @param lockService
     * @param key
     * @param reference
     * @throws LockException
     */
    public void startQueueProducer();
    
    /**
     * Delegates the execution of the QueueProducer to the provided TaskExecutor.
     * 
     * @param t
     * 
     */
    public void startQueueProducer(TaskExecutor t);
    
    /**
     * Lifecycle method to allow the closing and cleanup of resources.
     * 
     */
    public void close();
    
    /**
     * Method to indicate that this component was successfully initialized by a call to initialize().
     * @return
     */
    public boolean isInitialized();
    
    /**
     * A convenience method to indicate whether the queue has any items in it at a given moment.
     * @return
     */
    public boolean isQueueEmpty();
    
    /**
     * Indicates if there is nothing left to take from the queue.
     */
    public boolean isActive();
    
}
