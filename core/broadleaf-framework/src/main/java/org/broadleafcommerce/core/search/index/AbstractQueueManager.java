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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Kelly Tisdell
 *
 * @param <T>
 */
public abstract class AbstractQueueManager<T> implements QueueManager<T>, ApplicationContextAware {
    private static final Log LOG = LogFactory.getLog(AbstractQueueManager.class);
    private static final Map<String, QueueManager<?>> QUEUE_NAMES_IN_USE = new HashMap<>();
    private boolean initialized = false;
    private boolean queueProducerStarted = false;
    protected ApplicationContext ctx;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

    @Override
    public final synchronized void initialize() {
        if (!initialized) {
            synchronized(QUEUE_NAMES_IN_USE) {
                if (QUEUE_NAMES_IN_USE.containsKey(getQueueName())) {
                    throw new IllegalStateException("A QueueManager with the queueName of " + getQueueName() 
                        + " was already in use. Ensure it is stopped first.");
                }
                
                QUEUE_NAMES_IN_USE.put(getQueueName(), this);
            }
            
            initializeInternal(); //Allow subclasses to do what they need.
            
            //Check all of the components.
            Assert.notNull(getQueueLoader(), "Call to " + getClass().getName() 
                    + ".getQueueLoader() returned null.  It must return an instance of QueueLoader.");
            
            
            this.initialized = true;
        } else {
            LOG.warn("QueueManager was already started for queue: " + getQueueName());
        }
    }
    
    @Override
    public final synchronized void close() {
        if (initialized) {
            if (!isActive()) {
                closeInternal();
                synchronized(QUEUE_NAMES_IN_USE) {
                    QUEUE_NAMES_IN_USE.remove(getQueueName());
                }
                initialized = false;
                queueProducerStarted = false;
            } else {
                LOG.warn("QueueManager for queue " + getQueueName() + " cannot be closed because the QueueLoader thread is still "
                        + "running. Use SearchIndexProcessStateHolder.failFast to forcefully stop the process and then call " 
                        + getClass().getName() + ".close() again.");
            }
        }
    }

    @Override
    public final synchronized void startQueueProducer() {
        if (!initialized) {
            throw new IllegalStateException("QueueManager (" + getClass().getName() + ") was not initialized.");
        }
        if (queueProducerStarted) {
            throw new IllegalStateException("QueueLoader (" + getClass().getName() + ") was already started.");
        }
        Thread t = new Thread(getQueueLoader(), getClass().getName() + "-" + getQueueName());
        t.start();
        queueProducerStarted = true;
    }

    @Override
    public final synchronized void startQueueProducer(TaskExecutor t) {
        if (!initialized) {
            throw new IllegalStateException("QueueManager (" + getClass().getName() + ") was not initialized.");
        }
        if (queueProducerStarted) {
            throw new IllegalStateException("QueueLoader (" + getClass().getName() + ") was already started.");
        }
        t.execute(getQueueLoader());
        queueProducerStarted = true;
    }
    
    @Override
    public final synchronized boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Indicates if the QueueProducer has been started in a background thread.
     * @return
     */
    protected final boolean isQueueProducerStarted() {
        return queueProducerStarted;
    }
    
    /**
     * Hook point to allow any additional initialization.  This is called by the initialize method and is guaranteed to 
     * only be invoked once. A RuntimeException should be thrown from this method if there is an issue initializing.
     * @param processId
     */
    protected void initializeInternal() {
        //Nothing...
    }
    
    /**
     * Hook point to allow any additional cleanup.
     */
    protected void closeInternal() {
        //Nothing...
    }
    
}
