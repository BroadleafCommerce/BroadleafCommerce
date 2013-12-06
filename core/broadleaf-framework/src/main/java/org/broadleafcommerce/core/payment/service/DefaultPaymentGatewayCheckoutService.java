/*
 * Copyright 2008-2013 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.payment.service;

import org.broadleafcommerce.common.payment.dto.CreditCardDTO;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.payment.service.PaymentGatewayCheckoutService;
import org.broadleafcommerce.common.web.payment.controller.PaymentGatewayAbstractController;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.payment.service.type.PaymentType;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;


/**
 * Core framework implementation of the {@link PaymentGatewayCheckoutService}.
 * 
 * @see {@link PaymentGatewayAbstractController}
 * @author Phillip Verheyden (phillipuniverse)
 */
@Service("blPaymentGatewayCheckoutService")
public class DefaultPaymentGatewayCheckoutService implements PaymentGatewayCheckoutService {

    @Resource(name = "blOrderService")
    protected OrderService orderService;
    
    @Resource(name = "blOrderPaymentService")
    protected OrderPaymentService orderPaymentService;
    
    @Override
    public Long applyPaymentToOrder(PaymentResponseDTO responseDTO) {
        
        //Payments can ONLY be parsed into PaymentInfos if they are 'valid'
        if (!responseDTO.getValid()) {
            throw new IllegalArgumentException("Invalida payment responses cannot be parsed into the order payment domain");
        }
        
        Long orderId = Long.parseLong(responseDTO.getOrderId());
        Order order = orderService.findOrderById(orderId);
        
        //TODO: ensure that the order has not already been checked out before applying payments to it
        
        List<OrderPayment> payments = order.getPayments();
        
        //TODO: fill out order.getCustomer() values for anonymous customers based on values returned from the response
        PaymentType type = null;
        if (responseDTO.getCreditCard() instanceof CreditCardDTO) {
            type = PaymentType.CREDIT_CARD;
        }
        
        // This should not have to remove any order payments; this method should only be additive. This should do nothing
        // but simply create an order payment and assign it to the order. If there is some sort of failed payment as a result
        // of all this, it is the job of the individual module to clean itself up by invoking markPaymentAsInvalid
        
        // that said, it might be more appropriate to, rather than create an entirely new order payment, to instead look
        // up the old one and then add another transaction to it. But, in the majority of cases this I don't think makes much
        // logical sense as the amount of the payment might change between invocations of this method.
        
        OrderPayment payment = orderPaymentService.create();
        payment.setType(type);
        payment.setAmount(responseDTO.getAmount());
        
        //TODO: add billing address fields to the payment response DTO
        //payment.setBillingAddress(billingAddress)
        
        //TODO: I think this reference number should be completely optional. OOB I don't think there is any reason it needs
        //to be set.
        //payment.setReferenceNumber(referenceNumber)
        
        PaymentTransaction transaction = orderPaymentService.createTransaction();
        transaction.setAmount(responseDTO.getAmount());
        transaction.setRawResponse(responseDTO.getRawResponse());
        transaction.setSuccess(responseDTO.getSuccessful());
        
        //TODO: get the transaction type from the response DTO
        //transaction.setType(type);
        
        //TODO: validate that this particular type of transaction is valid to be added to the payment (there might already
        // be an AUTHORIZE transaction, for instance)
        payment.addTransaction(transaction);
        
        orderService.addPaymentToOrder(order, payment, null);
        
        return null;
    }

    @Override
    public void markPaymentAsInvalid(Long orderPaymentId) {
        // TODO Auto-generated method stub
    }

    @Override
    public String initiateCheckout(Long orderId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String lookupOrderNumberFromOrderId(PaymentResponseDTO responseDTO) {
        // TODO Auto-generated method stub
        return null;
    }

}
