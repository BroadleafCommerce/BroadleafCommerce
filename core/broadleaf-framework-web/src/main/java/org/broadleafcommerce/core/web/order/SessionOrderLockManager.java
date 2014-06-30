/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.order;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderLockManager;
import org.springframework.context.ApplicationListener;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * An {@link HttpSession} based {@link OrderLockManager}. This implementation is less concerned with the given Order
 * and instead will lock on the user's session to serialize order modification requests.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class SessionOrderLockManager implements OrderLockManager, ApplicationListener<HttpSessionDestroyedEvent> {

    private static final Log LOG = LogFactory.getLog(SessionOrderLockManager.class);
    private static final Object LOCK = new Object();
    private static final ConcurrentMap<String, ReentrantLock> SESSION_LOCKS;
    
    static {
        SESSION_LOCKS = new ConcurrentLinkedHashMap.Builder<String, ReentrantLock>()
            .maximumWeightedCapacity(10000)
            .build();
    }

    /**
     * Note that although this method accepts an {@link Order} parameter, it does not use it in any way. This 
     * session-based lock manager implementation will prevent all operations that are identified as requiring a
     * lock from happening in parallel. Instead, it will execute them sequentially as locks are released from 
     * previous implementations.
     */
    @Override
    public Object acquireLock(Order order) {
        ReentrantLock lockObject = getSessionLock();
        lockObject.lock();
        return lockObject;
    }

    @Override
    public void releaseLock(Object lockObject) {
        ReentrantLock lock = (ReentrantLock) lockObject;
        lock.unlock();
    }
    
    @Override
    public void onApplicationEvent(HttpSessionDestroyedEvent event) {
        ReentrantLock lock = SESSION_LOCKS.remove(event.getSession().getId());
        if (lock != null && LOG.isDebugEnabled()) {
            LOG.debug("Destroyed lock due to session invalidation: " + lock.toString());
        }
    }

    protected HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    protected ReentrantLock getSessionLock() {
        HttpSession session = getRequest().getSession();
        ReentrantLock lock = SESSION_LOCKS.get(session.getId());

        if (lock == null) {
            // There was no session lock object. We'll need to create one. To do this, we have to synchronize the
            // creation globally, so that two threads don't create the session lock at the same time.
            synchronized (LOCK) {
                lock = SESSION_LOCKS.get(session.getId());
                if (lock == null) {
                    lock = new ReentrantLock();
                    SESSION_LOCKS.put(session.getId(), lock);
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Created new lock object: " + lock.toString());
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Returning previously created lock object: " + lock.toString());
            }
        }

        return lock;
    }


}
