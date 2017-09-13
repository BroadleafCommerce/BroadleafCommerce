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

import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Uses an ArrayBlockingQueue to manage an in-memory queue of items.  This is Runnable, so this can be 
 * assigned to a thread.  The run method will populate the queue in a background thread.
 * 
 * This should only be used in single JVM mode as the queue is not a distributed queue. Instances of this 
 * component SHOULD NOT be singleton.  However, this is thread safe, and there should be a new instance 
 * per queue.
 * 
 * @author Kelly Tisdell
 *
 */
public abstract class AbstractSingleJvmBatchQueueManager<T> implements QueueProducer<T>, QueueConsumer<T> {
    
    protected final String processId;
    protected final ArrayBlockingQueue<T> queue;
    private boolean complete = false;
    
    /**
     * Default constructor uses a default max queue size of 10000, with a fairness policy of true.
     */
    public AbstractSingleJvmBatchQueueManager(String processId) {
        this(processId, 10000);
    }
    
    public AbstractSingleJvmBatchQueueManager(String processId, int maxQueueSize) {
        Assert.notNull(processId, "processId cannot be null.  "
                    + "This should be an object that is shared across multiple threads.");
        
        Assert.isTrue(maxQueueSize > 0, "maxQueueSize must be greater than 0.  Consider using a maxQueueSize around 10000. "
                + "This should be tuned for your specific memory requirements.");
        
        this.processId = processId;
        this.queue = new ArrayBlockingQueue<>(maxQueueSize, true);
    }

    @Override
    public void run() {
        while (true) {
            //Check to see if something is failed.
            if (ReindexProcessStateHolder.isFailed(processId) || isComplete()) {
                return;
            }
            List<T> itemsToAdd = readNextBatch();
            if (itemsToAdd != null && !itemsToAdd.isEmpty()) {
                for (T item : itemsToAdd) {
                    try {
                        boolean success = false;
                        while (!success) {
                            //Check to see if something is failed.
                            if (ReindexProcessStateHolder.isFailed(processId)) {
                                return;
                            }
                            
                            success = put(item, 1000L, TimeUnit.MILLISECONDS);
                        }
                    } catch (InterruptedException e) {
                        //Mark the process as failed.
                        ReindexProcessStateHolder.failFast(processId, e);
                        return;
                    }
                }
            } else {
                markComplete();
            }
        }
    }

    @Override
    public boolean put(T payload) throws InterruptedException {
        return queue.offer(payload);
    }
    
    @Override
    public boolean put(T payload, long timeout, TimeUnit timeUnit) throws InterruptedException {
        //This will block when the queue is full, until another thread has removed something from the Queue.
        //This helps prevent OOM errors where the queue fills up in an unbounded way.
        return queue.offer(payload, timeout, timeUnit);
    }

    @Override
    public T consume() throws InterruptedException {
        if (isQueueExpired()) {
            return null;
        }
        return queue.poll();
    }

    @Override
    public T consume(long timeout, TimeUnit timeUnit) throws InterruptedException {
        if (isQueueExpired()) {
            return null;
        }
        return queue.poll(timeout, timeUnit);
    }
    
    @Override
    public boolean isQueueExpired() {
        return (isComplete() && queue.isEmpty()) || ReindexProcessStateHolder.isFailed(processId);
    }

    @Override
    public synchronized boolean isComplete() {
        return complete;
    }
    
    @Override
    public final boolean isDistributed() {
        return false;
    }
    
    /**
     * Marks this queue as being complete. In other words, nothing more will be added to the queue. This does not indicate 
     * that the queue is empty.  Only that there is nothing more to add.
     * 
     * Implementors of readNextBatch() may also call this when there is nothing else to return.
     */
    protected synchronized void markComplete() {
        complete = true;
    }
    
    /**
     * Returns a list of items to add to the queue.  Implementors MUST return null, an empty list, 
     * or call markComplete() when there are no additional items to add.
     * @return
     */
    protected abstract List<T> readNextBatch();
}
