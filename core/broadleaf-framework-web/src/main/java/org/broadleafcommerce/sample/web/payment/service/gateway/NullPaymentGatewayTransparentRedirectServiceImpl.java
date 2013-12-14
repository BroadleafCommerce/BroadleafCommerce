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

package org.broadleafcommerce.sample.web.payment.service.gateway;

import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.common.payment.dto.AddressDTO;
import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.payment.service.PaymentGatewayTransparentRedirectService;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;
import org.broadleafcommerce.sample.web.vendor.nullPaymentGateway.service.payment.NullPaymentGatewayConstants;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * This is an example implementation of a {@link PaymentGatewayTransparentRedirectService}.
 * This is just a sample that mimics what hidden fields a real payment gateway implementation
 * might put on your transparent redirect credit card form on your checkout page.
 * Replace with a real Payment Gateway Integration like Braintree or PayPal PayFlow.
 *
 * In order to use load this demo service, you will need to component scan
 * the package "org.broadleafcommerce.sample.web".
 *
 * This should NOT be used in production, and is meant solely for demonstration
 * purposes only.
 *
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blNullPaymentGatewayTransparentRedirectService")
public class NullPaymentGatewayTransparentRedirectServiceImpl implements PaymentGatewayTransparentRedirectService {

    @Resource(name = "blNullPaymentGatewayConfigurationService")
    protected NullPaymentGatewayConfigurationService configurationService;

    @Override
    public PaymentResponseDTO createAuthorizeForm(PaymentRequestDTO requestDTO) throws PaymentException {
        return createCommonTRFields(requestDTO);
    }

    @Override
    public PaymentResponseDTO createAuthorizeAndCaptureForm(PaymentRequestDTO requestDTO) throws PaymentException {
        return createCommonTRFields(requestDTO);
    }

    protected PaymentResponseDTO createCommonTRFields(PaymentRequestDTO requestDTO) {
        Assert.isTrue(requestDTO.getTransactionTotal() != null,
                "The Transaction Total on the Payment Request DTO must not be null");
        Assert.isTrue(requestDTO.getOrderId() != null,
                "The Order ID on the Payment Request DTO must not be null");

        //Put The shipping, billing, and transaction amount fields as hidden fields on the form
        //In a real implementation, the gateway will probably provide some API to tokenize this information
        //which you can then put on your form as a secure token. For this sample,
        // we will just place them as plain-text hidden fields on the form
        PaymentResponseDTO responseDTO = new PaymentResponseDTO(PaymentType.CREDIT_CARD)
                .responseMap(NullPaymentGatewayConstants.ORDER_ID, requestDTO.getOrderId())
                .responseMap(NullPaymentGatewayConstants.TRANSACTION_AMT, requestDTO.getTransactionTotal())
                .responseMap(NullPaymentGatewayConstants.TRANSPARENT_REDIRECT_URL,
                        configurationService.getTransparentRedirectUrl());

        AddressDTO billTo = requestDTO.getBillTo();
        if (billTo != null)  {
            responseDTO.responseMap(NullPaymentGatewayConstants.BILLING_FIRST_NAME, billTo.getAddressFirstName())
                    .responseMap(NullPaymentGatewayConstants.BILLING_LAST_NAME, billTo.getAddressLastName())
                    .responseMap(NullPaymentGatewayConstants.BILLING_ADDRESS_LINE1, billTo.getAddressLine1())
                    .responseMap(NullPaymentGatewayConstants.BILLING_ADDRESS_LINE2, billTo.getAddressLine2())
                    .responseMap(NullPaymentGatewayConstants.BILLING_CITY, billTo.getAddressCityLocality())
                    .responseMap(NullPaymentGatewayConstants.BILLING_STATE, billTo.getAddressStateRegion())
                    .responseMap(NullPaymentGatewayConstants.BILLING_ZIP, billTo.getAddressPostalCode());
        }

        AddressDTO shipTo = requestDTO.getShipTo();
        if (shipTo != null) {
            responseDTO.responseMap(NullPaymentGatewayConstants.SHIPPING_FIRST_NAME, shipTo.getAddressFirstName())
                    .responseMap(NullPaymentGatewayConstants.SHIPPING_LAST_NAME, shipTo.getAddressLastName())
                    .responseMap(NullPaymentGatewayConstants.SHIPPING_ADDRESS_LINE1, shipTo.getAddressLine1())
                    .responseMap(NullPaymentGatewayConstants.SHIPPING_ADDRESS_LINE2, shipTo.getAddressLine2())
                    .responseMap(NullPaymentGatewayConstants.SHIPPING_CITY, shipTo.getAddressCityLocality())
                    .responseMap(NullPaymentGatewayConstants.SHIPPING_STATE, shipTo.getAddressStateRegion())
                    .responseMap(NullPaymentGatewayConstants.SHIPPING_ZIP, shipTo.getAddressPostalCode());
        }

        return responseDTO;

    }

}
