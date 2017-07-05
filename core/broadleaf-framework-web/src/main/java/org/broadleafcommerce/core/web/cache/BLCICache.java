/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.web.cache;

import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.slf4j.Logger;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheEntryValidityChecker;
import org.thymeleaf.util.Validate;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Modification of {@link org.thymeleaf.cache.StandardCache} in order for BLC to be able to
 * handle caching based on site and profile level templates.  As StandardCache is declared final,
 * this is a wholesale copy with a few changes to get(), put() and getValueIfStillValid().
 *
 * @author Chad Harchar (charchar)
 */
public class BLCICache<K, V> implements ICache<K, V> {

    //START BLC MODIFICATION

    public static final String NOT_FOUND = "NOT_FOUND";

    protected BLCICacheExtensionManager extensionManager;

    /**
     * This method differs from StandardCache.put() by not caching if we're in a sandbox, and adding a hook for
     * Site and Profile interactions
     *
     * @param key
     * @param value
     */
    @Override
    public void put(K key, V value) {

        if (BroadleafRequestContext.getBroadleafRequestContext().getSandBox() != null) {
            return;
        }

        ExtensionResultStatusType erst = extensionManager.getProxy().putCache(key, value, this);

        if (erst.equals(ExtensionResultStatusType.NOT_HANDLED)) {
            defaultPut(key, value);
        }

    }

    /**
     * This method differs from StandardCache.get() by not caching if we're in a sandbox, and adding a hook for
     * Site and Profile interactions
     *
     * @param key
     * @return
     */
    @Override
    public V get(K key) {
        V value = null;

        if (BroadleafRequestContext.getBroadleafRequestContext().getSandBox() != null) {
            return value;
        }

        ExtensionResultHolder<V> result = new ExtensionResultHolder<>();
        ExtensionResultStatusType erst = extensionManager.getProxy().getCache(key, (ExtensionResultHolder<Object>) result, this);

        if (erst.equals(ExtensionResultStatusType.HANDLED)) {
            value = result.getResult();
        } else if (erst.equals(ExtensionResultStatusType.NOT_HANDLED)) {
            value = defaultGet(key);
        }
        return value;
    }

    /**
     * The only difference from StandardCache() is the constructor name
     *
     * @param name
     * @param useSoftReferences
     * @param initialCapacity
     * @param maxSize
     * @param entryValidityChecker
     * @param logger
     * @param extensionManager
     */
    public BLCICache(final String name, final boolean useSoftReferences,
                         final int initialCapacity, final int maxSize, final ICacheEntryValidityChecker<? super K, ? super V> entryValidityChecker,
                         final Logger logger, BLCICacheExtensionManager extensionManager) {

        super();

        Validate.notEmpty(name, "Name cannot be null or empty");
        Validate.isTrue(initialCapacity > 0, "Initial capacity must be > 0");
        Validate.isTrue(maxSize != 0, "Cache max size must be either -1 (no limit) or > 0");

        this.name = name;
        this.useSoftReferences = useSoftReferences;
        this.maxSize = maxSize;
        this.entryValidityChecker = entryValidityChecker;

        this.logger = logger;
        this.traceExecution = (logger != null && logger.isTraceEnabled());

        this.dataContainer =
                new CacheDataContainer<>(this.name, initialCapacity, maxSize, this.traceExecution, this.logger);

        this.getCount = new AtomicLong(0);
        this.putCount = new AtomicLong(0);
        this.hitCount = new AtomicLong(0);
        this.missCount = new AtomicLong(0);

        this.extensionManager = extensionManager;

        if (this.logger != null) {
            if (this.maxSize < 0) {
                this.logger.debug("[THYMELEAF][CACHE_INITIALIZE] Initializing cache {}. Soft references {}.",
                        this.name, (this.useSoftReferences? "are used" : "not used"));
            } else {
                this.logger.debug("[THYMELEAF][CACHE_INITIALIZE] Initializing cache {}. Max size: {}. Soft references {}.",
                        new Object[] {this.name, Integer.valueOf(this.maxSize), (this.useSoftReferences? "are used" : "not used")});
            }
        }

    }

    /**
     * This method behaves the same as StandardCache.put()
     *
     * @param key
     * @param value
     */
    public void defaultPut(final K key, final V value) {

        incrementReportEntity(this.putCount);

        final CacheEntry<V> entry = new CacheEntry<>(value, this.useSoftReferences);

        // newSize will be -1 if traceExecution is false
        final int newSize = this.dataContainer.put(key, entry);

        if (this.traceExecution) {
            this.logger.trace(
                    "[THYMELEAF][{}][{}][CACHE_ADD][{}] Adding cache entry in cache \"{}\" for key \"{}\". New size is {}.",
                    new Object[] {TemplateEngine.threadIndex(), this.name, Integer.valueOf(newSize), this.name, key, Integer.valueOf(newSize)});
        }

        outputReportIfNeeded();

    }

    /**
     * This method behaves the same as StandardCache.get()
     *
     * @param key
     * @return
     */
    public V defaultGet(final K key) {
        return get(key, this.entryValidityChecker);
    }

    //END BLC MODIFICATION (Continued below)

    private static final long REPORT_INTERVAL = 300000L; // 5 minutes
    private static final String REPORT_FORMAT =
            "[THYMELEAF][*][*][*][CACHE_REPORT] %8s elements | %12s puts | %12s gets | %12s hits | %12s misses - [%s]";
    private volatile long lastExecution = System.currentTimeMillis();

    private final String name;
    private final boolean useSoftReferences;
    private final int maxSize;
    private final CacheDataContainer<K,V> dataContainer;
    private final ICacheEntryValidityChecker<? super K, ? super V> entryValidityChecker;

    private final boolean traceExecution;
    private final Logger logger;

    private final AtomicLong getCount;
    private final AtomicLong putCount;
    private final AtomicLong hitCount;
    private final AtomicLong missCount;

    @Override
    public V get(final K key, final ICacheEntryValidityChecker<? super K, ? super V> validityChecker) {

        incrementReportEntity(this.getCount);

        final CacheEntry<V> resultEntry = this.dataContainer.get(key);

        if (resultEntry == null) {
            incrementReportEntity(this.missCount);
            if (this.traceExecution) {
                this.logger.trace(
                        "[THYMELEAF][{}][{}][CACHE_MISS] Cache miss in cache \"{}\" for key \"{}\".",
                        new Object[] {TemplateEngine.threadIndex(), this.name, this.name, key});
            }
            outputReportIfNeeded();
            return null;
        }

        final V resultValue =
                resultEntry.getValueIfStillValid(this.name, key, validityChecker, this.traceExecution, this.logger);
        if (resultValue == null) {
            final int newSize = this.dataContainer.remove(key);
            if (this.traceExecution) {
                this.logger.trace(
                        "[THYMELEAF][{}][{}][CACHE_REMOVE][{}] Removing cache entry in cache \"{}\" (Entry \"{}\" is not valid anymore). New size is {}.",
                        new Object[] {TemplateEngine.threadIndex(), this.name, Integer.valueOf(newSize), this.name, key, Integer.valueOf(newSize)});
                this.logger.trace(
                        "[THYMELEAF][{}][{}][CACHE_MISS] Cache miss in cache \"{}\" for key \"{}\".",
                        new Object[] {TemplateEngine.threadIndex(), this.name, this.name, key});
            }
            incrementReportEntity(this.missCount);
            outputReportIfNeeded();
            return null;
        }

        if (this.traceExecution) {
            this.logger.trace(
                    "[THYMELEAF][{}][{}][CACHE_HIT] Cache hit in cache \"{}\" for key \"{}\".",
                    new Object[] {TemplateEngine.threadIndex(), this.name, this.name, key});
        }

        incrementReportEntity(this.hitCount);
        outputReportIfNeeded();
        return resultValue;

    }


    /**
     * <p>
     *   Returns all the keys contained in this cache. Note this method might return keys for entries
     *   that are already invalid, so the result of calling {@link #get(Object)} for these keys might
     *   be <tt>null</tt>.
     * </p>
     *
     * @return the complete set of cache keys. Might include keys for already-invalid (non-cleaned) entries.
     * @since 2.1.4
     */
    public Set<K> keySet() {
        return this.dataContainer.keySet();
    }

    @Override
    public void clear() {

        this.dataContainer.clear();

        if (this.traceExecution) {
            this.logger.trace(
                    "[THYMELEAF][{}][*][{}][CACHE_REMOVE][0] Removing ALL cache entries in cache \"{}\". New size is 0.",
                    new Object[] {TemplateEngine.threadIndex(), this.name, this.name});
        }

    }

    @Override
    public void clearKey(final K key) {

        final int newSize = this.dataContainer.remove(key);

        if (this.traceExecution && newSize != -1) {
            this.logger.trace(
                    "[THYMELEAF][{}][*][{}][CACHE_REMOVE][{}] Removed cache entry in cache \"{}\" for key \"{}\". New size is {}.",
                    new Object[] {TemplateEngine.threadIndex(), this.name, Integer.valueOf(newSize), this.name, key, Integer.valueOf(newSize)});
        }

    }

    public String getName() {
        return this.name;
    }

    public boolean hasMaxSize() {
        return (this.maxSize > 0);
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public boolean getUseSoftReferences() {
        return this.useSoftReferences;
    }

    public int size() {
        return this.dataContainer.size();
    }

    private void incrementReportEntity(final AtomicLong entity) {
        if (this.traceExecution) {
            entity.incrementAndGet();
        }
    }

    private void outputReportIfNeeded() {

        if (this.traceExecution) { // fail fast

            final long currentTime = System.currentTimeMillis();
            if ((currentTime - this.lastExecution) >= REPORT_INTERVAL) { // first check without need to sync
                synchronized (this) {
                    if ((currentTime - this.lastExecution) >= REPORT_INTERVAL) {
                        this.logger.trace(
                                String.format(REPORT_FORMAT,
                                        Integer.valueOf(size()),
                                        Long.valueOf(this.putCount.get()),
                                        Long.valueOf(this.getCount.get()),
                                        Long.valueOf(this.hitCount.get()),
                                        Long.valueOf(this.missCount.get()),
                                        this.name));
                        this.lastExecution = currentTime;
                    }
                }
            }

        }

    }

    static final class CacheDataContainer<K,V> {

        private final String name;
        private final boolean sizeLimit;
        private final int maxSize;
        private final boolean traceExecution;
        private final Logger logger;

        private final ConcurrentHashMap<K,CacheEntry<V>> container;
        private final Object[] fifo;
        private int fifoPointer;


        CacheDataContainer(final String name, final int initialCapacity,
                               final int maxSize, final boolean traceExecution, final Logger logger) {

            super();

            this.name = name;
            this.container = new ConcurrentHashMap<>(initialCapacity);
            this.maxSize = maxSize;
            this.sizeLimit = (maxSize >= 0);
            if (this.sizeLimit) {
                this.fifo = new Object[this.maxSize];
                Arrays.fill(this.fifo, null);
            } else {
                this.fifo = null;
            }
            this.fifoPointer = 0;
            this.traceExecution = traceExecution;
            this.logger = logger;

        }

        public CacheEntry<V> get(final Object key) {
            // FIFO is not used for this --> better performance, but no LRU (only insertion order will apply)
            return this.container.get(key);
        }

        public Set<K> keySet() {
            return new HashSet<>(Collections.list(this.container.keys()));
        }

        public int put(final K key, final CacheEntry<V> value) {
            if (this.traceExecution) {
                return putWithTracing(key, value);
            }
            return putWithoutTracing(key, value);
        }

        private int putWithoutTracing(final K key, final CacheEntry<V> value) {
            // If we are not tracing, it's better to avoid the size() operation which has
            // some performance implications in ConcurrentHashMap (iteration and counting these maps
            // is slow if they are big)

            final CacheEntry<V> existing = this.container.putIfAbsent(key, value);
            if (existing != null) {
                // When not in 'trace' mode, will always return -1
                return -1;
            }

            if (this.sizeLimit) {
                synchronized (this.fifo) {
                    final Object removedKey = this.fifo[this.fifoPointer];
                    if (removedKey != null) {
                        this.container.remove(removedKey);
                    }
                    this.fifo[this.fifoPointer] = key;
                    this.fifoPointer = (this.fifoPointer + 1) % this.maxSize;
                }
            }

            return -1;

        }

        private synchronized int putWithTracing(final K key, final CacheEntry<V> value) {

            final CacheEntry<V> existing = this.container.putIfAbsent(key, value);
            if (existing == null) {
                if (this.sizeLimit) {
                    final Object removedKey = this.fifo[this.fifoPointer];
                    if (removedKey != null) {
                        final CacheEntry<V> removed = this.container.remove(removedKey);
                        if (removed != null) {
                            final Integer newSize = Integer.valueOf(this.container.size());
                            this.logger.trace(
                                    "[THYMELEAF][{}][{}][CACHE_REMOVE][{}] Max size exceeded for cache \"{}\". Removing entry for key \"{}\". New size is {}.",
                                    new Object[] {TemplateEngine.threadIndex(), this.name, newSize, this.name, removedKey, newSize});
                        }
                    }
                    this.fifo[this.fifoPointer] = key;
                    this.fifoPointer = (this.fifoPointer + 1) % this.maxSize;
                }
            }
            return this.container.size();

        }

        public int remove(final K key) {
            if (this.traceExecution) {
                return removeWithTracing(key);
            }
            return removeWithoutTracing(key);
        }

        private int removeWithoutTracing(final K key) {
            // FIFO is also updated to avoid 'removed' keys remaining at FIFO (which could end up reducing cache size to 1)
            final CacheEntry<V> removed = this.container.remove(key);
            if (removed != null) {
                if (this.sizeLimit && key != null) {
                    for (int i = 0; i < this.maxSize; i++) {
                        if (key.equals(this.fifo[i])) {
                            this.fifo[i] = null;
                            break;
                        }
                    }
                }
            }
            return -1;
        }

        private synchronized int removeWithTracing(final K key) {
            // FIFO is also updated to avoid 'removed' keys remaining at FIFO (which could end up reducing cache size to 1)
            final CacheEntry<V> removed = this.container.remove(key);
            if (removed == null) {
                // When tracing is active, this means nothing was removed
                return -1;
            }
            if (this.sizeLimit && key != null) {
                for (int i = 0; i < this.maxSize; i++) {
                    if (key.equals(this.fifo[i])) {
                        this.fifo[i] = null;
                        break;
                    }
                }
            }
            return this.container.size();
        }

        public void clear() {
            this.container.clear();
        }

        public int size() {
            return this.container.size();
        }

    }

    static final class CacheEntry<V> {

        private final SoftReference<V> cachedValueReference;
        private final long creationTimeInMillis;

        // Although we will use the reference for normal operation for cleaner code, this
        // variable will act as an "anchor" to avoid the value to be cleaned if we don't
        // want the reference type to be "soft"
        @SuppressWarnings("unused")
        private final V cachedValueAnchor;

        CacheEntry(final V cachedValue, final boolean useSoftReferences) {
            super();
            this.cachedValueReference = new SoftReference<>(cachedValue);
            this.cachedValueAnchor = (!useSoftReferences? cachedValue : null);
            this.creationTimeInMillis = System.currentTimeMillis();
        }

        public <K> V getValueIfStillValid(final String cacheMapName,
                                          final K key, final ICacheEntryValidityChecker<? super K, ? super V> checker,
                                          final boolean traceExecution, final Logger logger) {

            final V cachedValue = this.cachedValueReference.get();

            if (cachedValue == null) {
                // The soft reference has been cleared by GC -> Memory could be running low
                if (traceExecution) {
                    logger.trace(
                            "[THYMELEAF][{}][*][{}][CACHE_DELETED_REFERENCES] Some entries at cache \"{}\" " +
                                    "seem to have been sacrificed by the Garbage Collector (soft references).",
                            new Object[] {TemplateEngine.threadIndex(), cacheMapName, cacheMapName});
                }
                return null;
            }

            // START BLC MODIFICATION
            // Added check for NOT_FOUND, which indicates that the current cache does not exist, but we want to
            // handle it in an extensionManager.get() implementation
            if (checker == null || (cachedValue instanceof String && cachedValue.equals(NOT_FOUND))
                    || checker.checkIsValueStillValid(key, cachedValue, this.creationTimeInMillis)) {
                return cachedValue;
            }
            // END BLC MODIFICATION

            return null;
        }

        public long getCreationTimeInMillis() {
            return this.creationTimeInMillis;
        }

    }
}
