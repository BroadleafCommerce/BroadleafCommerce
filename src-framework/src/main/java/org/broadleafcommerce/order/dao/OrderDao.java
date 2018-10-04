/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.order.dao;

import java.util.List;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.profile.domain.Customer;

public interface OrderDao {

    public Order readOrderById(Long orderId);

    Order readOrderById(Long orderId, boolean refresh);

    public List<Order> readOrdersForCustomer(Customer customer, OrderStatus orderStatus);

    public List<Order> readOrdersForCustomer(Long id);

    public Order readNamedOrderForCustomer(Customer customer, String name);

    public Order readCartForCustomer(Customer customer);

    public Order save(Order order);

    public void delete(Order order);

    public Order submitOrder(Order cartOrder);

    public Order create();

    public Order createNewCartForCustomer(Customer customer);

    public Order readOrderByOrderNumber(String orderNumber);

    public Order updatePrices(Order order);

    //    removed methods
    //    public List<Order> readNamedOrdersForcustomer(Customer customer);
    //
    //    public Order readOrderForCustomer(Long customerId, Long orderId);
    //
    //    public List<Order> readSubmittedOrdersForCustomer(Customer customer);
    //

    public boolean acquireLock(Order order);

    public boolean releaseLock(Order order);
}
