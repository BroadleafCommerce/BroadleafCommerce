/*
 * #%L
 * BroadleafCommerce Framework
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

package org.broadleafcommerce.core.checkout.service.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfiguration;
import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.payment.service.OrderPaymentService;
import org.broadleafcommerce.core.payment.service.OrderToPaymentRequestDTOService;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


/**
 * Verifies that there is enough payment on the order and confirms all payments that have not already been confirmed.
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public class ValidateAndConfirmPaymentActivity extends BaseActivity<ProcessContext<CheckoutSeed>> {
    
    protected static final Log LOG = LogFactory.getLog(ValidateAndConfirmPaymentActivity.class);

    @Autowired(required = false)
    @Qualifier("blPaymentGatewayConfiguration")
    protected PaymentGatewayConfiguration paymentGatewayConfiguration;
    
    @Resource(name = "blOrderToPaymentRequestDTOService")
    protected OrderToPaymentRequestDTOService orderToPaymentRequestService;

    @Resource(name = "blOrderPaymentService")
    protected OrderPaymentService orderPaymentService;

    @Override
    public ProcessContext<CheckoutSeed> execute(ProcessContext<CheckoutSeed> context) throws Exception {
        Order order = context.getSeedData().getOrder();
        
        // There are definitely enough payments on the order. We now need to confirm each unconfirmed payment on the order.
        // Unconfirmed payments could be added for things like gift cards and account credits; they are not actually
        // decremented from the user's account until checkout. This could also be used in some credit card processing
        // situations
        // Important: The payment.getAmount() must be the final amount that is going to be confirmed. If the order total
        // changed, the order payments need to be adjusted to reflect this and must add up to the order total.
        // This can happen in the case of PayPal Express or other hosted gateways where the unconfirmed payment comes back
        // to a review page, the customer selects shipping and the order total is adjusted.
        Map<OrderPayment, PaymentTransaction> additionalTransactions = new HashMap<OrderPayment, PaymentTransaction>();
        for (OrderPayment payment : order.getPayments()) {
            for (PaymentTransaction tx : payment.getTransactions()) {
                if (!tx.getConfirmed()) {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Transaction is not confirmed. Proceeding to confirm transaction.");
                    }

                    if (paymentGatewayConfiguration == null || paymentGatewayConfiguration.getTransactionConfirmationService() == null) {
                        String msg = "There are unconfirmed payment transactions on this payment but no payment gateway" +
                                " configuration or transaction confirmation service configured";
                        LOG.error(msg);
                        throw new CheckoutException(msg, context.getSeedData());
                    } else {
                        PaymentResponseDTO responseDTO = paymentGatewayConfiguration.getTransactionConfirmationService()
                            .confirmTransaction(orderToPaymentRequestService.translatePaymentTransaction(payment.getAmount(), tx));

                        if (LOG.isTraceEnabled()) {
                            LOG.trace("Transaction Confirmation Raw Response: " +  responseDTO.getRawResponse());
                        }

                        if (responseDTO.isSuccessful()) {
                            PaymentTransaction transaction = orderPaymentService.createTransaction();
                            transaction.setAmount(responseDTO.getAmount());
                            transaction.setRawResponse(responseDTO.getRawResponse());
                            transaction.setSuccess(responseDTO.isSuccessful());
                            transaction.setType(responseDTO.getPaymentTransactionType());
                            transaction.setParentTransaction(tx);
                            transaction.setOrderPayment(payment);
                            additionalTransactions.put(payment, transaction);
                        } else {
                            String msg = "Unable to Confirm Transaction with id: " + tx.getId();
                            LOG.error(msg);
                            throw new CheckoutException(msg, context.getSeedData());
                        }
                    }
                }
            }
        }

        for (OrderPayment payment : order.getPayments()) {
            if (additionalTransactions.containsKey(payment)) {
                payment.addTransaction(additionalTransactions.get(payment));
            }
        }
        
        // Add authorize and authorize_and_capture; there should only be one or the other in the payment
        Money paymentSum = new Money(BigDecimal.ZERO);
        for (OrderPayment payment : order.getPayments()) {
            if (PaymentType.THIRD_PARTY_ACCOUNT.equals(payment.getType())) {
                paymentSum = paymentSum.add(payment.getSuccessfulTransactionAmountForType(PaymentTransactionType.CONFIRMED));
            } else {
                paymentSum = paymentSum.add(payment.getSuccessfulTransactionAmountForType(PaymentTransactionType.AUTHORIZE))
                                   .add(payment.getSuccessfulTransactionAmountForType(PaymentTransactionType.AUTHORIZE_AND_CAPTURE));
            }
        }
        
        if (paymentSum.lessThan(order.getTotal())) {
            throw new IllegalArgumentException("There are not enough payments to pay for the total order. The sum of " + 
                    "the payments is " + paymentSum.getAmount().toPlainString() + " and the order total is " + order.getTotal().getAmount().toPlainString());
        }

        
        // There should also likely be something that says whether the payment was successful or not and this should check
        // that as well. Currently there isn't really a concept for that
        return context;
    }

}
