/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.common.payment.service;

import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;

public class AbstractPaymentGatewayTransparentRedirectService implements PaymentGatewayTransparentRedirectService {

    @Override
    public PaymentResponseDTO createAuthorizeForm(PaymentRequestDTO requestDTO) throws PaymentException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public PaymentResponseDTO createAuthorizeAndCaptureForm(PaymentRequestDTO requestDTO) throws PaymentException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public PaymentResponseDTO createCustomerPaymentTokenForm(PaymentRequestDTO requestDTO) throws PaymentException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public PaymentResponseDTO updateCustomerPaymentTokenForm(PaymentRequestDTO requestDTO) throws PaymentException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public String getCreateCustomerPaymentTokenReturnURLFieldKey(PaymentResponseDTO responseDTO) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public String getCreateCustomerPaymentTokenCancelURLFieldKey(PaymentResponseDTO responseDTO) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public String getUpdateCustomerPaymentTokenReturnURLFieldKey(PaymentResponseDTO responseDTO) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public String getUpdateCustomerPaymentTokenCancelURLFieldKey(PaymentResponseDTO responseDTO) {
        throw new UnsupportedOperationException("Not Implemented");
    }

}
