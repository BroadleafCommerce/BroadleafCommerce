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
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;

/**
 * <p>This is a decoupled interface that provides
 * the basic functions needed to create the normal BILLABLE Credit Card Transactions</p>
 *
 * <p>The intention of these method implementations are to make a Server to Server API call.
 * Depending on the Gateway implementation, the overall goal and meaning of the method may vary:
 * For example, a module can implement the AUTHORIZE method:
 * <ul>
 * <li>Either to send Credit Card information directly (Server to Server) to the gateway to perform the transaction</li>
 * <li>Or to confirm an AUTHORIZATION process (some gateways dont handle a token based process through a Transparent Redirect)</li>
 * <li>OR handle both (the implementation will do one or the other based on the passed in parameters)</li>
 * </ul>
 * </p>
 *
 * <p>Please check the documentation of the implementing module to determine intended goal.</p>
 *
 * <p>Note: in the case where a gateway doesn't support confirming the transaction before it is submitted
 * (i.e. paymentGatewayConfigurationService.completeCheckoutOnCallback() == true)
 * The PaymentGatewayWebResponseService will handle translation of the final transaction response from the gateway.
 * There is no need to re-call this service if the gateway doesn't support confirming the transaction.</p>
 *
 * @see {@link PaymentGatewayWebResponseService}
 *
 * @author Elbert Bautista (elbertbautista)
 */
public interface PaymentGatewayTransactionService {

    public PaymentResponseDTO authorize(PaymentRequestDTO paymentRequestDTO) throws PaymentException;

    public PaymentResponseDTO capture(PaymentRequestDTO paymentRequestDTO) throws PaymentException ;

    public PaymentResponseDTO authorizeAndCapture(PaymentRequestDTO paymentRequestDTO) throws PaymentException ;

    public PaymentResponseDTO reverseAuthorize(PaymentRequestDTO paymentRequestDTO) throws PaymentException;

    public PaymentResponseDTO refund(PaymentRequestDTO paymentRequestDTO) throws PaymentException;

    public PaymentResponseDTO voidPayment(PaymentRequestDTO paymentRequestDTO) throws PaymentException;

}
