package org.broadleafcommerce.cms.url.dao;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Resource;
import javax.cache.Cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extensibility.cache.JCacheUtil;
import org.broadleafcommerce.common.util.StreamingTransactionCapableUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

public class FullTableCacheStrategy {

    @Resource(name = "blJCacheUtil")
    protected JCacheUtil jcacheUtil;

    @Resource(name="blStreamingTransactionCapableUtil")
    protected StreamingTransactionCapableUtil transUtil;
    
    private static final Log LOG = LogFactory.getLog(FullTableCacheStrategy.class);

    protected String cacheName;
    protected long cacheRefreshInterval;
    protected ReadWriteLock lock = new ReentrantReadWriteLock(true);
    protected int cacheNameIncrement = 1;
    protected Timer refreshTimer;
    protected Cache<Object, Object> currentCache;
    protected Cache<Object, Object> rebuildCache;

    protected boolean isStarted = false;
    
    protected FullTableCacheOperation cacheOperation;

    public FullTableCacheStrategy(String cacheName, long cacheRefreshInterval) {
        this.cacheName = cacheName;
        this.cacheRefreshInterval = cacheRefreshInterval;
    }
    
    protected void startRefreshCycle() {
        isStarted = true;
        if (refreshTimer == null) {
            refreshTimer = new Timer(cacheName, true);
        }        
        refreshCache();
        System.out.println("REFRESH TIMER " + refreshTimer);
        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    refreshCache();
                } catch (Exception e) {
                    LOG.error("Unable to refresh cache", e);
                }
            }
        }, cacheRefreshInterval, cacheRefreshInterval);
    }

    public void stopRefreshCycle() {
        refreshTimer.cancel();
    }

    public boolean isRefreshCycleRunning() {
        return isStarted;
    }

    public Object findItemInCache(String key) {
        Object itemToReturn = null;
        lock.readLock().lock();
        try {
            itemToReturn = currentCache.get(key);
        } finally {
            lock.readLock().unlock();
        }
        return itemToReturn;
    }
    
    public void initializeCache(FullTableCacheOperation operation) {
        cacheOperation = operation;
        startRefreshCycle();
    }
    
    protected void swapCaches() {
        Cache<Object, Object> expiredCache = currentCache;
        lock.writeLock().lock();
        try {
            currentCache = rebuildCache;
        } finally {
            lock.writeLock().unlock();
        }
        if (expiredCache != null) {
            jcacheUtil.getCacheManager().destroyCache(expiredCache.getName());
        }        
    }
    
    protected void refreshCache() {
        if (cacheOperation != null) {
            transUtil.runOptionalEntityManagerInViewOperation(new Runnable() {
                @Override
                public void run() {
                    Long elementsInCache = cacheOperation.findFullCacheRowCount();
                    if (elementsInCache > 0L) {
                        rebuildCache = jcacheUtil.createCache(cacheName + cacheNameIncrement, -1, elementsInCache.intValue());
                        cacheNameIncrement = cacheNameIncrement == 2 ? 1 : 2;
                        cacheOperation.buildNewFullCache(rebuildCache);
                    }
                }
            });
            swapCaches();
        }
    }

}