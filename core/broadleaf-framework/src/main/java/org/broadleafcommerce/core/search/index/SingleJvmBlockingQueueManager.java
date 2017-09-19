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

import org.springframework.util.Assert;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * In-JVM QueueManager that uses an ArrayBlockingQueue.  This is not for use with distributed processes, but works great 
 * for single-VM, multi-threaded processes.
 * 
 * @author Kelly Tisdell
 *
 * @param <T>
 */
public class SingleJvmBlockingQueueManager<T> extends AbstractQueueManager<T> {
    
    protected static final long DEFAULT_POLL_TIME = 1000L;
    protected static final int DEFAULT_MAX_QUEUE_CAPACITY = 10000;
    
    protected final int maxCapacity;
    
    protected final boolean fair;
    
    protected final String queueName;
    
    protected final BatchReader<T> batchReader;
    
    protected String processId;
    
    protected QueueLoader<T> queueLoader;
    
    protected ArrayBlockingQueue<T> queue;
    
    public SingleJvmBlockingQueueManager(String queueName, BatchReader<T> batchReader) {
        this(queueName, DEFAULT_MAX_QUEUE_CAPACITY, batchReader);
    }
    
    public SingleJvmBlockingQueueManager(String queueName, int maxCapacity, BatchReader<T> batchReader) {
        this(queueName, maxCapacity, true, batchReader);
    }
    
    public SingleJvmBlockingQueueManager(String queueName, int maxCapacity, boolean fair, BatchReader<T> batchReader) {
        this.queueName = queueName;
        this.maxCapacity = maxCapacity;
        this.fair = fair;
        this.batchReader = batchReader;
        Assert.notNull(queueName, "The queueName cannot be null");
        Assert.notNull(batchReader, "The batchReader cannot be null.");
        Assert.isTrue(maxCapacity > 0, "The maxCapacity must be greater than 0. Default is " + DEFAULT_MAX_QUEUE_CAPACITY);
    }

    @Override
    public QueueLoader<T> getQueueLoader() {
        return queueLoader;
    }

    @Override
    public final boolean isDistributed() {
        //By definition this is in-JVM.  This is not distributed, and so it is final.
        return false;
    }

    @Override
    public synchronized boolean isQueueEmpty() {
        if (queue == null) {
            return true;
        }
        return queue.isEmpty();
    }

    @Override
    protected synchronized void initializeInternal(String processId) {
        this.queue = createQueue();
        this.batchReader.reset();
        this.processId = processId;
        Assert.notNull(processId, "The processId cannot be null.");
        this.queueLoader = createQueueLoader();
    }
    
    @Override
    protected synchronized void closeInternal(String processId) {
        this.queue.clear();
        this.batchReader.reset();
    }

    @Override
    public final String getQueueName() {
        return queueName;
    }
    
    protected ArrayBlockingQueue<T> createQueue() {
        return new ArrayBlockingQueue<>(maxCapacity, fair);
    }
    
    protected QueueLoader<T> createQueueLoader() {
        return new SingleJvmBlockingQueueLoader<>(processId, queue, batchReader);
    }
    
    /**
     * Time to wait for an entry to become available in the queue.  The default is 1000 (1 second).  This is backed by 
     * an ArrayBlockingQueue, so this affects the poll method, which blocks and waits for this amount of time for something 
     * to become available. Otherwise, it returns null.
     * 
     * @return
     */
    protected long getPollTime() {
        return DEFAULT_POLL_TIME;
    }

    @Override
    public synchronized boolean isActive() {
        if (isInitialized() && queueLoader != null) {
            return queueLoader.isActive();
        }
        return false;
    }

    /*
     * This method polls the queue for a determined period of time.  If nothing is available, it checks to make 
     * sure that no errors were raised by other threads and that the queue is still active (meaning that it is still being 
     * filled by the QueueLoader). Typically, a call to this method will return a value, unless an error has occured or 
     * unless the queue is empty and nothing else will be added.
     */
    @Override
    public final T consume() {
        T val;
        while (true) {
            if (isActive()) {
                try {
                    val = queue.poll(getPollTime(), TimeUnit.MILLISECONDS);
                    if (val != null) {
                        return val;
                    }
                } catch (InterruptedException e) {
                    SearchIndexProcessStateHolder.failFast(processId, e);
                    return null;
                }
            } else {
                return null;
            }
        }
    }
    
}
