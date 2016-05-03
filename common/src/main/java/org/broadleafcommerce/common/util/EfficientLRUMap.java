/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.util;

import org.apache.commons.collections4.map.LRUMap;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class provides an LRUMap structure that defaults to a more efficient ConcurrentHashMap if the
 * size has not yet been reached.
 * 
 * In Broadleaf, there are many instances where an LRUMap could be used to guard against implementations
 * where Map sizes grow in unexpected ways.    However, the large majority of cases would fit well within the
 * max bounds of the LRUMap.
 * 
 * This class provides an approach that provides the benefits of a LRUMap for memory protection while
 * allowing concurrent access under normal circumstances.
 * 
 * For the first [n] entries, the underlying implementation will be a ConcurrentHashMap.   On the "n+1"th 
 * entry, this implementation will switch its underlying implementation to a synchronized LRUMap. 
 * 
 * @author bpolster
 * 
 */
public class EfficientLRUMap<K, V> implements Map<K, V> {

    private Map<K, V> concurrentMap;
    private Map<K, V> lruMap;
    private int maxEntries;
    private boolean usingLRUMap = false;
    
    public EfficientLRUMap(int maxEntries) {
        this.maxEntries = maxEntries;
        concurrentMap = new ConcurrentHashMap<K, V>();
    }

    @Override
    public int size() {
        if (usingLRUMap) {
            return lruMap.size();
        } else {
            return concurrentMap.size();
        }
    }

    @Override
    public boolean isEmpty() {
        if (usingLRUMap) {
            return lruMap.isEmpty();
        } else {
            return concurrentMap.isEmpty();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        if (usingLRUMap) {
            return lruMap.containsKey(key);
        } else {
            return concurrentMap.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        if (usingLRUMap) {
            return lruMap.containsValue(value);
        } else {
            return concurrentMap.containsValue(value);
        }
    }

    @Override
    public V get(Object key) {
        if (usingLRUMap) {
            return lruMap.get(key);
        } else {
            return concurrentMap.get(key);
        }
    }

    @Override
    public V put(K key, V value) {
        if (usingLRUMap) {
            return lruMap.put(key, value);
        } else {
            V returnVal = concurrentMap.put(key, value);
            if (switchToLRUMap()) {
                // The switch could have happened on another thread.
                if (!lruMap.containsKey(key)) {
                    lruMap.put(key, value);
                }
            }
            return returnVal;
        }
    }

    protected synchronized boolean switchToLRUMap() {
        if (!usingLRUMap) {
            if (size() > maxEntries) {
                lruMap = Collections.synchronizedMap(new LRUMap<K, V>(maxEntries));
                lruMap.putAll(concurrentMap);
                usingLRUMap = true;
                concurrentMap.clear();
            }
        }
        return usingLRUMap; // this could be set by another thread        
    }

    @Override
    public V remove(Object key) {
        if (usingLRUMap) {
            // This could put us back below the threshold for LRU vs. Concurrent but we won't optimize to that
            // level as we are likely to thrash going back and forth.    Once an LRU, always an LRU unless clear
            // is called.
            return lruMap.remove(key);
        } else {
            return concurrentMap.remove(key);
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        if (usingLRUMap) {
            lruMap.putAll(m);
        } else {
            concurrentMap.putAll(m);
            if (switchToLRUMap()) {
                // The switch could have happened on another thread.                
                lruMap.putAll(m);
            }
        }
    }

    @Override
    public void clear() {
        if (usingLRUMap) {
            resetInternalMap();
        } else {
            concurrentMap.clear();
        }
    }

    /**
     * We are clearing the map, so we can switch back to a {@link ConcurrentHashMap}
     */
    protected synchronized void resetInternalMap() {
        usingLRUMap = false;
        lruMap.clear();
    }

    @Override
    public Set<K> keySet() {
        if (usingLRUMap) {
            return lruMap.keySet();
        } else {
            return concurrentMap.keySet();
        }
    }

    @Override
    public Collection<V> values() {
        if (usingLRUMap) {
            return lruMap.values();
        } else {
            return concurrentMap.values();
        }
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        if (usingLRUMap) {
            return lruMap.entrySet();
        } else {
            return concurrentMap.entrySet();
        }
    }
    
    protected Class getUnderlyingMapClass() {
        if (usingLRUMap) {
            return lruMap.getClass();
        } else {
            return concurrentMap.getClass();
        }
    }
}
