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
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderLock;
import org.broadleafcommerce.core.order.service.OrderLockManager;
import org.broadleafcommerce.core.order.service.OrderService;

import javax.annotation.Resource;

/**
 * An implementation of the {@link OrderLockManager} that relies on the database to provide synchronization
 * for locks on {@link Order}s. This class leverages the {@link OrderLock} domain object to provide this
 * functionality.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class DatabaseOrderLockManager implements OrderLockManager {

    protected static final Log LOG = LogFactory.getLog(DatabaseOrderLockManager.class);
    
    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Override
    public Object acquireLock(Order order) {
        if (order instanceof NullOrderImpl) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Thread[" + Thread.currentThread().getId() + "] Attempted to grab a lock for a NullOrderImpl. ");
            }
            return order;
        }

        boolean lockAcquired = false;
        
        while (!lockAcquired) {
            try {
                lockAcquired = orderService.acquireLock(order);
            } catch (Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Couldn't acquire lock - that's ok, we'll retry shortly", e);
                }
            }

            if (!lockAcquired) {
                try {
                    int msToSleep = getDatabaseLockPollingIntervalMs();

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Thread[" + Thread.currentThread().getId() + "] Could not acquire order lock for order[" +
                                order.getId() + "] - sleeping for " + msToSleep + " ms");
                    }

                    Thread.sleep(msToSleep);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return order;
    }

    @Override
    public Object acquireLockIfAvailable(Order order) {
        if (order instanceof NullOrderImpl) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Attempted to grab a lock for a NullOrderImpl. Not blocking");
            }
            return order;
        }

        boolean lockAcquired = orderService.acquireLock(order); 
        return lockAcquired ? order : null;
    }

    @Override
    public void releaseLock(Object lockObject) {
        Order order = (Order) lockObject;
        if (order instanceof NullOrderImpl) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Thread[" + Thread.currentThread().getId() + "] Attempted to release a lock for a NullOrderImpl");
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Thread[" + Thread.currentThread().getId() + "] releasing lock for order[" + order.getId() + "]");
            }
            orderService.releaseLock(order);
        }
    }

    protected int getDatabaseLockPollingIntervalMs() {
        return BLCSystemProperty.resolveIntSystemProperty("order.lock.databaseLockPollingIntervalMs");
    }

    @Override
    public boolean isActive() {
        return true;
    }

}
