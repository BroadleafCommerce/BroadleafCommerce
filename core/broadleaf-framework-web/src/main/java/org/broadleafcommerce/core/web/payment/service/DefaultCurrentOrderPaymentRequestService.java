/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
/**
 * 
 */
package org.broadleafcommerce.core.web.payment.service;

import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.payment.service.CurrentOrderPaymentRequestService;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.service.OrderToPaymentRequestDTOService;
import org.broadleafcommerce.core.web.order.CartState;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
@Service("blDefaultCurrentPaymentRequestService")
public class DefaultCurrentOrderPaymentRequestService implements CurrentOrderPaymentRequestService {

    @Resource
    protected OrderToPaymentRequestDTOService paymentRequestDTOService;
    
    @Override
    public PaymentRequestDTO getPaymentRequestFromCurrentOrder() {
        Order currentCart = CartState.getCart();
        PaymentRequestDTO request = paymentRequestDTOService.translateOrder(currentCart);
        return request;
    }

}
