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
package org.broadleafcommerce.core.order.dao;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderLock;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.profile.core.domain.Customer;

import java.util.Date;
import java.util.List;

public interface OrderDao {

    Order readOrderById(Long orderId);
    
    Order readOrderById(Long orderId, boolean refresh);

    List<Order> readOrdersForCustomer(Customer customer, OrderStatus orderStatus);

    List<Order> readOrdersForCustomer(Long id);

    Order readNamedOrderForCustomer(Customer customer, String name);

    Order readCartForCustomer(Customer customer);

    Order save(Order order);

    void delete(Order order);

    Order submitOrder(Order cartOrder);

    Order create();

    Order createNewCartForCustomer(Customer customer);

    Order readOrderByOrderNumber(String orderNumber);
    
    Order updatePrices(Order order);

    /**
     * This method will attempt to update the {@link OrderLock} object table for the given order to mark it as
     * locked, provided the OrderLock record for the given order was not already locked. It will return true or
     * false depending on whether or not the lock was able to be acquired.
     * 
     * @param order
     * @return true if the lock was acquired, false otherwise
     */
    public boolean acquireLock(Order order);

    /**
     * Releases the lock for the given order. Note that this method will release the lock for the order whether or not
     * the caller was the current owner of the lock. As such, callers of this method should take care to ensure they
     * hold the lock before attempting to release it.
     * 
     * @param order
     * @return true if the lock was successfully released, false otherwise
     */
    public boolean releaseLock(Order order);

}
