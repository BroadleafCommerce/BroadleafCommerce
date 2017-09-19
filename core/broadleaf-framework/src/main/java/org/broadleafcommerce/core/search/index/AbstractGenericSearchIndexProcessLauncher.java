package org.broadleafcommerce.core.search.index;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.task.TaskExecutor;

import java.io.Serializable;
import java.util.List;

/**
 * This is an abstract component to allow for the full reindexing of a specific index.  This component only operates as a 
 * controller for a multi-threaded process.  The purpose of this is to obtain a process lock (which may be in memory or 
 * distributed).  It then runs a QueueLoader in another thread, which begins to populate a Queue (which could be in memory or 
 * distributed).  It creates a Semaphore to block until all background threads are complete.  It then raises a process started 
 * event (which is a Spring event).  This may be propagated to a distributed event so that other QueueConsumers can be started.
 * Finally, it waits until the semaphore releases and completes the process.
 * 
 * @author Kelly Tisdell
 *
 * @param <I>
 */
public abstract class AbstractGenericSearchIndexProcessLauncher<I extends Indexable> 
implements SearchIndexProcessLauncher<I>, Runnable, ApplicationContextAware, InitializingBean {
    
    private static final Log LOG = LogFactory.getLog(AbstractGenericSearchIndexProcessLauncher.class);
    protected static final long DEFAULT_CLEANUP_WAIT_TIME = 5000L;
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
                
                //The owner of the lock is assumed to be the thread that controls the flow.  If it is a distributed lock, 
                //it is assumed that a particular thread on a particular node controls the flow.
                key = getLockService().lock(processId);
                
                startTime = System.currentTimeMillis();
            } catch (LockException e) {
                LOG.error("There was an error obtaining the lock for processId " + processId, e);
                return;
            }
        }
        
        //TODO: Create Barrier.
        QueueManager<?> queueManager = null;
        try {
            SearchIndexProcessStateHolder.startProcessState(processId);
            try {
                //Allow for any arbitrary pre-processing.
                preProcess(processId);
                
                try {
                    //This will start a QueueLoader in a background thread.
                    //The purpose is to begin independently populating a Queue so that consumer threads can independently 
                    //and asynchronously consume the messages.
                    queueManager = createQueueManager(processId);
                    queueManager.initialize(processId);
                    if (getTaskExecutor() == null) {
                        queueManager.startQueueProducer();
                    } else {
                        queueManager.startQueueProducer(getTaskExecutor());
                    }
                    
                    String queueName = queueManager.getQueueName();
                    
                    //Now, we need to raise an event so that consumers can begin consuming messages.
                    ctx.publishEvent(new SearchIndexProcessStartedEvent(processId, determineFieldEntity(), queueName));
                    
                } finally {
                    //TODO: Wait for Barrier...
                    
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
                    
                    //Now, raise an event that the process failed.
                    ctx.publishEvent(new SearchIndexProcessFailedEvent(processId, determineFieldEntity(), e));
                } catch (Exception ie) {
                    LOG.error("An error occured in the catch block.  Trapping and logging here for investigation.", ie);
                }
                
            } finally {
                try {
                    //Allow any background threads to finish what they are doing, realize there was an error, etc.
                    Thread.sleep(determineCleanupWaitTime());
                } catch (InterruptedException ie) {
                    //Ignore
                }
                
                try {
                    if (queueManager != null) {
                        queueManager.close(processId);
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
                    }
                }
            }
        } finally {
            synchronized(this) {
                startTime = -1;
            }
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
        if (!isExecuting()) {
            if (getTaskExecutor() != null) {
                getTaskExecutor().execute(this);
            } else {
                Thread t = new Thread(this, getClass().getName());
                t.start();
            }
        } else {
            throw new ServiceException("The index process is already running for processId " + determineProcessId());
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
     * This is the time that the main control thread will wait in the finally block before stopping the process and 
     * cleaning up.  This allows any other threads to get caught up, recognize that an error has occured, etc.
     * 
     * The default value is 5000 ms (5 seconds).
     * 
     * @return
     */
    protected long determineCleanupWaitTime() {
        return DEFAULT_CLEANUP_WAIT_TIME;
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
     * Method to allow implementors to return a specific TaskExecutor.  This method MAY return null.  If this method returns 
     * null, then Threads will be directly created and used for execution of this job, which is the default. The reason is 
     * that this process is careful to prevent multiple jobs running at the same time, and we don't want to consume 
     * worker threads from a thread pool to run a single control thread.
     * 
     * This component runs itself in a background thread when the rebuildIndex() method is called.  This component also 
     * delegates to a background thread to run the QueueLoader.  If you do return a task executor, make sure it has 
     * enough threads to handle the control thread, the QueueLoader, and the worker threads for the QueueConsumer.
     * 
     * @return
     */
    protected TaskExecutor getTaskExecutor() {
        //Default is to return null
        return null;
    }
    
    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //TODO: Make sure that the QueueManager, LockService, and this all have the proper distributed properties.
    }
    
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
     * This should return the QueueManager that owns the relationship between the QueueLoader and QueueConsumer.
     * 
     * @param processId
     * @return
     */
    protected abstract QueueManager<?> createQueueManager(String processId);
    
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
