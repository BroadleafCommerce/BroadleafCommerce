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
