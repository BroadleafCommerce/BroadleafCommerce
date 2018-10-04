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
package org.broadleafcommerce.order;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderLock;
import org.broadleafcommerce.order.service.OrderLockManager;
import org.broadleafcommerce.order.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * An implementation of the {@link OrderLockManager} that relies on the database to provide synchronization
 * for locks on {@link Order}s. This class leverages the {@link OrderLock} domain object to provide this
 * functionality.
 *
 * @author Andre Azzolini (apazzolini)
 */
public class DatabaseOrderLockManager implements OrderLockManager {

    @Autowired
    protected Environment env;

    protected static final Log LOG = LogFactory.getLog(DatabaseOrderLockManager.class);

    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Override
    public Object acquireLock(Order order) {
        if (order == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Thread[" + Thread.currentThread().getId() + "] Attempted to grab a lock for a null order. ");
            }
            return order;
        }

        boolean lockAcquired = false;
        int count = 0;
        while (!lockAcquired) {
            try {
                lockAcquired = orderService.acquireLock(order);
            } catch (Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Couldn't acquire lock - that's ok, we'll retry shortly", e);
                }
            }

            if (!lockAcquired) {
                count++;
                if (count >= getDatabaseLockAcquisitionNumRetries()) {
                    LOG.warn(String.format("Exceeded max retries to attempt to acquire a lock on current Order (%s)", order.getId()));
                    throw new RuntimeException("Exceeded max retries to attempt to acquire a lock on current Order");
                }
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
        boolean lockAcquired = orderService.acquireLock(order);
        return lockAcquired ? order : null;
    }

    @Override
    public void releaseLock(Object lockObject) {
        Order order = (Order) lockObject;

        if (LOG.isDebugEnabled()) {
            LOG.debug("Thread[" + Thread.currentThread().getId() + "] releasing lock for order[" + order.getId() + "]");
        }
        orderService.releaseLock(order);
    }

    protected int getDatabaseLockPollingIntervalMs() {
        return Integer.valueOf(env.getProperty("order.lock.databaseLockPollingIntervalMs", "1000"));
    }

    protected int getDatabaseLockAcquisitionNumRetries() {
        return Integer.valueOf(env.getProperty("order.lock.databaseLockAcquisitionNumRetries", "5"));
    }

    @Override
    public boolean isActive() {
        return true;
    }

}