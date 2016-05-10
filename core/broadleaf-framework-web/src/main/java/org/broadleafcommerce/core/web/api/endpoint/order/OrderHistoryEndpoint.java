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
package org.broadleafcommerce.core.web.api.endpoint.order;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.web.api.BroadleafWebServicesException;
import org.broadleafcommerce.core.web.api.endpoint.BaseEndpoint;
import org.broadleafcommerce.core.web.api.wrapper.OrderWrapper;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * This endpoint depends on JAX-RS.  It should be extended by components that actually wish 
 * to provide an endpoint.  The annotations such as @Path, @Scope, @Context, @PathParam, @QueryParam, 
 * @GET, @POST, @PUT, and @DELETE are purposely not provided here to allow implementors finer control over 
 * the details of the endpoint.
 * <p/>
 * User: Kelly Tisdell
 * Date: 4/10/12
 */
public abstract class OrderHistoryEndpoint extends BaseEndpoint {

    @Resource(name="blOrderService")
    protected OrderService orderService;

    public List<OrderWrapper> findOrdersForCustomer(HttpServletRequest request,
            String orderStatus) {
        Customer customer = CustomerState.getCustomer(request);
        OrderStatus status = OrderStatus.getInstance(orderStatus);

        if (customer != null && status != null) {
            List<Order> orders = orderService.findOrdersForCustomer(customer, status);

            if (orders != null && !orders.isEmpty()) {
                List<OrderWrapper> wrappers = new ArrayList<OrderWrapper>();
                for (Order order : orders) {
                    OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
                    wrapper.wrapSummary(order, request);
                    wrappers.add(wrapper);
                }

                return wrappers;
            }

            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                    .addMessage(BroadleafWebServicesException.CART_NOT_FOUND);
        }

        throw BroadleafWebServicesException.build(HttpStatus.BAD_REQUEST.value())
                .addMessage(BroadleafWebServicesException.CUSTOMER_NOT_FOUND);
    }
}
