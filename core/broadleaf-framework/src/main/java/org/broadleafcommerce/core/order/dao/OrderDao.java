/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.order.dao;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderLock;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.profile.core.domain.Customer;

import java.util.Date;
import java.util.List;

public interface OrderDao {

    Order readOrderById(Long orderId);
    
    List<Order> readOrdersByIds(List<Long> orderIds);

    /**
     * Reads a batch list of orders from the DB.  The status is optional and can be null.  If no status 
     * is provided, then all order will be read.  Otherwise, only orders with that status will be read.
     * @param start
     * @param pageSize
     * @param statuses
     * @return
     */
    List<Order> readBatchOrders(int start, int pageSize, List<OrderStatus> statuses);

    Order readOrderById(Long orderId, boolean refresh);

    Order readOrderByExternalId(String orderExternalId);

    List<Order> readOrdersForCustomer(Customer customer, OrderStatus orderStatus);

    List<Order> readOrdersForCustomer(Long id);

    Order readNamedOrderForCustomer(Customer customer, String name);

    Order readCartForCustomer(Customer customer);

    Order save(Order order);

    void delete(Order order);

    Order submitOrder(Order cartOrder);

    Order create();

    void refresh(Order order);

    Order createNewCartForCustomer(Customer customer);

    Order readOrderByOrderNumber(String orderNumber);

    List<Order> readOrdersByDateRange(Date startDate, Date endDate);

    List<Order> readOrdersForCustomersInDateRange(List<Long> customerIds, Date startDate, Date endDate);

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

    List<Order> readOrdersByEmail(String email);
}
