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

import java.util.Date;
import java.util.List;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.profile.core.domain.Customer;

public interface OrderDao {

    Order readOrderById(Long orderId);

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
     * Deletes carts from the database. Carts are generally considered orders that have
     * not made it to the submitted status. The method parameters can be left null, or included to refine
     * the deletion criteria. Note, if statuses are null, the query defaults to selecting
     * only orders that have a status of IN_PROCESS.
     *
     * @param names One or more order names to restrict the deletion by. Can be null.
     * @param statuses One or more order statuses to restrict the deletion by. Can be null.
     * @param dateCreatedMinThreshold Min creation date to restrict the deletion by. Orders created before this date
     *                                are removed. Can be null.
     * @return the number of deleted carts
     */
    List<Order> findCarts(String[] names, OrderStatus[] statuses, Date dateCreatedMinThreshold);

}
