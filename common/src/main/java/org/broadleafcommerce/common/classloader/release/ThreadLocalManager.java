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
package org.broadleafcommerce.common.classloader.release;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class ThreadLocalManager {

    private static final Log LOG = LogFactory.getLog(ThreadLocalManager.class);

    private static final ThreadLocal<ThreadLocalManager> THREAD_LOCAL_MANAGER = new ThreadLocal<ThreadLocalManager>() {
        @Override
        protected ThreadLocalManager initialValue() {
            ThreadLocalManager manager = new ThreadLocalManager();
            String checkOrphans = System.getProperty("ThreadLocalManager.notify.orphans");
            if ("true".equals(checkOrphans)) {
                manager.marker = new RuntimeException("Thread Local Manager is not empty - the following is the culprit call that setup the thread local but did not clear it.");
            }
            return manager;
        }
    };

    protected Map<Long, ThreadLocal> threadLocals = new LinkedHashMap<Long, ThreadLocal>();
    protected RuntimeException marker = null;

    public static void addThreadLocal(ThreadLocal threadLocal) {
        Long position;
        synchronized (threadLock) {
            count++;
            position = count;
        }
        THREAD_LOCAL_MANAGER.get().threadLocals.put(position, threadLocal);
    }

    public static <T> ThreadLocal<T> createThreadLocal(final Class<T> type) {
        return createThreadLocal(type, true);
    }

    public static <T> ThreadLocal<T> createThreadLocal(final Class<T> type, final boolean createInitialValue) {
        ThreadLocal<T> response = new ThreadLocal<T>() {
            @Override
            protected T initialValue() {
                addThreadLocal(this);
                if (!createInitialValue) {
                    return null;
                }
                try {
                    return type.newInstance();
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void set(T value) {
                super.get();
                super.set(value);
            }
        };
        return response;
    }

    public static void remove() {
        for (Map.Entry<Long, ThreadLocal> entry : THREAD_LOCAL_MANAGER.get().threadLocals.entrySet()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Removing ThreadLocal #" + entry.getKey() + " from request thread.");
            }
            entry.getValue().remove();
        }
        THREAD_LOCAL_MANAGER.get().threadLocals.clear();
        THREAD_LOCAL_MANAGER.remove();
    }

    public static void remove(ThreadLocal threadLocal) {
        Long removePosition = null;
        for (Map.Entry<Long, ThreadLocal> entry : THREAD_LOCAL_MANAGER.get().threadLocals.entrySet()) {
            if (entry.getValue().equals(threadLocal)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Removing ThreadLocal #" + entry.getKey() + " from request thread.");
                }
                entry.getValue().remove();
                removePosition = entry.getKey();
            }
        }
        THREAD_LOCAL_MANAGER.get().threadLocals.remove(removePosition);
    }

    private static Long count = 0L;
    private static final Object threadLock = new Object();

    @Override
    public String toString() {
        if (!threadLocals.isEmpty() && marker != null) {
            marker.printStackTrace();
        }
        return super.toString();
    }
}
