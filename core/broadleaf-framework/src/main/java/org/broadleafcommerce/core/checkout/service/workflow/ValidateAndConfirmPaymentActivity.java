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
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.payment.service.PaymentGatewayCheckoutService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfigurationServiceProvider;
import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.checkout.service.strategy.OrderPaymentConfirmationStrategy;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.payment.service.OrderPaymentService;
import org.broadleafcommerce.core.payment.service.OrderPaymentStatusService;
import org.broadleafcommerce.core.payment.service.OrderToPaymentRequestDTOService;
import org.broadleafcommerce.core.payment.service.type.OrderPaymentStatus;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.state.ActivityStateManagerImpl;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;


/**
 * <p>This activity is responsible for validating and processing several aspects of an order's payment so that
 * it may successfully complete the checkout workflow. This activity will:
 *
 * <ul>
 * <li>Verify that there is enough payment on the order via the <i>successful</i> amount on {@link PaymentTransactionType.AUTHORIZE} and
 * {@link PaymentTransactionType.AUTHORIZE_AND_CAPTURE} and {@link PaymentTransactionType.PENDING} transactions.</li>
 *
 * <li>"Confirm" any {@link PaymentTransactionType.UNCONFIRMED} transactions that exist on an {@link OrderPayment}. This can
 * mean different things depending on the type of Order Payment and is handled by the {@link org.broadleafcommerce.core.checkout.service.strategy.OrderPaymentConfirmationStrategy}</li>
 * <li>If there is an exception (either in this activity or later downstream) the confirmed payments are rolled back via
 * {@link org.broadleafcommerce.core.checkout.service.workflow.ConfirmPaymentsRollbackHandler}. It will also by default
 * attempt to mark the payment as "ARCHIVED" so that the user may attempt to re-enter their payment details.</li>
 * </ul>
 *
 * </p>
 *
 * @author Phillip Verheyden (phillipuniverse)
 * @author Elbert Bautista (elbertbautista)
 */
public class ValidateAndConfirmPaymentActivity extends BaseActivity<ProcessContext<CheckoutSeed>> {
    
    protected static final Log LOG = LogFactory.getLog(ValidateAndConfirmPaymentActivity.class);
    
    /**
     * <p>
     * Used by the {@link org.broadleafcommerce.core.checkout.service.workflow.ConfirmPaymentsRollbackHandler}
     * to roll back transactions that this activity confirms.
     * 
     * <p>
     * This could also contain failed transactions that still need to be rolled back
     */
    public static final String ROLLBACK_TRANSACTIONS = "confirmedTransactions";

    public static final String FAILED_RESPONSES = "failedResponses";
    

    @Autowired(required = false)
    @Qualifier("blPaymentGatewayConfigurationServiceProvider")
    protected PaymentGatewayConfigurationServiceProvider paymentConfigurationServiceProvider;
    
    @Resource(name = "blOrderToPaymentRequestDTOService")
    protected OrderToPaymentRequestDTOService orderToPaymentRequestService;

    @Resource(name = "blOrderPaymentService")
    protected OrderPaymentService orderPaymentService;

    @Resource(name = "blPaymentGatewayCheckoutService")
    protected PaymentGatewayCheckoutService paymentGatewayCheckoutService;

    @Resource(name = "blOrderPaymentConfirmationStrategy")
    protected OrderPaymentConfirmationStrategy orderPaymentConfirmationStrategy;

    @Resource(name = "blOrderPaymentStatusService")
    protected OrderPaymentStatusService orderPaymentStatusService;

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
        
        /**
         * This list contains the additional transactions that were created to confirm previously unconfirmed transactions
         * which can occur if you send credit card data directly to Broadleaf and rely on this activity to confirm
         * that transaction
         */
        Map<OrderPayment, PaymentTransaction> additionalTransactions = new HashMap<OrderPayment, PaymentTransaction>();
        List<ResponseTransactionPair> failedTransactions = new ArrayList<ResponseTransactionPair>();
        // Used for the rollback handler; we want to make sure that we roll back transactions that have already been confirmed
        // as well as transactions that we are about to confirm here
        List<PaymentTransaction> confirmedTransactions = new ArrayList<PaymentTransaction>();
        /**
         * This is a subset of the additionalTransactions that contains the transactions that were confirmed in this activity
         */
        Map<OrderPayment, PaymentTransactionType> additionalConfirmedTransactions = new HashMap<OrderPayment, PaymentTransactionType>();

        for (OrderPayment payment : order.getPayments()) {
            if (payment.isActive()) {
                for (PaymentTransaction tx : payment.getTransactions()) {
                    if (OrderPaymentStatus.UNCONFIRMED.equals(orderPaymentStatusService.determineOrderPaymentStatus(payment)) &&
                            PaymentTransactionType.UNCONFIRMED.equals(tx.getType())) {
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("Transaction " + tx.getId() + " is not confirmed. Proceeding to confirm transaction.");
                        }

                        PaymentResponseDTO responseDTO = orderPaymentConfirmationStrategy.confirmTransaction(tx, context);

                        if (responseDTO == null) {
                            String msg = "Unable to 'confirm' the UNCONFIRMED Transaction with id: " + tx.getId() + ". " +
                                    "The ResponseDTO null. Please check your order payment" +
                                    "confirmation strategy implementation";
                            LOG.error(msg);
                            throw new CheckoutException(msg, context.getSeedData());
                        }

                        if (LOG.isTraceEnabled()) {
                            LOG.trace("Transaction Confirmation Raw Response: " +  responseDTO.getRawResponse());
                        }

                        if (responseDTO.getAmount() == null || responseDTO.getPaymentTransactionType() == null) {
                            //Log an error, an exception will get thrown later as the payments won't add up.
                            LOG.error("The ResponseDTO returned from the Gateway does not contain either an Amount or Payment Transaction Type. " +
                                    "Please check your implementation");
                        }

                        // Create a new transaction that references its parent UNCONFIRMED transaction.
                        PaymentTransaction transaction = orderPaymentService.createTransaction();
                        transaction.setAmount(responseDTO.getAmount());
                        transaction.setRawResponse(responseDTO.getRawResponse());
                        transaction.setSuccess(responseDTO.isSuccessful());
                        transaction.setType(responseDTO.getPaymentTransactionType());
                        transaction.setParentTransaction(tx);
                        transaction.setOrderPayment(payment);
                        transaction.setAdditionalFields(responseDTO.getResponseMap());
                        transaction = orderPaymentService.save(transaction);
                        additionalTransactions.put(payment, transaction);

                        if (responseDTO.isSuccessful()) {
                            //if response is successful, attempt to create a customer payment token
                            createCustomerPaymentToken(transaction);
                            additionalConfirmedTransactions.put(payment, transaction.getType());
                        } else {
                            failedTransactions.add(new ResponseTransactionPair(responseDTO, transaction.getId()));
                        }

                    } else if (PaymentTransactionType.AUTHORIZE.equals(tx.getType()) ||
                            PaymentTransactionType.AUTHORIZE_AND_CAPTURE.equals(tx.getType())) {
                        // attempt to create a customer payment token if payment is marked as tokenized
                        createCustomerPaymentToken(tx);
                        // After each transaction is confirmed, associate the new list of confirmed transactions to the rollback state. This has the added
                        // advantage of being able to invoke the rollback handler if there is an exception thrown at some point while confirming multiple
                        // transactions. This is outside of the transaction confirmation block in order to capture transactions
                        // that were already confirmed prior to this activity running
                        confirmedTransactions.add(tx);
                    }
                }
            }
        }
        
        // Add the new transactions to this payment (failed and confirmed) These need to be saved on the order payment
        // regardless of an error in the workflow later.
        for (OrderPayment payment : order.getPayments()) {
            if (additionalTransactions.containsKey(payment)) {
                PaymentTransactionType confirmedType = null;
                if (additionalConfirmedTransactions.containsKey(payment)) {
                    confirmedType = additionalConfirmedTransactions.get(payment);
                }

                payment.addTransaction(additionalTransactions.get(payment));
                payment = orderPaymentService.save(payment);

                if (confirmedType != null) {
                    List<PaymentTransaction> types = payment.getTransactionsForType(confirmedType);
                    if (types.size() == 1) {
                        confirmedTransactions.add(types.get(0));
                    } else {
                        throw new IllegalArgumentException("There should only be one AUTHORIZE or AUTHORIZE_AND_CAPTURE transaction." +
                                "There are more than one confirmed payment transactions for Order Payment:" + payment.getId() );
                    }
                }
            }
        }

        // Once all transactions have been confirmed, add them to the rollback state.
        // If an exception is thrown after this, the confirmed transactions will need to be voided or reversed
        // (based on the implementation requirements of the Gateway)
        rollbackState.put(ROLLBACK_TRANSACTIONS, confirmedTransactions);
        ActivityStateManagerImpl.getStateManager().registerState(this, context, getRollbackHandler(), rollbackState);

        //Handle the failed transactions (default implementation is to throw a new CheckoutException)
        if (!failedTransactions.isEmpty()) {
            handleUnsuccessfulTransactions(failedTransactions, context);
        }

        // Add authorize and authorize_and_capture transactions;
        // there should only be one or the other in the payment
        // Also add any pending transactions (as these are marked as being AUTH or CAPTURED later)
        Money paymentSum = new Money(BigDecimal.ZERO);
        for (OrderPayment payment : order.getPayments()) {
            if (payment.isActive()) {
                paymentSum = paymentSum.add(payment.getSuccessfulTransactionAmountForType(PaymentTransactionType.AUTHORIZE))
                               .add(payment.getSuccessfulTransactionAmountForType(PaymentTransactionType.AUTHORIZE_AND_CAPTURE))
                               .add(payment.getSuccessfulTransactionAmountForType(PaymentTransactionType.PENDING));
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

    /**
     * <p>
     * Default implementation is to throw a generic CheckoutException which will be caught and displayed
     * on the Checkout Page where the Customer can try again. In many cases, this is
     * sufficient as it is usually recommended to display a generic Error Message to prevent
     * Credit Card fraud.
     *
     * <p>
     * The configured payment gateway may return a more specific error.
     * Each gateway is different and will often times return different error codes based on the acquiring bank as well.
     * In that case, you may override this method to decipher these errors
     * and handle it appropriately based on your business requirements.
     *
     */
    protected void handleUnsuccessfulTransactions(List<ResponseTransactionPair> failedTransactions, ProcessContext<CheckoutSeed> context) throws Exception {
        //The Response DTO was not successful confirming/authorizing a transaction.
        String msg = "Attempting to confirm/authorize an UNCONFIRMED transaction on the order was unsuccessful.";
        
        
        /**
         * For each of the failed transactions we might need to register state with the rollback handler
         */
        List<OrderPayment> invalidatedPayments = new ArrayList<OrderPayment>();
        List<PaymentTransaction> failedTransactionsToRollBack = new ArrayList<PaymentTransaction>();
        List<PaymentResponseDTO> failedResponses = new ArrayList<PaymentResponseDTO>();
        for (ResponseTransactionPair responseTransactionPair : failedTransactions) {
            PaymentTransaction tx = orderPaymentService.readTransactionById(responseTransactionPair.getTransactionId());
            if (shouldRollbackFailedTransaction(responseTransactionPair)) {
                failedTransactionsToRollBack.add(tx);
            } else if (!invalidatedPayments.contains(tx.getOrderPayment())) {
                paymentGatewayCheckoutService.markPaymentAsInvalid(tx.getOrderPayment().getId());
                OrderPayment payment = orderPaymentService.save(tx.getOrderPayment());
                invalidatedPayments.add(payment);
            }
            failedResponses.add(responseTransactionPair.getResponseDTO());
        }
        
        /**
         * Even though the original transaction confirmation failed, there is still a possibility that we need to rollback
         * the failure. The use case is in the case of fraud checks, some payment gateways complete the AUTHORIZE prior to
         * executing the fraud check. Thus, the AUTHORIZE technically fails because of fraud but the user's card was still
         * charged. This handles the case of rolling back the AUTHORIZE transaction in that case
         */
        Map<String, Object> rollbackState = new HashMap<String, Object>(); 
        rollbackState.put(ROLLBACK_TRANSACTIONS, failedTransactionsToRollBack);
        context.getSeedData().getUserDefinedFields().put(FAILED_RESPONSES, failedResponses);
        ActivityStateManagerImpl.getStateManager().registerState(this, context, getRollbackHandler(), rollbackState);
        
        if (LOG.isErrorEnabled()) {
            LOG.error(msg);
        }

        if (LOG.isTraceEnabled()) {
            for (ResponseTransactionPair responseTransactionPair : failedTransactions) {
                LOG.trace(responseTransactionPair.getResponseDTO().getRawResponse());
            }
        }
        
        throw new CheckoutException(msg, context.getSeedData());
    }
    
    protected boolean shouldRollbackFailedTransaction(ResponseTransactionPair failedTransactionPair) {
        return false;
    }

    protected CustomerPayment createCustomerPaymentToken(PaymentTransaction transaction) {
        if (transaction.isSaveToken()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(String.format("Attempting to create a customer payment for Order Payment - (%s)", transaction.getId()));
            }
            return orderPaymentService.createCustomerPaymentFromPaymentTransaction(transaction);
        }

        return null;
    }
    
    protected class ResponseTransactionPair {
        PaymentResponseDTO responseDTO;
        Long transactionId;
        
        ResponseTransactionPair() {
            this(null, null);
        }
        ResponseTransactionPair(PaymentResponseDTO responseDTO, Long transactionId) {
            this.responseDTO = responseDTO;
            this.transactionId = transactionId;
        }
        
        public PaymentResponseDTO getResponseDTO() {
            return responseDTO;
        }
        
        public Long getTransactionId() {
            return transactionId;
        }
    }

}
