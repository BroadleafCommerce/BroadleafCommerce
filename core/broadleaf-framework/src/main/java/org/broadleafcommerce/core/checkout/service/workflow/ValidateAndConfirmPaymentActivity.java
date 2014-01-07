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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfigurationService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfigurationServiceProvider;
import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.payment.service.OrderPaymentService;
import org.broadleafcommerce.core.payment.service.OrderToPaymentRequestDTOService;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.state.ActivityStateManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


/**
 * <p>Verifies that there is enough payment on the order via the <i>successful</i> amount on {@link PaymentTransactionType.AUTHORIZE} and
 * {@link PaymentTransactionType.AUTHORIZE_AND_CAPTURE} transactions. This will also confirm any {@link PaymentTransactionType.UNCONFIRMED} transactions
 * that exist on am {@link OrderPayment}.</p>
 * 
 * <p>If there is an exception (either in this activity or later downstream) the confirmed payments are rolled back via {@link ConfirmPaymentsRollbackHandler}
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public class ValidateAndConfirmPaymentActivity extends BaseActivity<ProcessContext<CheckoutSeed>> {
    
    protected static final Log LOG = LogFactory.getLog(ValidateAndConfirmPaymentActivity.class);
    
    /**
     * Used by the {@link ConfirmPaymentsRollbackHandler} to roll back transactions that this activity confirms.
     */
    public static final String CONFIRMED_TRANSACTIONS = "confirmedTransactions";

    @Autowired(required = false)
    @Qualifier("blPaymentGatewayConfigurationServiceProvider")
    protected PaymentGatewayConfigurationServiceProvider paymentConfigurationServiceProvider;
    
    @Resource(name = "blOrderToPaymentRequestDTOService")
    protected OrderToPaymentRequestDTOService orderToPaymentRequestService;

    @Resource(name = "blOrderPaymentService")
    protected OrderPaymentService orderPaymentService;

    @Override
    public ProcessContext<CheckoutSeed> execute(ProcessContext<CheckoutSeed> context) throws Exception {
        Order order = context.getSeedData().getOrder();
        
        Map<String, Object> rollbackState = new HashMap<String, Object>(); 
        
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
                if (PaymentTransactionType.UNCONFIRMED.equals(tx.getType())) {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Transaction " + tx.getId() + " is not confirmed. Proceeding to confirm transaction.");
                    }
                    
                    // Cannot confirm anything here if there is no provider
                    if (paymentConfigurationServiceProvider == null) {
                        String msg = "There are unconfirmed payment transactions on this payment but no payment gateway" +
                                " configuration or transaction confirmation service configured";
                        LOG.error(msg);
                        throw new CheckoutException(msg, context.getSeedData());
                    }
                    
                    PaymentGatewayConfigurationService cfg = paymentConfigurationServiceProvider.getGatewayConfigurationService(tx.getOrderPayment().getGatewayType());
                    PaymentResponseDTO responseDTO = cfg.getTransactionConfirmationService()
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
                        // Since there was a problems processing the 
                        String msg = "Transaction confirmation attempt with id: " + tx.getId() + " was unsuccessful";
                        LOG.error(msg);
                        throw new CheckoutException(msg, context.getSeedData());
                    }
                    
                    // After each transaction is confirmed, associate the new list of confirmed transactions to the rollback state. This has the added
                    // advantage of being able to invoke the rollback handler if there is an exception thrown at some point while confirming multiple
                    // transactions
                    rollbackState.put(CONFIRMED_TRANSACTIONS, additionalTransactions.values());
                    ActivityStateManagerImpl.getStateManager().registerState(this, context, getRollbackHandler(), rollbackState);
                }
            }
        }

        // Add the new transactions to this payment
        for (OrderPayment payment : order.getPayments()) {
            if (additionalTransactions.containsKey(payment)) {
                payment.addTransaction(additionalTransactions.get(payment));
            }
        }
        
        // Add authorize and authorize_and_capture; there should only be one or the other in the payment
        Money paymentSum = new Money(BigDecimal.ZERO);
        for (OrderPayment payment : order.getPayments()) {
            paymentSum = paymentSum.add(payment.getSuccessfulTransactionAmountForType(PaymentTransactionType.AUTHORIZE))
                               .add(payment.getSuccessfulTransactionAmountForType(PaymentTransactionType.AUTHORIZE_AND_CAPTURE));
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
