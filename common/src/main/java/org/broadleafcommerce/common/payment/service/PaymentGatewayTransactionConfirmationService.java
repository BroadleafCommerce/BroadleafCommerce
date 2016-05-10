/*
 * #%L
 * BroadleafCommerce Common Libraries
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
 * PaymentGatewayConfiguration.completeCheckoutOnCallback(). If this value is set to true,
 * then the gateway does not support confirming the transaction, as it assumes to be the final step
 * in the completion process. Most Credit Card integrations do not support confirming the transaction,
 * Third Party providers like PayPal Express, or the BLC Gift Card Module do and should implement
 * this interface.
 * </p>
 *
 * @see {@link PaymentGatewayRollbackService}
 * @see {@link PaymentGatewayConfiguration}
 *
 * @author Elbert Bautista (elbertbautista)
 */
public interface PaymentGatewayTransactionConfirmationService {

    public PaymentResponseDTO confirmTransaction(PaymentRequestDTO paymentRequestDTO) throws PaymentException;

}
