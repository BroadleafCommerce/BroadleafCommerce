/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
