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
public interface QueueLoader<T> extends Runnable {
    
    /**
     * Indicates that there is nothing else that will be added to the queue during this process.  Since the QueueLoader 
     * is single threaded, and has the responsibility of populating a queue with a finite set of data, when there is nothing 
     * else to add to the queue, it is considered complete.
     * 
     * @return
     */
    public boolean isComplete();
    
    /**
     * The return of this method indicates whether the queue is:
     * 1. Active (meaning that there is more to add to the queue and/or the queue is not empty)
     * 2. And, the process has not failed @see {@link SearchIndexProcessStateHolder}
     * 
     * @return
     */
    public boolean isActive();
    
    /**
     * Indicates if the queue (at the time of the method invocation) is empty.
     * @return
     */
    public boolean isEmpty();
    
    
}
