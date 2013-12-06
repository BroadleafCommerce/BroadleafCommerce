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

import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.payment.service.PaymentGatewayCheckoutService;
import org.broadleafcommerce.common.web.payment.controller.PaymentGatewayAbstractController;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
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
    
    @Override
    public Long applyPaymentToOrder(PaymentResponseDTO responseDTO) {
        Long orderId = Long.parseLong(responseDTO.getOrderId());
        Order order = orderService.findOrderById(orderId);
        
        //TODO: do not apply payments to orders that have already been checked out
        
        List<OrderPayment> payments = order.getPayments();
        
        //TODO: ensure that a payment does not actually get deleted
        //TODO: eventually, this should not delete all the payments from an order for the given type
        //TODO: fill out order.getCustomer() values for anonymous customers based on values returned from the response
        
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
