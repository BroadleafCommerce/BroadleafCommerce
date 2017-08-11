/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.service;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Jacob Mitash
 */
@Service("blOrderHistoryService")
public class OrderHistoryServiceImpl implements OrderHistoryService {

    public static final String ACTIVE_CUSTOMER_OWNERSHIP_ERROR_MESSAGE = "The active customer does not own the object that they are trying to view, edit, or remove.";

    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Autowired
    protected Environment env;

    @Override
    public Order getOrderDetails(String orderNumber) {
        Order order = orderService.findOrderByOrderNumber(orderNumber);
        if (order == null) {
            throw new IllegalArgumentException("The orderNumber provided is not valid");
        }

        validateCustomerOwnedData(order);

        return order;
    }

    @Override
    public void validateCustomerOwnedData(Order order) throws SecurityException {
        if (shouldValidateCustomerOwnedData()) {
            Customer activeCustomer = CustomerState.getCustomer();
            if (activeCustomer != null && !(activeCustomer.equals(order.getCustomer()))) {
                throw new SecurityException(ACTIVE_CUSTOMER_OWNERSHIP_ERROR_MESSAGE);
            } else if (activeCustomer == null && order.getCustomer() != null) {
                throw new SecurityException(ACTIVE_CUSTOMER_OWNERSHIP_ERROR_MESSAGE);
            }
        }
    }

    protected boolean shouldValidateCustomerOwnedData() {
        return env.getProperty("validate.customer.owned.data", boolean.class, true);
    }

}
