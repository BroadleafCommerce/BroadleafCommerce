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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This component is a container for allowing thread-safe static access to state across a global, in JVM process (e.g. 
 * a process that is multi-threaded). This should typically be accessed by control or flow components.  
 * You must start and end the process to prevent memory leaks and other side effects:
 * 
 * ReindexStateHolder.startProcessState(FieldEntity.PRODUCT);
 * try {
 *   ... //Do a full re-index, likely across multiple threads.
 * } finally {
 *   ReindexStateHolder.endProcessState(FieldEntity.PRODUCT);
 * }
 * 
 * Alternatively, you could use a unique ID for a local process (e.g. a process is reindexing specific documents in a single thread):
 * 
 * String processId = UUID.randomUUID().toString();
 * ReindexStateHolder.startProcessState(processId);
 * try {
 *   ... //Re-index a few documents within a single thread.
 * } finally {
 *   ReindexStateHolder.endProcessState(processId);
 * }
 * 
 * This is useful for determining if a process has failed, or for causing a process to fail fast across multiple threads.
 * 
 * @author Kelly Tisdell
 *
 */
public class ReindexProcessStateHolder {
    
    private static final Log LOG = LogFactory.getLog(ReindexProcessStateHolder.class);
    
    public static final String PRIMARY_INDEX_NAME = "PRIMARY_INDEX_NAME"; //Convenience name of the current primary index that will be swapped to the background
    public static final String PRIMARY_ALIAS_NAME = "PRIMARY_ALIAS_NAME"; //Convenience name of the current primary alias 
    public static final String REINDEX_INDEX_NAME = "REINDEX_INDEX_NAME"; //Convenience name of the index or collection that is being reindexed.
    public static final String REINDEX_ALIAS_NAME = "REINDEX_ALIAS_NAME"; //Convenience name of the alias that is being reindexed.
    
    private static final Map<String,ReindexProcessStateHolder> STATE_HOLDER_MAP = new HashMap<>();
    private final Map<String, Object> additionalPropeties = new HashMap<>();
    private final ArrayList<Throwable> failures = new ArrayList<>();
    private boolean failed = false;
    private long expectedIndexableItemsToProcess = 0L;
    private long indexableItemsProcessed = 0L;
    private long documentsProcessed = 0L;
    
    public ReindexProcessStateHolder() {
        //This should never be directly instantiated.  Use the static methods to access it.  Use additional properties 
        //to store additional state.
    }
    
    /**
     * Initiates an instance of ReindexStateHolder that will be used across multiple threads, typically for full 
     * reindexing.  The processID allows for multiple reindexing to happen at the same time (e.g. catalog and customer).
     * 
     * One approach is to use an enumeration value to start the process state for the reindexing of a particular entity.  For 
     * example, you might use <code>org.broadleafcommerce.core.search.domain.FieldEntity.PRODUCT</code> to create a global ReindexStateHolder 
     * for reindexing a particular type of entity.  For individual processes that may be reindexing individual documents, you 
     * might choose to use a unique ID, such as a UUID.
     * 
     * You must use the same process ID to call endProcessState.
     * 
     * @param processId
     */
    public static void startProcessState(String processId) {
        if (processId == null) {
            throw new IllegalArgumentException("Process ID must not be null in order to start a process.");
        }
        if (processId != null) {
            synchronized (ReindexProcessStateHolder.class) {
                if (STATE_HOLDER_MAP.containsKey(processId)) {
                    throw new IllegalStateException("Can't start process state for this process ID. It is already started.");
                }
                STATE_HOLDER_MAP.put(processId, new ReindexProcessStateHolder());
            }
        }
    }
    
    /**
     * End's a process for a given process Id.  This MUST be called, typically in a finally block of a control thread after a full re-index has been completed.
     */
    public static void endProcessState(String processId) {
        if (processId != null) {
            synchronized (ReindexProcessStateHolder.class) {
                if (STATE_HOLDER_MAP.containsKey(processId)) {
                    STATE_HOLDER_MAP.remove(processId);
                } else {
                    LOG.warn("Process state was not started for processId " + processId);
                }
            }
        }
    }
    
    /**
     * Indicates if any thread has started a process state associated with this process ID, and not stopped it.
     * @return
     */
    public static boolean isProcessStateEnabled(String processId) {
        if (processId != null) {
            synchronized (ReindexProcessStateHolder.class) {
                return (STATE_HOLDER_MAP.containsKey(processId));
            }
        }
        
        return false;
    }
    
    /**
     * Notifies that the process has failed and keeps track of the Throwables passed in.  This allows other threads to monitor 
     * this state and fail the entire process fast if other thread reported a failure.
     * @param processId
     * @param th
     */
    public static void failFast(String processId, Throwable th) {
        ReindexProcessStateHolder instance = getInstance(processId);
        synchronized(instance) {
            instance.failed = true;
            if (th != null) {
                instance.failures.add(th);
            }
        }   
    }
    
    /**
     * Indicates if the process has been notified as failing by one or more threads.
     * 
     * @param processId
     * @return
     */
    public static boolean isFailed(String processId) {
        ReindexProcessStateHolder instance = getInstance(processId);
        synchronized(instance) {
            return instance.failed;
        }
    }
    
    /**
     * More than one thread can potentially report an error.  This returns the first Throwable that was reported, which 
     * caused the process to fail.
     * @param processId
     * @return
     */
    public static Throwable getFirstFailure(String processId) {
        ReindexProcessStateHolder instance = getInstance(processId);
        synchronized(instance) {
            if (!instance.failures.isEmpty()){
                return instance.failures.get(0);
            } else {
                return null;
            }
        }
    }
    
    /**
     * More than one thread may have reported an error before realizing that the process failed.  
     * This keeps track of all exceptions in order, and returns the list.  It is possible that the list is 
     * empty, if someone passed in a null Throwable when failing the process.
     * @param processId
     * @return
     */
    public static List<Throwable> getAllFailures(String processId) {
        ReindexProcessStateHolder instance = getInstance(processId);
        synchronized(instance) {
            return Collections.unmodifiableList(instance.failures);
        }
    }
    
    /**
     * Returns an arbitrary Object in a map, based on the provided key.
     * @param processId
     * @param key
     * @return
     */
    public static Object getAdditionalProperty(String processId, String key) {
        ReindexProcessStateHolder instance = getInstance(processId);
        synchronized (instance.additionalPropeties) {
            return instance.additionalPropeties.get(key);
        }
    }
    
    /**
     * Returns an unmodifiable map of all additional properties.  This is an arbitrary map of 
     * key/value pairs, where the key is a String and value is an Object.
     * @param processId
     * @return
     */
    public static Map<String, Object> getAdditionalProperties(String processId) {
        ReindexProcessStateHolder instance = getInstance(processId);
        synchronized (instance.additionalPropeties) {
            return Collections.unmodifiableMap(instance.additionalPropeties);
        }
    }
    
    /**
     * Allows callers to set arbitrary state with key/value pairs for a particular process.  
     * This is thread safe and is managed via a map.
     * @param processId
     * @param key
     * @param value
     */
    public static void setAdditionalProperty(String processId, String key, Object value) {
        ReindexProcessStateHolder instance = getInstance(processId);
        synchronized (instance.additionalPropeties) {
            instance.additionalPropeties.put(key, value);
        }
    }
    
    /**
     * Provides a thread-safe, atomic way to increment an arbitrary Long value in the additional properties map. 
     * If the property does not exist, it will be incremented (or decremented) from zero.  If the property already exists and is 
     * not a long value, a ClassCastException will be thrown.  A negative number can be used to decrement.
     * 
     * @param processId
     * @param key
     * @param incrementBy
     */
    public static void incrementOrDecrementLongPropertyVal(String processId, String key, long incrementBy) {
        ReindexProcessStateHolder instance = getInstance(processId);
        synchronized (instance.additionalPropeties) {
            Long val = (Long)instance.additionalPropeties.get(key);
            if (val == null) {
                val = incrementBy;
            } else {
                val += incrementBy;
            }
            instance.additionalPropeties.put(key, val);
        }
    }
    
    /**
     * Provides a thread-safe, atomic way to increment an arbitrary Integer value in the additional properties map. 
     * If the property does not exist, it will be incremented (or decremented) from zero.  If the property already exists and is 
     * not a long value, a ClassCastException will be thrown.  A negative number can be used to decrement.
     * 
     * @param processId
     * @param key
     * @param incrementBy
     */
    public static void incrementOrDecrementIntPropertyVal(String processId, String key, int incrementBy) {
        ReindexProcessStateHolder instance = getInstance(processId);
        synchronized (instance.additionalPropeties) {
            Integer val = (Integer)instance.additionalPropeties.get(key);
            if (val == null) {
                val = incrementBy;
            } else {
                val += incrementBy;
            }
            instance.additionalPropeties.put(key, val);
        }
    }
    
    /**
     * Allows callers to remove an arbirary entry from a map.
     * @param processId
     * @param key
     * @return
     */
    public static Object removeAdditionalProperty(String processId, String key) {
        ReindexProcessStateHolder instance = getInstance(processId);
        synchronized (instance.additionalPropeties) {
            return instance.additionalPropeties.remove(key);
        }
    }
    
    public static void incrementIndexableItemsProcessed(String processId) {
        ReindexProcessStateHolder instance = getInstance(processId);
        synchronized (instance) {
            instance.indexableItemsProcessed++;
        }
    }
    
    public static void incrementIndexableItemsProcessed(String processId, long itemsProcessed) {
        ReindexProcessStateHolder instance = getInstance(processId);
        synchronized (instance) {
            instance.indexableItemsProcessed += itemsProcessed;
        }
    }
    
    public static long getIndexableItemsProcessed(String processId) {
        ReindexProcessStateHolder instance = getInstance(processId);
        synchronized (instance) {
            return instance.indexableItemsProcessed;
        }
    }
    
    public static void incrementDocumentsProcessed(String processId) {
        ReindexProcessStateHolder instance = getInstance(processId);
        synchronized (instance) {
            instance.documentsProcessed++;
        }
    }
    
    public static void incrementDocumentsProcessed(String processId, long itemsProcessed) {
        ReindexProcessStateHolder instance = getInstance(processId);
        synchronized (instance) {
            instance.documentsProcessed += itemsProcessed;
        }
    }
    
    public static long getDocumentsProcessed(String processId) {
        ReindexProcessStateHolder instance = getInstance(processId);
        synchronized (instance) {
            return instance.documentsProcessed;
        }
    }
    
    public static void setExepectedIndexableItemsToProcess(String processId, long expectedIndexableItemsToProcess) {
        ReindexProcessStateHolder instance = getInstance(processId);
        synchronized (instance) {
            instance.expectedIndexableItemsToProcess += expectedIndexableItemsToProcess;
        }
    }
    
    public static long getExepectedIndexableItemsToProcess(String processId) {
        ReindexProcessStateHolder instance = getInstance(processId);
        synchronized (instance) {
            return instance.expectedIndexableItemsToProcess;
        }
    }
    
    private static ReindexProcessStateHolder getInstance(String processId) {
        if (processId != null) {
            synchronized(ReindexProcessStateHolder.class) {
                if (STATE_HOLDER_MAP.containsKey(processId)) {
                    return STATE_HOLDER_MAP.get(processId);
                }
            }
        }
        
        throw new IllegalStateException("Thread attemtped access to ReindexStateHolder with process ID " 
                + processId + " but it was not initialized. Be sure to call startProcessState() to intialize the process state.");
    }
}
