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
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.util.BlockingRejectedExecutionHandler;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * This is an abstract component to allow for the full reindexing of a specific index.  This component only operates as a 
 * controller for a multi-threaded process.  The purpose of this is to obtain a process lock. 
 * It then runs a QueueLoader in another thread, which begins to populate a Queue.  
 * It creates a Semaphore to block until all background threads are complete.  It then raises a process started 
 * event (which is a Spring event). 
 * Finally, it waits until the semaphore releases and completes the process.
 * 
 * @author Kelly Tisdell
 *
 * @param <I>
 */
public abstract class AbstractGenericSearchIndexProcessLauncher<I> 
implements SearchIndexProcessLauncher<I>, Runnable, ApplicationContextAware, InitializingBean {
    
    private static final Log LOG = LogFactory.getLog(AbstractGenericSearchIndexProcessLauncher.class);
    private static final Map<String, SearchIndexProcessLauncher<?>> FIELD_ENTITY_REGISTRY = new HashMap<>();
    private static final Map<String, QueueManager<?>> QUEUE_MANAGERS_IN_USE = new HashMap<>();
    protected static final long DEFAULT_PAUSE_TIME = 5000L;
    private long startTime = -1;
    protected ApplicationContext ctx;
    
    /**
     * Method in this class that does the heavy lifting.  However, for the most part, this delegates to the preProcess, process, 
     * and postProcess methods.
     * 
     * This method is responsible for obtaining (and removing) necessary locks and for starting/ending the process state.
     * 
     * It is not recommended that you override this method.  Instead, implement or override the preProcess, process, and postProcess 
     * methods.
     */
    protected final void executeInternally() {
        final String processId = determineProcessId();
        final Serializable key;
        synchronized(this) {
            try {
                //The whole point of a lock is to avoid having 2 threads or even 2 different cluster members 
                //trying to do this at the same time.  Incremental updates are fine without a lock, but we don't want 
                //2 different threads simultaneously kicking off the same process at the same time, which could cause 
                //major state issues in addition to increased resource use and reduced performance.
                
                //The owner of the lock is assumed to be the thread that controls the flow.
                key = getLockService().lock(processId);
                
                startTime = System.currentTimeMillis();
            } catch (Exception e) {
                LOG.error("There was an error obtaining the lock for processId " + processId, e);
                return;
            }
        }
        
        
        try {
            SearchIndexProcessStateHolder.startProcessState(processId, determineFieldEntity());
            QueueManager<I> queueManager = null;
            int count = 0;
            try {
                //Allow for any arbitrary pre-processing.
                preProcess(processId);
                Semaphore semaphore = new Semaphore(0);
                try {
                    queueManager = getQueueManager(processId);
                    SearchIndexProcessStateHolder.setAdditionalProperty(processId, "_QUEUE_MANAGER", queueManager);
                    
                    //This will start a QueueLoader in a background thread.
                    //The purpose is to begin independently populating a Queue so that consumer threads can independently 
                    //and asynchronously consume the messages.
                    queueManager.initialize();
                    if (getTaskExecutor() == null) {
                        queueManager.startQueueProducer();
                    } else {
                        queueManager.startQueueProducer(getTaskExecutor());
                    }
                    
                    String queueName = queueManager.getQueueName();
                    
                    //Now, we need to raise an event so that consumers can begin consuming messages.
                    ctx.publishEvent(new SearchIndexProcessStartedEvent(processId, determineFieldEntity(), queueName));
                    
                    count = process(processId, semaphore);
                    
                } finally {
                    try {
                        semaphore.acquire(count);
                    } catch (InterruptedException e) {
                        LOG.error("Caught exception waiting for Semphore.", e);
                    }
                    
                    if (SearchIndexProcessStateHolder.isFailed(processId)) {
                        if (SearchIndexProcessStateHolder.getFirstFailure(processId) != null) {
                            throw new ServiceException ("An error occured during the indexing process for processId: " 
                                    + processId, SearchIndexProcessStateHolder.getFirstFailure(processId));
                        } else {
                            //This should never happen, but it is an unlikely flow, so we'll code for it.
                            LOG.error("An unexpected failure occured during the Search Index Process for processId " 
                                    + processId + " but no exception was raised or provided.");
                            return;
                        }
                    } else {
                        //Allow for any arbitrary post-processing.
                        postProcessSuccess(processId);
                        
                        //Now, raise an event that the process completed.
                        ctx.publishEvent(new SearchIndexProcessCompletedEvent(processId, determineFieldEntity()));
                    }
                }
                
            } catch (Exception e) {
                try {
                    //There is no need to explicitly raise an event here because the SearchIndexProcessStateHolder 
                    //should have taken care of that.
                    if (SearchIndexProcessStateHolder.isFailed(processId)) {
                        List<Throwable> throwables = SearchIndexProcessStateHolder.getAllFailures(processId);
                        if (throwables != null && !throwables.isEmpty()) {
                            for (Throwable t : throwables) {
                                LOG.error("An error was encountered during a search index process for processId: " 
                                        + processId, t);
                            }
                        }
                    } else {
                        LOG.error("An error occured reindexing with processId " + processId, e);
                        SearchIndexProcessStateHolder.failFast(processId, e);
                    }
                    
                    //Allow for any arbitrary post-processing.
                    postProcessFailure(processId);
                } catch (Exception ie) {
                    LOG.error("An error occured in the catch block.  Trapping and logging here for investigation.", ie);
                }
                
            } finally {
                try {
                    //Allow any background threads to finish what they are doing, realize there was an error, etc.
                    Thread.sleep(determinePauseTime());
                } catch (InterruptedException ie) {
                    //Ignore
                }
                
                try {
                    if (queueManager != null) {
                        queueManager.close();
                    }
                    synchronized(QUEUE_MANAGERS_IN_USE) {
                        QUEUE_MANAGERS_IN_USE.remove(processId);
                    }
                    
                } catch (Exception e) {
                    LOG.error("An error occured trying to cleanup the QueueManager for Queue name: " 
                            + queueManager.getQueueName(), e);
                }
                
                try {
                    SearchIndexProcessStateHolder.endProcessState(processId);
                } catch (Exception e) {
                    LOG.error("Error trying to end process state with processId: " + processId, e);
                }
                
                synchronized (this) {
                    try {
                        getLockService().unlock(key, processId);
                    } catch (LockException e) {
                        LOG.error("There was an error trying to remove the lock for processId " + processId, e);
                    } finally {
                        startTime = -1;
                    }
                }
            }
        } finally {
            //
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.search.index.service.IndexService#rebuildIndex()
     * 
     * The default behavior here is check whether this process is already running, and if not, delegate to a background thread.
     */
    @Override
    public void rebuildIndex() throws ServiceException {
        synchronized(this) {
            if (!isExecuting()) {
                Thread t = new Thread(this, getClass().getName());
                t.start();
            } else {
                throw new ServiceException("The index process is already running for processId " + determineProcessId());
            }
        }
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     * 
     * This simply delegates to the executeInternally() method.
     */
    @Override
    public void run() {
        executeInternally();
    }
    
    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.search.index.service.SearchIndexService#isExecuting()
     */
    @Override
    public boolean isExecuting() {
        synchronized (this) {
            return getLockService().isLocked(determineProcessId());
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.search.index.SearchIndexProcessLauncher#getElapsedTime()
     */
    public long getElapsedTime() {
        synchronized(this) {
            if (startTime < 0L) {
                return -1L;
            } else {
                return (System.currentTimeMillis() - startTime);
            }
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.search.index.service.SearchIndexService#forceStop()
     */
    @Override
    public void forceStop() {
        synchronized(this) {
            if (isExecuting()) {
                try {
                    throw new Exception("A process with processId: " + determineProcessId() + " is being forceably stopped.");
                } catch (Exception e) {
                    SearchIndexProcessStateHolder.failFast(determineProcessId(), e);
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }
    
    /**
     * Method to consume the Queue entry.  This is what will take whatever is on the queue and finish processing.  
     * Typically, this executes in a background thread, if the ThreadPoolTaskExecutor is not null.  Otherwise, it executes 
     * in the current thread.  This also delegates to the provided QueueProcessor, which must not be null.
     * The semaphore keeps track of executions so that the control thread can wait on background threads. 
     * Each individual call to this method must result in a single call to release the semaphore.
     * 
     * @param executor
     * @param processor
     * @param processId
     * @param entry
     * @param semaphore
     */
    protected void processEntry(final ThreadPoolTaskExecutor executor, 
            final QueueEntryProcessor<I> processor, final String processId, final I entry, final Semaphore semaphore) {
        
        if (entry == null) {
            semaphore.release();
            return;
        }
        
        if (!SearchIndexProcessStateHolder.isFailed(processId)) {
            semaphore.release();
            return;
        }
        
        if (executor != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        processor.process(processId, entry);
                    } catch (Throwable t) {
                        SearchIndexProcessStateHolder.failFast(processId, t);
                    } finally {
                        semaphore.release();
                    }
                }
            });
        } else {
            try {
                processor.process(processId, entry);
            } catch (Throwable t) {
                SearchIndexProcessStateHolder.failFast(processId, t);
            } finally {
                semaphore.release();
            }
        }
        
    }
    
    protected int process(final String processId, final Semaphore semaphore) {
        final QueueEntryProcessor<I> processor = getQueueEntryProcessor();
        final ThreadPoolTaskExecutor executor = getTaskExecutor();
        @SuppressWarnings("unchecked")
        final QueueManager<I> queueManager = 
                (QueueManager<I>)SearchIndexProcessStateHolder.getAdditionalProperty(processId, "_QUEUE_MANAGER");
        int count = 0;
        try {
            while(true) {
                I entry = queueManager.consume();
                if (entry != null) {
                    processEntry(executor, processor, processId, entry, semaphore);
                    count++;
                } else if (!queueManager.isActive()) {
                    break;
                }
            }
        } catch (Exception e) {
            SearchIndexProcessStateHolder.failFast(processId, e);
        }
        
        //We must return a count so that the semaphore can release.
        return count;
    }
    
    /**
     * This is the time that the main control thread will pause to allow other threads to finish what they 
     * are doing or catch up.
     * 
     * The default value is 5000 ms (5 seconds).
     * 
     * @return
     */
    protected long determinePauseTime() {
        return DEFAULT_PAUSE_TIME;
    }
    
    /**
     * The default is to return the String representation of the FieldEntity (determineFieldEntity().getType()).  This should 
     * return a consistent value across multiple invocations.  It is recommended that implementors not override this.  However, 
     * this method is made protected to allow it if needed.
     * 
     * @return
     */
    protected String determineProcessId() {
        return determineFieldEntity().getType();
    }
    
    /**
     * Method to allow implementors to return a specific ThreadPoolTaskExecutor.  This method MAY return null.
     * 
     * This component runs itself in a background thread when the rebuildIndex() method is called.  This component also 
     * delegates to a background thread to run the QueueLoader.  If you do return a task executor, make sure it has 
     * enough threads to handle the QueueLoader and the worker threads for the QueueConsumer.
     * 
     * One warning about this - the ThreadPoolTaskExecutor is not closed, destroyed, or otherwise cleaned up.  If Spring provides 
     * the ThreadPoolTaskExecutor, Spring should be responsible for the lifecycle.  If the subclass creates the thread pool, it should 
     * clean up the ThreadPoolTaskExecutor in both of the postProcess methods, or as part of the lifecycle of the implementing bean.
     * 
     * It is strongly recommended that implementations return a ThreadPoolTaskExecutor, and that the 
     * configuration of it include the {@link BlockingRejectedExecutionHandler}.
     * 
     * @return
     */
    protected ThreadPoolTaskExecutor getTaskExecutor() {
        //Default is to return null
        return null;
    }
    
    /**
     * Optional hook point called by afterPropertiesSet.  This gets invoked once by Spring when the bean is created.
     */
    protected void initialize() {
        //Nothing
    }
    
    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public final void afterPropertiesSet() throws Exception {
        if (determineFieldEntity() == null) {
            throw new IllegalStateException("FieldEntity was null.");
        }
        
        synchronized(AbstractGenericSearchIndexProcessLauncher.class) {
            if (FIELD_ENTITY_REGISTRY.containsKey(determineFieldEntity().getType())) {
                throw new IllegalStateException(getClass().getName() 
                        + " attempted to register itself with"
                        + " FieldEntity type of " + determineFieldEntity().getType() 
                        + " but that was already registered to "
                        + FIELD_ENTITY_REGISTRY.get(determineFieldEntity().getType()).getClass().getName() 
                        + ". There can only be one SearchIndexProcessLauncher registered for a given FieldEntity.");
            } else {
                FIELD_ENTITY_REGISTRY.put(determineFieldEntity().getType(), this);
            }
        }
        
        //Delegate to subclasses to allow them to initialize, if needed.
        initialize();
    }
    
    /**
     * This is a static convenience (factory) method for finding a SearchIndexProcessLauncher by its associated FieldEntity type.
     * Returns null if nothing is registered for the provided FieldEntity.
     * @param entity
     * @return
     */
    public static SearchIndexProcessLauncher<?> getProcessLauncherForFieldEntity(
            FieldEntity entity) {
        if (entity == null) {
            return null;
        }
        synchronized (AbstractGenericSearchIndexProcessLauncher.class) {
            return FIELD_ENTITY_REGISTRY.get(entity.getType());
        }  
    }
    
    @SuppressWarnings("unchecked")
    private QueueManager<I> getQueueManager(String processId) {
        synchronized (QUEUE_MANAGERS_IN_USE) {
            QueueManager<?> qm = QUEUE_MANAGERS_IN_USE.get(processId);
            if (qm == null) {
                qm = createQueueManager(processId);
                QUEUE_MANAGERS_IN_USE.put(processId, qm);
            }
            
            return (QueueManager<I>)qm;
        }
    }
    
    /**
     * This must return a thread-safe singleton {@link QueueEntryProcessor} that will handle entries from the Queue.
     * @return
     */
    protected abstract QueueEntryProcessor<I> getQueueEntryProcessor();
    
    /**
     * Returns the LockService to be used by this component to lock and unlock the process.  This MUST NOT return null.
     * 
     * @return
     */
    protected abstract LockService getLockService();
    
    /**
     * This should consistently return the FieldEntity value on which this IndexService operates.  This must not return null, 
     * and return the same value for each invocation.
     * 
     * @return
     */
    protected abstract FieldEntity determineFieldEntity();
    
    /**
     * This should return a new instance of the QueueManager that owns the relationship between the QueueLoader 
     * and QueueReader. 
     * 
     * @param processId
     * @return
     */
    protected abstract QueueManager<I> createQueueManager(String processId);
    
    /**
     * This is a lifecycle method that allows for arbitrary pre-processing.  This method is invoked after a lock is obtained, 
     * and after a the process state is initialized (so implementors can access SearchIndexProcessStateHolder), but before 
     * any other processing.  Another alternative to using this is to implement a Spring event listener that listens for 
     * SearchIndexProcessStartedEvent types. This method is called before the SearchIndexProcessStartedEvent is raised.
     * @param processId
     * @throws ServiceException
     */
    protected abstract void preProcess(String processId) throws ServiceException;
    
    /**
     * This is a lifecycle method that allows for arbitrary pre-processing of a success flow.  This is invoked when no errors 
     * have occured, and after all other processing normal takes place.  However, this is invoked before the QueueLoader's 
     * cleanup method is called, and before the process state is destroyed (so implementors can access 
     * SearchIndexProcessStateHolder).  Another alternative to using this is to implement a Spring event listener that listens for 
     * SearchIndexProcessCompletedEvent types. This method is called before the SearchIndexProcessCompletedEvent is raised.
     * 
     * @param processId
     * @throws ServiceException
     */
    protected abstract void postProcessSuccess(String processId) throws ServiceException;
    
    /**
     * This is a lifecycle method that allows for arbitrary pre-processing of a failure flow.  This is invoked if/when an 
     * error occurs, but before the QueueLoader's 
     * cleanup method is called, and before the process state is destroyed (so implementors can access 
     * SearchIndexProcessStateHolder). Another alternative to using this is to implement a Spring event listener that listens for 
     * SearchIndexProcessFailedEvent types. This method is called before the SearchIndexProcessFailedEvent is raised.
     * @param processId
     * @throws ServiceException
     */
    protected abstract void postProcessFailure(String processId) throws ServiceException;
}
