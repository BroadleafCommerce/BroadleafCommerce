/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.rest.api.v2.endpoint.order;

import org.apache.commons.collections4.CollectionUtils;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.rest.api.v2.wrapper.OrderWrapper;
import org.broadleafcommerce.core.web.api.BroadleafWebServicesException;
import org.broadleafcommerce.core.web.api.endpoint.BaseEndpoint;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
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
    
    @Resource(name="blCustomerService")
    protected CustomerService customerService;

    public List<OrderWrapper> findOrdersForCustomer(HttpServletRequest request,
            String orderStatus, Long customerId) {
        Customer customer = customerService.readCustomerById(customerId);
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

    public List<OrderWrapper> findAllOrdersForCustomer(HttpServletRequest request, Long customerId) {
        Customer customer = customerService.readCustomerById(customerId);

        if (customer != null) {
            List<Order> orders = orderService.findOrdersForCustomer(customer);
            if (CollectionUtils.isNotEmpty(orders)) {
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

    public OrderWrapper findOrderById(HttpServletRequest request, Long orderId) {
        Order order = orderService.findOrderById(orderId);
        if (order == null) {
            throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CART_NOT_FOUND);
        }
        OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
        wrapper.wrapSummary(order, request);
        return wrapper;
    }
}
