/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2019 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.service.solr.indexer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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

    private static final Map<String, ReindexStateHolder> STATE_HOLDER_REGISTRY = Collections.synchronizedMap(new HashMap<String, ReindexStateHolder>());
    private final String collectionName;
    private final boolean incrementalCommits;
    private final Map<String, Object> additionalState = Collections.synchronizedMap(new HashMap<String, Object>());
    private final AtomicLong indexableCount = new AtomicLong();
    private final AtomicLong unindexedItemCount = new AtomicLong();
    private final AtomicLong lastComitted = new AtomicLong(-1L);
    private final AtomicBoolean failed = new AtomicBoolean(false);
    private final AtomicReference<Exception> throwable = new AtomicReference<>();
    
    private ReindexStateHolder(String collectionName, boolean incrementalCommits) {
        this.collectionName = collectionName;
        this.incrementalCommits = incrementalCommits;
        synchronized (STATE_HOLDER_REGISTRY) {
            if (STATE_HOLDER_REGISTRY.containsKey(collectionName)) {
                throw new IllegalStateException("There was already a ReindexStateHolder registered for collection name: " 
                        + collectionName + ". Ensure that you call the destroy method after you are done using the object.");
            }
            STATE_HOLDER_REGISTRY.put(collectionName, this);
        }
    }
    
    /**
     * Returns a shared instance of this class or null.
     * 
     * This method will typically be used by background worker threads that will use an instance that has already been created.  This may return null if 
     * an instance has not yet been created by the control thread or the control thread has already deregistered the instance by calling deregister.
     * 
     * @param collectionName
     * @return
     */
    public static ReindexStateHolder getInstance(String collectionName) {
        return getInstance(collectionName, false, false);
    }
    
    /**
     * Creates or returns a shared instance of this class associated with the collectionName.  If createIfAbsent is set to false, this will return null if an 
     * instance has not been created.  The argument for incrementalCommits is a hint indicating that commits may be issued during a reindex process. This 
     * argument is ignored if an instance has already been created.
     * 
     * This method will typically be used by a control thread that will create a new instance for a process that may be multi-threaded.
     * 
     * @param collectionName
     * @param incrementalCommits
     * @param createIfAbsent
     * @return
     */
    public static ReindexStateHolder getInstance(String collectionName, boolean incrementalCommits, boolean createIfAbsent) {
        synchronized (STATE_HOLDER_REGISTRY) {
            ReindexStateHolder holder = STATE_HOLDER_REGISTRY.get(collectionName);
            if (holder == null) {
                if (createIfAbsent) {
                    //This automatically registers this holder.
                    holder = new ReindexStateHolder(collectionName, incrementalCommits);
                }
            }
            return holder;
        }
    }
    
    public static void unregister(String collectionName) {
        synchronized (STATE_HOLDER_REGISTRY) {
            STATE_HOLDER_REGISTRY.remove(collectionName);
        }
    }
    
    public String getCollectionName() {
        return collectionName;
    }
    
    public boolean isIncrementalCommits() {
        return incrementalCommits;
    }
    
    public synchronized boolean isFailed() {
        return failed.get();
    }
    
    public synchronized void failFast(Exception t) {
        if (!failed.get()) {
            failed.set(true);
            if (t != null && throwable.get() == null) {
                throwable.set(t);
            }
        }
    }
    
    public synchronized Exception getFailure() {
        if (isFailed()) {
            return throwable.get();
        }
        return null;
    }
    
    public synchronized long getIndexableCount() {
        return indexableCount.get();
    }
    
    public synchronized long incrementIndexableCount(long delta) {
        return indexableCount.addAndGet(delta);
    }
    
    public synchronized long getUnindexedItemCount() {
        return unindexedItemCount.get();
    }
    
    public synchronized long incrementUnindexedItemCount(long delta) {
        return unindexedItemCount.addAndGet(delta);
    }
    
    public Map<String, Object> getAdditionalState() {
        return additionalState;
    }
    
    public long getLastCommitted() {
        return lastComitted.get();
    }
    
    public void setLastCommitted(long lastCommitted) {
        this.lastComitted.set(lastCommitted);
    }
}
