/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.payment.service;

import org.broadleafcommerce.common.payment.dto.AddressDTO;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public interface PaymentResponseDTOToEntityService {

    public void populateBillingInfo(PaymentResponseDTO responseDTO, OrderPayment payment, Address tempBillingAddress, boolean isUseBillingAddressFromGateway);

    public void populateShippingInfo(PaymentResponseDTO responseDTO, Order order);

    public void populateAddressInfo(AddressDTO<PaymentResponseDTO> dto, Address address);

    /**
     * <p>
     * Will attempt to populate the {@link org.broadleafcommerce.profile.core.domain.CustomerPayment#setPaymentToken(String)}
     * by first looking at the response map for key {@link org.broadleafcommerce.common.payment.PaymentAdditionalFieldType#TOKEN}.
     * If not found, it will next look and see if a Credit Card is populated on the response and will attempt to get the
     * {@link org.broadleafcommerce.common.payment.dto.CreditCardDTO#getCreditCardNum()}
     *
     * <p>
     * Usually used during a tokenization flow when there is a direct response from the gateway (e.g. transparent redirect)
     * outside the scope of a checkout flow.
     * @param customerPayment
     * @param responseDTO
     * @see {@link org.broadleafcommerce.core.payment.service.DefaultPaymentGatewayCheckoutService}
     */
    public void populateCustomerPaymentToken(PaymentResponseDTO responseDTO, CustomerPayment customerPayment);

}
