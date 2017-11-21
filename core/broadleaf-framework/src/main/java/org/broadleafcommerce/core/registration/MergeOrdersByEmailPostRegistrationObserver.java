/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.registration;

import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.extension.PostUpdateOrderExtensionHandler;
import org.broadleafcommerce.core.order.extension.PostUpdateOrderExtensionManager;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.core.service.listener.PostRegistrationObserver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component("blMergeOrdersByEmailPostRegistrationObserver")
public class MergeOrdersByEmailPostRegistrationObserver implements PostRegistrationObserver {

    @Value("${merge.orders.after.registration:false}")
    private boolean enabled;

    @Resource(name = "blCustomerService")
    private CustomerService customerService;

    @Resource(name = "blOrderService")
    private OrderService orderService;

    @Resource(name = "blPostUpdateOrderExtensionManager")
    private PostUpdateOrderExtensionManager extensionManager;

    @PostConstruct
    protected void init() {
        if (enabled) {
            customerService.addPostRegisterListener(this);
        }
    }

    @Override
    public void processRegistrationEvent(Customer customer) {
        List<Order> orders = orderService.findOrdersByEmail(customer.getEmailAddress());
        List<Order> updOrders = new ArrayList<>();
        for (Order o : orders) {
            if (!o.getCustomer().isRegistered()) {
                o.setCustomer(customer);
                updOrders.add(o);
            }
        }
        List<PostUpdateOrderExtensionHandler> handlers = extensionManager.getHandlers();
        for (PostUpdateOrderExtensionHandler h : handlers) {
            ExtensionResultStatusType status = h.postUpdateAll(updOrders);
            if (!extensionManager.shouldContinue(status, null, null, null)) {
                break;
            }
        }
    }
}
