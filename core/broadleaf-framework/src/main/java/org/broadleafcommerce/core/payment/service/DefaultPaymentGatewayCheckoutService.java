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
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfigurationService;
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
    public Long applyPaymentToOrder(PaymentResponseDTO responseDTO, PaymentGatewayConfigurationService configService) {
        
        //Payments can ONLY be parsed into PaymentInfos if they are 'valid'
        if (!responseDTO.getValid()) {
            throw new IllegalArgumentException("Invalid payment responses cannot be parsed into the order payment domain");
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
        
        // ALWAYS create a new order payment for the payment that comes in. Invalid payments should be cleaned up by
        // invoking {@link #markPaymentaAsInvalid}.
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
        
        //TODO: handle payments that have to be confirmed. Scenario:
        /*
         * 1. User goes through checkout
         * 2. User submits payment to gateway which supports a 'confirmation
         * 3. User is on review order page
         * 4. User goes back and makes modifications to their cart
         * 5. The user now has an order payment in the system which has been unconfirmed and is really in this weird, invalid
         *    state.
         * 6. 
         */
        
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
        // TODO delete (which archives) the given payment id
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
