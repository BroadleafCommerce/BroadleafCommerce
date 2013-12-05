/*
 * #%L
 * BroadleafCommerce Common Libraries
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

package org.broadleafcommerce.common.payment.service;

import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;

/**
 * @author Elbert Bautista (elbertbautista)
 *
 * Certain Payment Integrations allow you to use Fraud Services like Address Verification and Buyer Authentication,
 * such as PayPal Payments Pro (PayFlow Edition)
 *
 * This API allows you to call certain fraud prevention APIs exposed from the gateway.
 *
 */
public interface PaymentGatewayFraudService {

    /**
     * Certain Gateways integrate with Visa’s Verified by Visa and MasterCard’s SecureCode API
     * If the buyer is enrolled in such a service, we will need to redirect the buyer's browser
     * to the ACS ( Access Control Server, eg. users' bank) for verification.
     * See: http://en.wikipedia.org/wiki/3-D_Secure
     *
     * This method is intended to retrieve a URL to the ACS from the gateway.
     *
     * @param paymentRequestDTO
     * @return
     */
    public PaymentResponseDTO requestPayerAuthentication(PaymentRequestDTO paymentRequestDTO);

}
