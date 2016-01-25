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
package org.broadleafcommerce.core.checkout.service.strategy;

import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;
import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutSeed;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.WorkflowException;

/**
 * Implementation to "confirm" an unconfirmed transaction.
 *
 * Default implementation is to:
 * - If it is an unconfirmed {@link org.broadleafcommerce.common.payment.PaymentType#CREDIT_CARD},
 * then it will attempt to either "Authorize" or "Authorize and Capture" it at this time.
 * - If the transaction is of any other type, it will attempt to call the implementing gateway's
 * {@link org.broadleafcommerce.common.payment.service.PaymentGatewayTransactionConfirmationService#confirmTransaction(org.broadleafcommerce.common.payment.dto.PaymentRequestDTO)}
 * - If the system is configured to handle PENDING payments during a checkout, it will create that in the interim.
 *
 * @author Elbert Bautista (elbertbautista)
 */
public interface OrderPaymentConfirmationStrategy {

    /**
     * Strategy to determine how to "confirm" an OrderPayment at checkout
     */
    public PaymentResponseDTO confirmTransaction(PaymentTransaction tx, ProcessContext<CheckoutSeed> context) throws PaymentException, WorkflowException, CheckoutException;

    /**
     * Strategy to determine how to "confirm" a PENDING OrderPayment post-checkout
     */
    public PaymentResponseDTO confirmPendingTransaction(PaymentTransaction tx, ProcessContext<CheckoutSeed> context) throws PaymentException, WorkflowException, CheckoutException;
}
