/*
 * #%L
 * BroadleafCommerce Framework
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
