/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.payment.service;

import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;

/**
 * <p>
 * This API is intended to be called by the Checkout Workflow
 * to confirm all Payments on the order that have not yet been confirmed/finalized.
 * In the case where an error is thrown by the gateway and confirming is not possible,
 * the workflow should invoke the rollback handlers on any Payments that have already been
 * successfully confirmed.
 * </p>
 *
 * <p>
 * Not all Gateways allow confirmation. That setting can be found on the
 * PaymentGatewayConfigurationService.completeCheckoutOnCallback(). If this value is set to true,
 * then the gateway does not support confirming the transaction, as it assumes to be the final step
 * in the completion process. Most Credit Card integrations do not support confirming the transaction,
 * Third Party providers like PayPal Express, or the BLC Gift Card Module do and should implement
 * this interface.
 * </p>
 *
 * @see {@link PaymentGatewayRollbackService}
 * @see {@link PaymentGatewayConfigurationService}
 *
 * @author Elbert Bautista (elbertbautista)
 */
public interface PaymentGatewayTransactionConfirmationService {

    public PaymentResponseDTO confirmTransaction(PaymentRequestDTO paymentRequestDTO) throws PaymentException;

}
