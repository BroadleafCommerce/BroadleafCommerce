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
package org.broadleafcommerce.core.web.api.endpoint.checkout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.checkout.service.CheckoutService;
import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutResponse;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.service.OrderPaymentService;
import org.broadleafcommerce.core.web.api.BroadleafWebServicesException;
import org.broadleafcommerce.core.web.api.endpoint.BaseEndpoint;
import org.broadleafcommerce.core.web.api.wrapper.OrderPaymentWrapper;
import org.broadleafcommerce.core.web.api.wrapper.OrderWrapper;
import org.broadleafcommerce.core.web.order.CartState;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * This endpoint depends on JAX-RS to provide checkout services.  It should be extended by components that actually wish 
 * to provide an endpoint.  The annotations such as @Path, @Scope, @Context, @PathParam, @QueryParam, 
 * @GET, @POST, @PUT, and @DELETE are purposely not provided here to allow implementors finer control over 
 * the details of the endpoint.
 * <p/>
 * User: Kelly Tisdell
 * Date: 4/10/12
 */
public abstract class CheckoutEndpoint extends BaseEndpoint {

    private static final Log LOG = LogFactory.getLog(CheckoutEndpoint.class);

    @Resource(name="blCheckoutService")
    protected CheckoutService checkoutService;

    @Resource(name="blOrderService")
    protected OrderService orderService;

    @Resource(name="blOrderPaymentService")
    protected OrderPaymentService orderPaymentService;

    public List<OrderPaymentWrapper> findPaymentsForOrder(HttpServletRequest request) {
        Order cart = CartState.getCart();
        if (cart != null && cart.getPayments() != null && !cart.getPayments().isEmpty()) {
            List<OrderPayment> payments = cart.getPayments();
            List<OrderPaymentWrapper> paymentWrappers = new ArrayList<OrderPaymentWrapper>();
            for (OrderPayment payment : payments) {
                OrderPaymentWrapper orderPaymentWrapper = (OrderPaymentWrapper) context.getBean(OrderPaymentWrapper.class.getName());
                orderPaymentWrapper.wrapSummary(payment, request);
                paymentWrappers.add(orderPaymentWrapper);
            }
            return paymentWrappers;
        }

        throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CART_NOT_FOUND);
    }

    public OrderPaymentWrapper addPaymentToOrder(HttpServletRequest request,
                                                              OrderPaymentWrapper wrapper) {
        Order cart = CartState.getCart();
        if (cart != null) {
            OrderPayment orderPayment = wrapper.unwrap(request, context);

            if (orderPayment.getOrder() != null && orderPayment.getOrder().getId().equals(cart.getId())) {
                orderPayment = orderPaymentService.save(orderPayment);
                OrderPayment savedPayment = orderService.addPaymentToOrder(cart, orderPayment, null);
                OrderPaymentWrapper orderPaymentWrapper = (OrderPaymentWrapper) context.getBean(OrderPaymentWrapper.class.getName());
                orderPaymentWrapper.wrapSummary(savedPayment, request);
                return orderPaymentWrapper;
            }
        }

        throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CART_NOT_FOUND);

    }

    public OrderWrapper removePaymentFromOrder(HttpServletRequest request, OrderPaymentWrapper wrapper) {

        Order cart = CartState.getCart();
        if (cart != null) {
            OrderPayment orderPayment = wrapper.unwrap(request, context);

            if (orderPayment.getOrder() != null && orderPayment.getOrder().getId().equals(cart.getId())) {
                OrderPayment paymentToRemove = null;
                for (OrderPayment payment : cart.getPayments()) {
                    if (payment.getId().equals(orderPayment.getId())) {
                        paymentToRemove = payment;
                    }
                }

                orderService.removePaymentFromOrder(cart, paymentToRemove);

                OrderWrapper orderWrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
                orderWrapper.wrapDetails(cart, request);
                return orderWrapper;
            }
        }

        throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CART_NOT_FOUND);
    }

    public OrderWrapper performCheckout(HttpServletRequest request) {
        Order cart = CartState.getCart();
        if (cart != null) {
            try {
                CheckoutResponse response = checkoutService.performCheckout(cart);
                Order order = response.getOrder();
                OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
                wrapper.wrapDetails(order, request);
                return wrapper;
            } catch (CheckoutException e) {
                throw BroadleafWebServicesException.build(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .addMessage(BroadleafWebServicesException.CHECKOUT_PROCESSING_ERROR);
            }
        }

        throw BroadleafWebServicesException.build(HttpStatus.NOT_FOUND.value())
                .addMessage(BroadleafWebServicesException.CART_NOT_FOUND);

    }
}
