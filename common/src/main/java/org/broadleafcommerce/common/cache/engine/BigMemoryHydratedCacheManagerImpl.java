/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.common.cache.engine;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cache.spi.CacheKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class BigMemoryHydratedCacheManagerImpl extends AbstractHydratedCacheManager {

    private static final Log LOG = LogFactory.getLog(BigMemoryHydratedCacheManagerImpl.class);
    private static final BigMemoryHydratedCacheManagerImpl MANAGER = new BigMemoryHydratedCacheManagerImpl();

    public static BigMemoryHydratedCacheManagerImpl getInstance() {
        return MANAGER;
    }

    private Map<String, List<String>> cacheMemberNamesByEntity = Collections.synchronizedMap(new HashMap<String, List<String>>(100));
    private List<String> removeKeys = Collections.synchronizedList(new ArrayList<String>(100));
    private Cache offHeap = null;

    private BigMemoryHydratedCacheManagerImpl()  {
        //CacheManager.getInstance() and CacheManager.create() cannot be called in this constructor because it will create two cache manager instances
    }
    
    private synchronized Cache getHeap() {
        if (offHeap == null) {
            if (CacheManager.getInstance().cacheExists("hydrated-offheap-cache")) {
                offHeap = CacheManager.getInstance().getCache("hydrated-offheap-cache");
            } else {
                CacheConfiguration config = new CacheConfiguration("hydrated-offheap-cache", 500).eternal(true).overflowToOffHeap(true).maxMemoryOffHeap("1400M");
                Cache cache = new Cache(config);
                CacheManager.create().addCache(cache);
                offHeap = cache;
            }
        }
        return offHeap;
    }

    @Override
    public Object getHydratedCacheElementItem(String cacheRegion, String cacheName, Serializable elementKey, String elementItemName) {
        Element element;
        String myKey = cacheRegion + '_' + cacheName + '_' + elementItemName + '_' + elementKey;
        if (removeKeys.contains(myKey)) {
            return null;
        }
        Object response = null;
        element = getHeap().get(myKey);
        if (element != null) {
            response = element.getObjectValue();
        }
        return response;
    }

    @Override
    public void addHydratedCacheElementItem(String cacheRegion, String cacheName, Serializable elementKey, String elementItemName, Object elementValue) {
        String heapKey = cacheRegion + '_' + cacheName + '_' + elementItemName + '_' + elementKey;
        String nameKey = cacheRegion + '_' + cacheName + '_' + elementKey;
        removeKeys.remove(nameKey);
        Element element = new Element(heapKey, elementValue);
        if (!cacheMemberNamesByEntity.containsKey(nameKey)) {
            List<String> myMembers = new ArrayList<String>(50);
            myMembers.add(elementItemName);
            cacheMemberNamesByEntity.put(nameKey, myMembers);
        } else {
            List<String> myMembers = cacheMemberNamesByEntity.get(nameKey);
            myMembers.add(elementItemName);
        }
        getHeap().put(element);
    }

    protected void removeCache(String cacheRegion, Serializable key) {
        String cacheName = cacheRegion;
        if (key instanceof CacheKey) {
            cacheName = ((CacheKey) key).getEntityOrRoleName();
            key = ((CacheKey) key).getKey();
        }
        String nameKey = cacheRegion + '_' + cacheName + '_' + key;
        if (cacheMemberNamesByEntity.containsKey(nameKey)) {
            String[] members = new String[cacheMemberNamesByEntity.get(nameKey).size()];
            members = cacheMemberNamesByEntity.get(nameKey).toArray(members);
            for (String myMember : members) {
                String itemKey = cacheRegion + '_' + myMember + '_' + key;
                removeKeys.add(itemKey);
            }
            cacheMemberNamesByEntity.remove(nameKey);
        }
    }
    
    protected void removeAll(String cacheName) {
        //do nothing
    }

    @Override
    public void notifyElementEvicted(Ehcache arg0, Element arg1) {
        removeCache(arg0.getName(), arg1.getKey());
    }

    @Override
    public void notifyElementExpired(Ehcache arg0, Element arg1) {
        removeCache(arg0.getName(), arg1.getKey());
    }

    @Override
    public void notifyElementPut(Ehcache arg0, Element arg1) throws CacheException {
        //do nothing
    }

    @Override
    public void notifyElementRemoved(Ehcache arg0, Element arg1) throws CacheException {
        removeCache(arg0.getName(), arg1.getKey());
    }

    @Override
    public void notifyElementUpdated(Ehcache arg0, Element arg1) throws CacheException {
        removeCache(arg0.getName(), arg1.getKey());
    }

    @Override
    public void notifyRemoveAll(Ehcache arg0) {
        removeAll(arg0.getName());
    }

}
