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
/**
 * 
 */
package org.broadleafcommerce.core.web.expression;

import org.broadleafcommerce.common.web.expression.BroadleafVariableExpression;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;

import javax.annotation.Resource;


/**
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class OrderVariableExpression implements BroadleafVariableExpression {

    @Resource(name = "blOrderService")
    protected OrderService orderService;
    
    @Override
    public String getName() {
        return "orders";
    }

    public Order getNamedOrderForCurrentCustomer(String orderName) {
        return getNamedOrderForCustomer(orderName, CustomerState.getCustomer());
    }
    
    public Order getNamedOrderForCustomer(String orderName, Customer customer) {
        return orderService.findNamedOrderForCustomer(orderName, customer);
    }
}
