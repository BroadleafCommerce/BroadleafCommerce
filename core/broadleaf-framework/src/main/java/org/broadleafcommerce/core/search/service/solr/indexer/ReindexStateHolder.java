package org.broadleafcommerce.core.search.service.solr.indexer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Holder to contain cross-thread state during Solr reindexing.
 * 
 * @author Kelly Tisdell
 *
 */
public class ReindexStateHolder {

    private final Map<String, Object> additionalState = Collections.synchronizedMap(new HashMap<String, Object>());
    private final AtomicLong indexableCount = new AtomicLong();
    private final AtomicLong documentErrorCount = new AtomicLong();
    private final AtomicBoolean failed = new AtomicBoolean(false);
    private final AtomicBoolean queueLoadCompleted = new AtomicBoolean(false);
    private final AtomicReference<Exception> throwable = new AtomicReference<>();
    private final BlockingQueue<Long[]> idQueue = new ArrayBlockingQueue<>(1000);
    
    public ReindexStateHolder() {}
    
    public synchronized boolean isFailed() {
        return failed.get();
    }
    
    public synchronized void failFast(Exception t) {
        failed.set(true);
        if (t != null && throwable.get() == null) {
            throwable.set(t);
        }
    }
    
    public synchronized Exception getFailure() {
        if (isFailed()) {
            return throwable.get();
        }
        return null;
    }
    
    public synchronized long incrementIndexableCount(long delta) {
        return indexableCount.addAndGet(delta);
    }
    
    public synchronized long incrementDocumentErrorCount(long delta) {
        return documentErrorCount.addAndGet(delta);
    }
    
    public Map<String, Object> getAdditionalState() {
        return additionalState;
    }
    
    public BlockingQueue<Long[]> getIdQueue() {
        return idQueue;
    }
    
    public synchronized void markQueueLoadCompleted() {
        queueLoadCompleted.set(true);
    }
    
    public synchronized boolean isQueueLoadCompleted() {
        return queueLoadCompleted.get();
    }
}
