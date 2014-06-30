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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * An {@link HttpSession} based {@link OrderLockManager}. This implementation is less concerned with the given Order
 * and instead will lock on the user's session to serialize order modification requests.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class SessionOrderLockManager implements OrderLockManager {

    private static final Log LOG = LogFactory.getLog(SessionOrderLockManager.class);
    private static final Object LOCK = new Object();
    private static final String LOCK_SESSION_ATTR_NAME = "SESSION_LOCK";

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

    protected HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    protected ReentrantLock getSessionLock() {
        HttpSession session = getRequest().getSession();
        ReentrantLock result = (ReentrantLock) session.getAttribute(LOCK_SESSION_ATTR_NAME);

        if (result == null) {
            // There was no session lock object. We'll need to create one. To do this, we have to synchronize the
            // creation globally, so that two threads don't create the session lock at the same time.
            synchronized (LOCK) {
                result = (ReentrantLock) session.getAttribute(LOCK_SESSION_ATTR_NAME);
                if (result == null) {
                    result = new ReentrantLock();
                    session.setAttribute(LOCK_SESSION_ATTR_NAME, result);
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Created new lock object: " + result.toString());
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Returning previously created lock object: " + result.toString());
            }
        }

        return result;
    }

}
