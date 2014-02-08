/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.checkout.service;

import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutResponse;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutSeed;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.exception.RequiredAttributeNotProvidedException;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.workflow.ActivityMessages;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.Processor;
import org.broadleafcommerce.core.workflow.WorkflowException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Resource;

@Service("blCheckoutService")
public class CheckoutServiceImpl implements CheckoutService {

    @Resource(name="blCheckoutWorkflow")
    protected Processor checkoutWorkflow;

    @Resource(name="blOrderService")
    protected OrderService orderService;
    
    /**
     * Map of locks for given order ids. This lock map ensures that only a single request can handle a particular order
     * at a time
     */
    protected static ConcurrentMap<Long, Object> lockMap = new ConcurrentHashMap<Long, Object>();

    @Override
    public CheckoutResponse performCheckout(Order order) throws CheckoutException {
        CheckoutSeed seed = null;
        try {
            seed = new CheckoutSeed(order, new HashMap<String, Object>());
            
            //Immediately fail if another thread is currently attempting to check out the order
            Object lockObject = putLock(order.getId());
            if (lockObject != null) {
                throw new CheckoutException("This order is already in the process of being submitted, unable to checkout order -- id: " + order.getId(), seed);
            }

            // Immediately fail if this order has already been checked out previously
            if (hasOrderBeenCompleted(order)) {
                throw new CheckoutException("This order has already been submitted, unable to checkout order -- id: " + order.getId(), seed);
            }
            
            order = orderService.save(order, false);

            ProcessContext<CheckoutSeed> context = (ProcessContext<CheckoutSeed>) checkoutWorkflow.doActivities(seed);

            // We need to pull the order off the seed and save it here in case any activity modified the order.
            order = orderService.save(seed.getOrder(), false);
            order.getOrderMessages().addAll(((ActivityMessages) context).getActivityMessages());
            seed.setOrder(order);

            return seed;
        } catch (PricingException e) {
            throw new CheckoutException("Unable to checkout order -- id: " + order.getId(), e, seed);
        } catch (WorkflowException e) {
            throw new CheckoutException("Unable to checkout order -- id: " + order.getId(), e.getRootCause(), seed);
        } catch (RequiredAttributeNotProvidedException e) {
            throw new CheckoutException("Unable to checkout order -- id: " + order.getId(), e.getCause(), seed);
        } finally {
            // The order has completed processing, remove the order from the processing map
            removeLock(order.getId());
        }
    }
    
    /**
     * Checks if the <b>order</b> has already been gone through the checkout workflow.
     * 
     * @param order
     * @return
     */
    protected boolean hasOrderBeenCompleted(Order order) {
        return OrderStatus.SUBMITTED.equals(order.getStatus());
    }

    /**
    * Get an object to lock on for the given order id
    * 
    * @param orderId
    * @return null if there was not already a lock object available. If an object was already in the map, this will return
    * that object, which means that there is already a thread attempting to go through the checkout workflow
    */
    protected Object putLock(Long orderId) {
        return lockMap.putIfAbsent(orderId, new Object());
    }
    
    /**
     * Done with processing the given orderId, remove the lock from the map
     * 
     * @param orderId
     */
    protected void removeLock(Long orderId) {
        lockMap.remove(orderId);
    }
    
}
