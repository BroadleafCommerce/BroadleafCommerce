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
            return new ThreadLocalManager();
        }
    };

    protected Map<Long, ThreadLocal> threadLocals = new LinkedHashMap<Long, ThreadLocal>();

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

    private static Long count = 0L;
    private static final Object threadLock = new Object();
}
