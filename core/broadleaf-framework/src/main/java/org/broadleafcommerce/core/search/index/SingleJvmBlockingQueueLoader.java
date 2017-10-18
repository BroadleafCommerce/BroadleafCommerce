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

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Uses an ArrayBlockingQueue to manage an in-memory queue of items.  This is Runnable, so this can be 
 * assigned to a thread.  The run method will populate the queue in a background thread.
 * 
 * @author Kelly Tisdell
 *
 * @param <T>
 */
public class SingleJvmBlockingQueueLoader<T> implements QueueLoader<T> {
    
    protected final static long DEFAULT_PUT_WAIT_TIME = 1000L;
    protected final String processId;
    protected final BatchReader<T> batchReader;
    protected final ArrayBlockingQueue<T> queue;
    private boolean complete = false;
    private boolean started = false;
    
    public SingleJvmBlockingQueueLoader(String processId, ArrayBlockingQueue<T> queue, BatchReader<T> batchReader) {
        this.queue = queue;
        this.batchReader = batchReader;
        this.processId = processId;
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
            throw new IllegalStateException("QueueLoader " 
                    + getClass().getName() + " was already started and cannot be restarted. Please create a new instance.");
        }
        started = true;
        
        while (true) {
            //Check to see if something is failed.
            if (SearchIndexProcessStateHolder.isFailed(processId) || isLoadingComplete()) {
                return;
            }
            try {
                List<T> itemsToAdd = batchReader.readBatch();
                if (itemsToAdd != null && !itemsToAdd.isEmpty()) {
                    for (T item : itemsToAdd) {
                        boolean success = false;
                        while (!success) {
                            //Check to see if something is failed.
                            if (SearchIndexProcessStateHolder.isFailed(processId)) {
                                return;
                            }
                            
                            success = addToQueue(item);
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
    
    @Override
    public boolean addToQueue(T item) {
        try {
            return queue.offer(item, determinePutWaitTime(), TimeUnit.MILLISECONDS);
        } catch(InterruptedException e) {
            throw new RuntimeException("Thread was interrupted trying to add an item to the queue.", e);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.search.index.service.QueueLoader#isComplete()
     */
    @Override
    public synchronized boolean isLoadingComplete() {
        return complete;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.search.index.service.QueueConsumer#isActive()
     */
    @Override
    public boolean isActive() {
        //These are already synchronized...
        return ((!isLoadingComplete() || !queue.isEmpty()) && ! SearchIndexProcessStateHolder.isFailed(processId));
    }

    @Override
    public boolean isEmpty() {
        //This is already synchronized at the Queue level.
        return queue.isEmpty();
    }

    protected long determinePutWaitTime() {
        return DEFAULT_PUT_WAIT_TIME;
    }
    
    private synchronized void markComplete() {
        this.complete = true;
    }
}
