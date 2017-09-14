/*-
 * #%L
 * BroadleafCommerce Core Solr Components Module
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt).
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */

package org.broadleafcommerce.core.search.index.service;

import org.broadleafcommerce.core.search.domain.FieldEntity;

import java.util.concurrent.TimeUnit;

/**
 * This represents a component that can populate a Queue concept, specifically for the purpose of reindexing a search engine.  
 * The Queue can be any type of queue, whether JMS, AMQP, in memory (e.g. ArrayBlockingQueue), etc.  
 * The previous design of the Search Reindex processes in Broadleaf was single threaded. Some work was done to have the 
 * main thread read batches of Indexables (e.g. Products) and delegate to a thread pool to process.  The main issues with this 
 * were:
 * 1. LazyInitialization (reading batches of top level objects in one thread, and processing them in another)
 * 2. The main thread became responsible for reading batches of objects, and then scheduling the the runnable
 * 
 * This approach is different in that it frees the main control thread from reading batches and scheduling.  Instead it puts 
 * the concept of a blocking queue between the producer of the data and the consumers and allows them to independently 
 * operate as efficiently as possible.  It should be noted that this producer is single threaded, and should be made as 
 * efficient as possible.  The reason is that we are typically reading in batches and it's difficult to coordinate multiple threads 
 * reading a sequential batch of items (e.g. from the DB) without overlapping each other, missing data, or duplicating data.
 * 
 * Typically, implementors of this should populate the Queue with IDs or ID ranges so that the QueueConsumers can read 
 * batches of Indexable items for processing.  After all, it is the sequential batch reading that is difficult.  
 * Implementors could potentially put fully hydrated domain objects on the queue.  However, if those objects require 
 * serialization that can create issues such as LazyInitializationExceptions once deserialized, or serialization 
 * overhead due to deep object graphs.  Additionally, the Queue producer is single threaded, so it needs to be fast so 
 * that there is no thread starvation during multi-threaded consumption of the queue.  Therefore, it is recommended 
 * that implementors of this interface read ID batches so that consumers can read the top level objects based on those IDs.
 * 
 * @author Kelly Tisdell
 *
 * @param <T>
 */
public interface QueueProducer<T> extends Runnable {
    
    /**
     * Puts the object on a queue, typically for consumption by another thread.  A return value of true 
     * means that the put was successful.  A return value of false means that the value could not be put on the 
     * queue in the specified time (often due to the queue having a max capacity).
     * 
     * If this method returns false, it is the implementer's responsibility to retry or fail as needed.
     * 
     * @param payload
     * @param timeout
     * @param timeUnit
     * @return
     * @throws InterruptedException
     */
    public boolean put(T payload, long timeout, TimeUnit timeUnit) throws InterruptedException;
    
    /**
     * Indicates that there is nothing else that will be added to the queue during this process.  Since the QueueProducer 
     * is single threaded, and has the responsibility of populating a queue with a finite set of data, when there is nothing 
     * else to add to the queue, it is considered complete.
     * 
     * @return
     */
    public boolean isComplete();
    
    /**
     * Indicates if this queue producer supports distributed processing.
     * 
     * @return
     */
    public boolean isDistributed();
    
    /**
     * This is the FieldEntity for which this QueueProducer is producing data for consumption.  This should not return null.
     * @return
     */
    public FieldEntity getFieldEntity();
    
    /**
     * A method to return the name of the Queue.
     * @return
     */
    public String getQueueName();
    
    /**
     * Method to allow any necessary configuration, connection to external resources, etc. Calling this implementation more 
     * than once should have no effect.
     */
    public void initialize();
    
    /**
     * Method to allow any necessary cleanup of resources, closing of connections, deletions of queues, etc.  
     * This method does not necessarily stop or interrupt the thread, and may have no effect if the background thread is 
     * still running.  Use the SearchIndexProcessStateHolder.failFast() method to stop a process.
     */
    public void cleanup();
    
    /**
     * Marks this queue as being complete. In other words, nothing more will be added to the queue. This does not indicate 
     * that the queue is empty.  Only that there is nothing more to add.
     */
    public void markComplete();
    
}
