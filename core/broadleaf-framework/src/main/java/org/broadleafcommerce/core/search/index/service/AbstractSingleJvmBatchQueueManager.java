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
 * component SHOULD NOT be singleton.  However, this is thread safe, but there should be a new instance 
 * per queue.  Invoking the run() method more than once per instance will result in an error.
 * 
 * @author Kelly Tisdell
 *
 */
public abstract class AbstractSingleJvmBatchQueueManager<T> implements QueueProducer<T>, QueueConsumer<T> {
    
    protected final static long DEFAULT_PUT_WAIT_TIME = 1000L;
    protected final String processId;
    protected final ArrayBlockingQueue<T> queue;
    private boolean complete = false;
    private boolean started = false;
    
    /**
     * Default constructor uses a default max queue size of 10000, with a fairness policy of true.
     */
    public AbstractSingleJvmBatchQueueManager(String processId) {
        this(processId, 10000);
    }
    
    /**
     * Constructor that takes in the processId and the maxQueueSize.  ProcessId cannot be null and the maxQueueSize 
     * must be greater than 0.
     * 
     * @param processId
     * @param maxQueueSize
     */
    public AbstractSingleJvmBatchQueueManager(String processId, int maxQueueSize) {
        Assert.notNull(processId, "processId cannot be null.  "
                    + "This should be an object that is shared across multiple threads.");
        
        Assert.isTrue(maxQueueSize > 0, "maxQueueSize must be greater than 0.  Consider using a maxQueueSize around 10000. "
                + "This should be tuned for your specific memory requirements.");
        
        this.processId = processId;
        this.queue = new ArrayBlockingQueue<>(maxQueueSize, true);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     * 
     * Invoking the run method more than once per instance will result in an error.
     */
    @Override
    public final void run() {
        if (started) {
            throw new IllegalStateException("QueueProducer " 
                    + getClass().getName() + " was already started and cannot be restarted. Please create a new instance.");
        }
        started = true;
        
        while (true) {
            //Check to see if something is failed.
            if (SearchIndexProcessStateHolder.isFailed(processId) || isComplete()) {
                return;
            }
            try {
                List<T> itemsToAdd = readNextBatch();
                if (itemsToAdd != null && !itemsToAdd.isEmpty()) {
                    for (T item : itemsToAdd) {
                        try {
                            boolean success = false;
                            while (!success) {
                                //Check to see if something is failed.
                                if (SearchIndexProcessStateHolder.isFailed(processId)) {
                                    return;
                                }
                                
                                success = put(item, determinePutWaitTime(), TimeUnit.MILLISECONDS);
                            }
                        } catch (InterruptedException e) {
                            //Mark the process as failed.
                            SearchIndexProcessStateHolder.failFast(processId, e);
                            return;
                        }
                    }
                } else {
                    markComplete();
                }
            } catch (Throwable t) {
                SearchIndexProcessStateHolder.failFast(processId, t);
                return;
            }
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.search.index.service.QueueProducer#put(java.lang.Object, long, java.util.concurrent.TimeUnit)
     */
    @Override
    public boolean put(T payload, long timeout, TimeUnit timeUnit) throws InterruptedException {
        //This will block when the queue is full, until another thread has removed something from the Queue.
        //This helps prevent OOM errors where the queue fills up in an unbounded way.
        return queue.offer(payload, timeout, timeUnit);
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.search.index.service.QueueConsumer#consume()
     */
    @Override
    public T consume() throws InterruptedException {
        if (isQueueExpired()) {
            return null;
        }
        return queue.poll();
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.search.index.service.QueueConsumer#consume(long, java.util.concurrent.TimeUnit)
     */
    @Override
    public T consume(long timeout, TimeUnit timeUnit) throws InterruptedException {
        if (isQueueExpired()) {
            return null;
        }
        return queue.poll(timeout, timeUnit);
    }
    
    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.search.index.service.QueueConsumer#isQueueExpired()
     */
    @Override
    public boolean isQueueExpired() {
        return (isComplete() && queue.isEmpty()) || SearchIndexProcessStateHolder.isFailed(processId);
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.search.index.service.QueueProducer#isComplete()
     */
    @Override
    public synchronized boolean isComplete() {
        return complete;
    }
    
    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.search.index.service.QueueProducer#isDistributed()
     */
    @Override
    public final boolean isDistributed() {
        return false;
    }
    
    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.search.index.service.QueueProducer#initialize()
     */
    @Override
    public void initialize() {
        //Nothing to do.
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.search.index.service.QueueProducer#cleanup()
     */
    @Override
    public void cleanup() {
        //Nothing to do.
    }
    
    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.search.index.service.QueueProducer#getQueueName()
     */
    @Override
    public String getQueueName() {
        return processId;
    }
    
    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.search.index.service.QueueProducer#markComplete()
     * 
     * Implementors of readNextBatch() may also call this when there is nothing else to return.
     */
    public synchronized void markComplete() {
        complete = true;
    }
    
    /**
     * This returns the wait time in millis to do a put in the queue.  This is backed by an ArrayBlockingQueue, which 
     * has a max capacity.  In the event that the queue is full, this will block for the specified number of millis while 
     * waiting for capacity to free up.  Too small a value, and the system will use more CPU as it loops trying to put the 
     * item(s) in the queue.  Too large a value, and it will block for a potentially long period of time and an error 
     * might be raised that is not recognized or seen by this component, and might get overlooked, where it should stop 
     * this component from running.
     * 
     * The default value is 1000 ms (1 second). It is recommended that value be used.
     * 
     * @return
     */
    protected long determinePutWaitTime() {
        return DEFAULT_PUT_WAIT_TIME;
    }
    
    /**
     * Returns a list of items to add to the queue.  Implementors MUST return null, an empty list, 
     * or call markComplete() when there are no additional items to return.
     * @return
     */
    protected abstract List<T> readNextBatch();

}
