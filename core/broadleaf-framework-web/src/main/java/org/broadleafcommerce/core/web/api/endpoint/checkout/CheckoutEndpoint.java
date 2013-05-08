/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web.api.endpoint.checkout;

import org.broadleafcommerce.core.checkout.service.CheckoutService;
import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutResponse;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentResponseItem;
import org.broadleafcommerce.core.payment.domain.Referenced;
import org.broadleafcommerce.core.payment.service.CompositePaymentService;
import org.broadleafcommerce.core.payment.service.exception.PaymentException;
import org.broadleafcommerce.core.payment.service.workflow.CompositePaymentResponse;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.api.endpoint.BaseEndpoint;
import org.broadleafcommerce.core.web.api.wrapper.OrderWrapper;
import org.broadleafcommerce.core.web.api.wrapper.PaymentReferenceMapWrapper;
import org.broadleafcommerce.core.web.api.wrapper.PaymentResponseItemWrapper;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

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

    @Resource(name="blCheckoutService")
    protected CheckoutService checkoutService;

    //This service is backed by the entire payment workflow configured in the application context.
    //It is the entry point for engaging the payment workflow
    @Resource(name="blCompositePaymentService")
    protected CompositePaymentService compositePaymentService;

    @Resource(name="blOrderService")
    protected OrderService orderService;

    //This should only be called for modules that need to engage the workflow directly without doing a complete checkout.
    //e.g. PayPal for doing an authorize and retrieving the redirect: url to PayPal
    public PaymentResponseItemWrapper executePayment(HttpServletRequest request, PaymentReferenceMapWrapper mapWrapper) {
        Customer customer = CustomerState.getCustomer(request);

        if (customer != null) {
            Order cart = orderService.findCartForCustomer(customer);
            if (cart != null) {
                try {
                        Map<PaymentInfo, Referenced> payments = new HashMap<PaymentInfo, Referenced>();
                        PaymentInfo paymentInfo = mapWrapper.getPaymentInfoWrapper().unwrap(request, context);
                        Referenced referenced = mapWrapper.getReferencedWrapper().unwrap(request, context);
                        payments.put(paymentInfo, referenced);

                        CompositePaymentResponse compositePaymentResponse = compositePaymentService.executePayment(cart, payments);
                        PaymentResponseItem responseItem = compositePaymentResponse.getPaymentResponse().getResponseItems().get(paymentInfo);

                        PaymentResponseItemWrapper paymentResponseItemWrapper = context.getBean(PaymentResponseItemWrapper.class);
                        paymentResponseItemWrapper.wrap(responseItem, request);

                        return paymentResponseItemWrapper;

                } catch (PaymentException e) {
                    throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                }
            }
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }

    public OrderWrapper performCheckout(HttpServletRequest request, List<PaymentReferenceMapWrapper> mapWrappers) {
        Customer customer = CustomerState.getCustomer(request);

        if (customer != null) {
            Order cart = orderService.findCartForCustomer(customer);
            if (cart != null) {
                try {
                    if (mapWrappers != null && !mapWrappers.isEmpty()) {
                        Map<PaymentInfo, Referenced> payments = new HashMap<PaymentInfo, Referenced>();
                        List<PaymentInfo> paymentInfos = new ArrayList<PaymentInfo>();

                        for (PaymentReferenceMapWrapper mapWrapper : mapWrappers) {
                            PaymentInfo paymentInfo = mapWrapper.getPaymentInfoWrapper().unwrap(request, context);
                            Referenced referenced = mapWrapper.getReferencedWrapper().unwrap(request, context);

                            payments.put(paymentInfo, referenced);
                            paymentInfos.add(paymentInfo);
                        }

                        cart.setPaymentInfos(paymentInfos);
                        cart.setStatus(OrderStatus.SUBMITTED);
                        cart.setSubmitDate(Calendar.getInstance().getTime());

                        CheckoutResponse response = checkoutService.performCheckout(cart, payments);
                        Order order = response.getOrder();
                        OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
                        wrapper.wrap(order,request);
                        return wrapper;
                    }
                } catch (CheckoutException e) {

                    cart.setStatus(OrderStatus.IN_PROCESS);

                    try {
                        orderService.save(cart, false);
                    } catch (PricingException e1) {
                        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                    }

                    throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
                }
            }
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        throw new WebApplicationException(Response.Status.BAD_REQUEST);
    }
}
