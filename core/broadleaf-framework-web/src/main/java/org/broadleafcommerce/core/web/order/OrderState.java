/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.order;

import org.broadleafcommerce.core.order.dao.OrderDao;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.profile.core.domain.Customer;

import java.util.HashMap;

import javax.annotation.Resource;

/**
 * This class is used as a request-scope container for the current
 * orderid. As a result, items that need the order during the control
 * flow of a single request may retrieve the order from this object. OrderState
 * utilizes the DAO to retrieve the full order from its dehydrated state in the
 * Hibernate cache.
 * 
 * @author jfischer
 *
 */
public class OrderState {

    private final HashMap<Long, Long> orders = new HashMap<Long, Long>();

    @Resource(name = "blOrderDao")
    protected OrderDao orderDao;

    public Order getOrder(Customer customer) {
        if (orders.get(customer.getId()) == null) {
            return null;
        }
        Order order = orderDao.readOrderById(orders.get(customer.getId()));
        return order;
    }

    public Order setOrder(Customer customer, Order order) {
        if (customer != null && order != null) {
            orders.put(customer.getId(), order.getId());
        }
        return order;
    }

}
