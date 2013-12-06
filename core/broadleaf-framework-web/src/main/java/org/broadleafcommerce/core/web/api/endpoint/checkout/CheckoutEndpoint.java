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
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.core.checkout.service.CheckoutService;
import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutResponse;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentResponseItem;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.payment.domain.secure.Referenced;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.api.BroadleafWebServicesException;
import org.broadleafcommerce.core.web.api.endpoint.BaseEndpoint;
import org.broadleafcommerce.core.web.api.wrapper.OrderWrapper;
import org.broadleafcommerce.core.web.api.wrapper.PaymentReferenceMapWrapper;
import org.broadleafcommerce.core.web.api.wrapper.PaymentResponseItemWrapper;
import org.broadleafcommerce.core.web.order.CartState;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

    private static final Log LOG = LogFactory.getLog(CheckoutEndpoint.class);

    @Resource(name="blCheckoutService")
    protected CheckoutService checkoutService;

    @Resource(name="blOrderService")
    protected OrderService orderService;

    //This should only be called for modules that need to engage the workflow directly without doing a complete checkout.
    //e.g. PayPal for doing an authorize and retrieving the redirect: url to PayPal
    public PaymentResponseItemWrapper executePayment(HttpServletRequest request, PaymentReferenceMapWrapper mapWrapper) {
        Order cart = CartState.getCart();
        if (cart != null) {
            //try {
                Map<OrderPayment, Referenced> payments = new HashMap<OrderPayment, Referenced>();
                OrderPayment paymentInfo = mapWrapper.getPaymentInfoWrapper().unwrap(request, context);
                Referenced referenced = mapWrapper.getReferencedWrapper().unwrap(request, context);
                payments.put(paymentInfo, referenced);

                //CompositePaymentResponse compositePaymentResponse = compositePaymentService.executePayment(cart, payments);
                //CompositePaymentResponse compositePaymentResponse = new CompositePayme
                //PaymentResponseItem responseItem = compositePaymentResponse.getPaymentResponse().getResponseItems().get(paymentInfo);
                
                //TODO: FIXME PJV
                
                PaymentResponseItem responseItem = new PaymentResponseItem() {
                    
                    @Override
                    public void setUserName(String userName) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void setTransactionTimestamp(Date transactionTimestamp) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void setTransactionSuccess(Boolean transactionSuccess) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void setTransactionId(String transactionId) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void setTransactionAmount(Money amount) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void setRemainingBalance(Money remainingBalance) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void setProcessorResponseText(String processorResponseText) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void setProcessorResponseCode(String processorResponseCode) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void setPaymentTransaction(PaymentTransaction paymentTransaction) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void setMiddlewareResponseText(String middlewareResponseText) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void setMiddlewareResponseCode(String middlewareResponseCode) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void setImplementorResponseText(String implementorResponseText) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void setImplementorResponseCode(String implementorResponseCode) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void setAvsCode(String avsCode) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void setAuthorizationCode(String authorizationCode) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void setAdditionalFields(Map<String, String> additionalFields) {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public String getUserName() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public Date getTransactionTimestamp() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public Boolean getTransactionSuccess() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public String getTransactionId() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public Money getTransactionAmount() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public Money getRemainingBalance() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public String getProcessorResponseText() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public String getProcessorResponseCode() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public PaymentTransaction getPaymentTransaction() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public String getMiddlewareResponseText() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public String getMiddlewareResponseCode() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public String getImplementorResponseText() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public String getImplementorResponseCode() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public String getAvsCode() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public String getAuthorizationCode() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                    @Override
                    public Map<String, String> getAdditionalFields() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                };
                
                PaymentResponseItemWrapper paymentResponseItemWrapper = context.getBean(PaymentResponseItemWrapper.class);
                paymentResponseItemWrapper.wrapDetails(responseItem, request);

                return paymentResponseItemWrapper;

            //} catch (PaymentException e) {
            //    throw BroadleafWebServicesException.build(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), null, null, e);
            //}
        }
        throw BroadleafWebServicesException.build(Response.Status.NOT_FOUND.getStatusCode())
                .addMessage(BroadleafWebServicesException.CART_NOT_FOUND);

    }

    public OrderWrapper performCheckout(HttpServletRequest request, List<PaymentReferenceMapWrapper> mapWrappers) {
        Order cart = CartState.getCart();
        if (cart != null) {
            try {
                if (mapWrappers != null && !mapWrappers.isEmpty()) {
                    Map<OrderPayment, Referenced> payments = new HashMap<OrderPayment, Referenced>();
                    orderService.removePaymentsFromOrder(cart, PaymentType.CREDIT_CARD);

                    for (PaymentReferenceMapWrapper mapWrapper : mapWrappers) {
                        OrderPayment paymentInfo = mapWrapper.getPaymentInfoWrapper().unwrap(request, context);
                        paymentInfo.setOrder(cart);
                        Referenced referenced = mapWrapper.getReferencedWrapper().unwrap(request, context);

                        if (cart.getPayments() == null) {
                            cart.setPayments(new ArrayList<OrderPayment>());
                        }

                        cart.getPayments().add(paymentInfo);
                        payments.put(paymentInfo, referenced);
                    }

                    CheckoutResponse response = checkoutService.performCheckout(cart, payments);
                    Order order = response.getOrder();
                    OrderWrapper wrapper = (OrderWrapper) context.getBean(OrderWrapper.class.getName());
                    wrapper.wrapDetails(order, request);
                    return wrapper;
                }
            } catch (CheckoutException e) {

                cart.setStatus(OrderStatus.IN_PROCESS);

                try {
                    orderService.save(cart, false);
                } catch (PricingException e1) {
                    LOG.error("An unexpected error occured saving / pricing the cart.", e1);
                }

                throw BroadleafWebServicesException.build(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), null, null, e);
            }
        }
        throw BroadleafWebServicesException.build(Response.Status.NOT_FOUND.getStatusCode())
                .addMessage(BroadleafWebServicesException.CART_NOT_FOUND);

    }
}
