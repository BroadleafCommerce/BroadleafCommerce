package org.broadleafcommerce.core.search.index.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.Assert;

/**
 * This is an abstract component to allow for the full reindexing of a specific index.
 * @author Kelly Tisdell
 *
 * @param <I>
 */
public abstract class AbstractGenericIndexService<I extends Indexable> implements IndexService<I>, Runnable {
    
    private static final Log LOG = LogFactory.getLog(AbstractGenericIndexService.class);
    
    protected final LockService lockService;
    
    protected final TaskExecutor taskExecutor;
    
    public AbstractGenericIndexService(LockService lockService) {
        this(lockService, null);
    }
    
    public AbstractGenericIndexService(LockService lockService, TaskExecutor taskExecutor) {
        Assert.notNull(lockService, "The LockService must not be null.");
        this.lockService = lockService;
        this.taskExecutor = taskExecutor;
    }
    
    /**
     * Method in this class that does the heavy lifting.  However, for the most part, this delegates to the preProcess, process, 
     * and postProcess methods.
     * 
     * This method is responsible for obtaining (and removing) necessary locks and for starting/ending the process state.
     * 
     * It is not recommended that you override this method.  Instead, implement or override the preProcess, process, and postProcess 
     * methods.
     */
    protected void executeInternally() {
        final String processId = determineProcessId();
        if (processId == null) {
            throw new IllegalStateException("processId cannot be null.  Check implementation of method determineProcessId()");
        }
        
        boolean lockObtained = false;
        synchronized(this) {
            if (!lockService.isLocked(processId)) {
                try {
                    lockService.lock(processId);
                    lockObtained = true;
                } catch (LockException e) {
                    LOG.error("There was an error obtaining the lock for processId " + processId, e);
                    return;
                }
            } else {
                LOG.warn("This service was already locked for processId " + processId);
                return;
            }
        }
        
        boolean processStateStarted = false;
        try {
            ReindexProcessStateHolder.startProcessState(processId);
            processStateStarted = true;
            
            preProcess(processId);
            process(processId);
            postProcess(processId);
            
        } catch (Exception e) {
            LOG.error("An error occured reindexing with processId " + processId, e);
            ReindexProcessStateHolder.failFast(processId, e);
        } finally {
            if (processStateStarted) {
                ReindexProcessStateHolder.endProcessState(processId);
            }
            
            synchronized (this) {
                if (lockObtained) {
                    try {
                        lockService.unlock(processId);
                    } catch (LockException e) {
                        LOG.error("There was an error trying to remove the lock for processId " + processId, e);
                    }
                }
            }
        }
    }
    
    public void rebuildIndex() throws ServiceException {
        if (!isExecutingReindex()) {
            if (taskExecutor != null) {
                taskExecutor.execute(this);
            } else {
                Thread t = new Thread(this, getClass().getName());
                t.start();
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
    
    @Override
    public boolean isExecutingReindex() {
        synchronized (this) {
            return lockService.isLocked(determineProcessId());
        }
    }
    
    /**
     * This should return a common and predictable Object for a given job.  
     * For example, <code>org.broadleafcommerce.core.search.domain.FieldEntity</code> is a good object to use since it 
     * is an enumerated value that identifies the type of entities being reindexed.  So, if reindexing products, it is 
     * recommended that this method return FieldEntity.PRODUCT.getType().
     * 
     * @return
     */
    protected abstract String determineProcessId();
    
    /**
     * Method to handle setup of execution.  For example, if this is a Solr reindex job, it may be to set up or empty the appropriate collection. 
     * If there is nothing to do, then this can be implemented as a pass through.
     * 
     * @param processId
     * @throws ServiceException
     */
    protected abstract void preProcess(Object processId) throws ServiceException;
    
    /**
     * This method is to handle post processing.  For example, if this is a Solr reindex job, it may be to commit and swap/re-alias the 
     * Solr collections.  If there is nothing to do, then this can be implemented as a pass through.
     * 
     * @param processId
     * @throws ServiceException
     */
    protected abstract void postProcess(Object processId) throws ServiceException;
    
    /**
     * This is where the majority of processing will occur.
     * @param processId
     * @throws ServiceException
     */
    protected abstract void process(Object processId) throws ServiceException;
}
