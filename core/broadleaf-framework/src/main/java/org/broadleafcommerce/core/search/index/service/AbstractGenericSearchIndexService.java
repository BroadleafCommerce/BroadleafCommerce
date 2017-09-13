package org.broadleafcommerce.core.search.index.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuActiveDatesService;
import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuPricingService;
import org.broadleafcommerce.core.catalog.service.dynamic.SkuActiveDateConsiderationContext;
import org.broadleafcommerce.core.catalog.service.dynamic.SkuPricingConsiderationContext;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;

import java.util.HashMap;

/**
 * This is an abstract component to allow for the full reindexing of a specific index.
 * @author Kelly Tisdell
 *
 * @param <I>
 */
public abstract class AbstractGenericSearchIndexService<I extends Indexable> implements SearchIndexService<I>, Runnable {
    
    private static final Log LOG = LogFactory.getLog(AbstractGenericSearchIndexService.class);
    
    @Autowired
    @Qualifier("blSearchReindexLockService")
    protected LockService lockService;
    
    @Autowired(required=false)
    @Qualifier("blSearchReindexTaskExecutor")
    protected TaskExecutor taskExecutor;
    
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
        final String processId = determineFieldEntity().getType();
        synchronized(this) {
            try {
                //The whole point of a lock is to avoid having 2 threads or even 2 different cluster members 
                //trying to do this at the same time.  Incremental updates are fine without a lock, but we don't want 
                //2 different threads simultaneously kicking off the same process at the same time, which could cause 
                //major state issues in addition to increased resource use and reduced performance.
                lockService.lock(processId);
            } catch (LockException e) {
                LOG.error("There was an error obtaining the lock for processId " + processId, e);
                return;
            }
        }
        
        Object[] pack = saveState();
        try {
            ReindexProcessStateHolder.startProcessState(processId);
            try {
                preProcess(processId);
                
                final Long numItemsToIndex;
                try {
                    beforeCountIndexables();
                    numItemsToIndex = countIndexables();
                    ReindexProcessStateHolder.setExepectedIndexableItemsToProcess(processId, numItemsToIndex);
                } finally {
                    afterCountIndexables();
                }
                
                process(processId);
                postProcess(processId);
                
            } catch (Exception e) {
                LOG.error("An error occured reindexing with processId " + processId, e);
                ReindexProcessStateHolder.failFast(processId, e);
                
                try {
                    //Allow any background threads to realize that we've failed before we go into the 
                    //finally block and kill the process.
                    Thread.sleep(5000L);
                } catch (InterruptedException ie) {
                    //Ignore
                }
                
            } finally {
                ReindexProcessStateHolder.endProcessState(processId);
                synchronized (this) {
                    try {
                        lockService.unlock(processId);
                    } catch (LockException e) {
                        LOG.error("There was an error trying to remove the lock for processId " + processId, e);
                    }
                }
            }
        } finally {
            restoreState(pack);
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
            return lockService.isLocked(determineFieldEntity().getType());
        }
    }
    
    protected Object[] saveState() {
        return new Object[] {
            BroadleafRequestContext.getBroadleafRequestContext(),
            SkuPricingConsiderationContext.getSkuPricingConsiderationContext(),
            SkuPricingConsiderationContext.getSkuPricingService(),
            SkuActiveDateConsiderationContext.getSkuActiveDatesService()
        };
    }
        
    protected void restoreState(Object[] pack) {
        BroadleafRequestContext.setBroadleafRequestContext((BroadleafRequestContext) pack[0]);
        SkuPricingConsiderationContext.setSkuPricingConsiderationContext((HashMap<?,?>) pack[1]);
        SkuPricingConsiderationContext.setSkuPricingService((DynamicSkuPricingService) pack[2]);
        SkuActiveDateConsiderationContext.setSkuActiveDatesService((DynamicSkuActiveDatesService) pack[3]);
    }
    
    protected void beforeCountIndexables() {
        //Pass through to allow overriding.
    }
    
    protected void afterCountIndexables() {
        //Pass through to allow overriding.
    }
    
    /**
     * This should consistently return the FieldEntity value on which this IndexService operates.  This must not return null.
     * 
     * @return
     */
    protected abstract FieldEntity determineFieldEntity();
    
    /**
     * Method to handle setup of execution.  For example, if this is a Solr reindex job, it may be to set up or empty the appropriate collection. 
     * If there is nothing to do, then this can be implemented as a pass through.
     * 
     * @param processId
     * @throws ServiceException
     */
    protected abstract void preProcess(String processId) throws ServiceException;
    
    /**
     * This method is to handle post processing.  For example, if this is a Solr reindex job, it may be to commit and swap/re-alias the 
     * Solr collections.  If there is nothing to do, then this can be implemented as a pass through.
     * 
     * @param processId
     * @throws ServiceException
     */
    protected abstract void postProcess(String processId) throws ServiceException;
    
    /**
     * This is where the majority of processing will occur.
     * @param processId
     * @throws ServiceException
     */
    protected abstract void process(String processId) throws ServiceException;
    
    /**
     * Counts the number of indexable items that are expected to be indexed
     * @return
     */
    protected abstract Long countIndexables();
}
