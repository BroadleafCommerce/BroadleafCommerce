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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfigurationService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfigurationServiceProvider;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.payment.domain.secure.CreditCardPayment;
import org.broadleafcommerce.core.payment.service.OrderPaymentService;
import org.broadleafcommerce.core.payment.service.OrderToPaymentRequestDTOService;
import org.broadleafcommerce.core.payment.service.SecureOrderPaymentService;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.WorkflowException;
import org.broadleafcommerce.core.workflow.state.ActivityStateManagerImpl;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


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

    @Resource(name = "blSecureOrderPaymentService")
    protected SecureOrderPaymentService secureOrderPaymentService;

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
         * which can occur if you send credit card data directly to Broadlaef and rely on this activity to confirm
         * that transaction
         */
        Map<OrderPayment, PaymentTransaction> additionalTransactions = new HashMap<OrderPayment, PaymentTransaction>();
        List<PaymentResponseDTO> failedTransactions = new ArrayList<PaymentResponseDTO>();
        // Used for the rollback handler; we want to make sure that we roll back transactions that have already been confirmed
        // as well as transctions that we are about to confirm here
        List<PaymentTransaction> confirmedTransactions = new ArrayList<PaymentTransaction>();
        /**
         * This is a subset of the additionalTransactions that contains the transactions that were confirmed in this activity
         */
        Map<OrderPayment, PaymentTransactionType> additionalConfirmedTransactions = new HashMap<OrderPayment, PaymentTransactionType>();

        for (OrderPayment payment : order.getPayments()) {
            if (payment.isActive()) {
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
                        PaymentResponseDTO responseDTO = null;

                        if (PaymentType.CREDIT_CARD.equals(payment.getType())) {
                            // Handles the PCI-Compliant Scenario where you have an UNCONFIRMED CREDIT_CARD payment on the order.
                            // This can happen if you send the Credit Card directly to Broadleaf or you use a Digital Wallet solution like MasterPass.
                            // The Actual Credit Card PAN is stored in blSecurePU and will need to be sent to the Payment Gateway for processing.

                            PaymentRequestDTO s2sRequest = orderToPaymentRequestService.translatePaymentTransaction(payment.getAmount(), tx);
                            populateCreditCardOnRequest(s2sRequest, payment);
                            populateBillingAddressOnRequest(s2sRequest, payment);
                            populateCustomerOnRequest(s2sRequest, payment);

                            if (cfg.getConfiguration().isPerformAuthorizeAndCapture()) {
                                responseDTO = cfg.getTransactionService().authorizeAndCapture(s2sRequest);
                            } else {
                                responseDTO = cfg.getTransactionService().authorize(s2sRequest);
                            }

                        } else {
                            // This handles the THIRD_PARTY_ACCOUNT scenario (like PayPal Express Checkout) where
                            // the transaction just needs to be confirmed with the Gateway

                            responseDTO = cfg.getTransactionConfirmationService()
                                .confirmTransaction(orderToPaymentRequestService.translatePaymentTransaction(payment.getAmount(), tx));
                        }

                        if (responseDTO == null) {
                            String msg = "Unable to Confirm/Authorize the UNCONFIRMED Transaction with id: " + tx.getId() + ". " +
                                    "The ResponseDTO returned from the Gateway was null. Please check your implementation";
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
                        additionalTransactions.put(payment, transaction);

                        if (responseDTO.isSuccessful()) {
                            saveTokenToCustomerPayment(payment, responseDTO);
                            additionalConfirmedTransactions.put(payment, transaction.getType());
                        } else {
                            failedTransactions.add(responseDTO);
                        }

                    } else if (PaymentTransactionType.AUTHORIZE.equals(tx.getType()) ||
                            PaymentTransactionType.AUTHORIZE_AND_CAPTURE.equals(tx.getType())) {
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
        rollbackState.put(CONFIRMED_TRANSACTIONS, confirmedTransactions);
        ActivityStateManagerImpl.getStateManager().registerState(this, context, getRollbackHandler(), rollbackState);

        //Handle the failed transactions (default implementation is to throw a new CheckoutException)
        if (!failedTransactions.isEmpty()) {
            handleUnsuccessfulTransactions(failedTransactions, context);
        }

        // Add authorize and authorize_and_capture; there should only be one or the other in the payment
        Money paymentSum = new Money(BigDecimal.ZERO);
        for (OrderPayment payment : order.getPayments()) {
            if (payment.isActive()) {
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

    /**
     * Default implementation does nothing since implementation is dependent on payment gateway.
     *
     * @param payment, responseDTO
     */
    protected void saveTokenToCustomerPayment(OrderPayment payment, PaymentResponseDTO responseDTO) {
    }

    /**
     * Default implementation is to throw a generic CheckoutException which will be caught and displayed
     * on the Checkout Page where the Customer can try again. In many cases, this is
     * sufficient as it is usually recommended to display a generic Error Message to prevent
     * Credit Card fraud.
     *
     * The configured payment gateway may return a more specific error.
     * Each gateway is different and will often times return different error codes based on the acquiring bank as well.
     * In that case, you may override this method to decipher these errors
     * and handle it appropriately based on your business requirements.
     *
     * @param responseDTOs
     */
    protected void handleUnsuccessfulTransactions(List<PaymentResponseDTO> responseDTOs, ProcessContext<CheckoutSeed> context) throws Exception {
        //The Response DTO was not successful confirming/authorizing a transaction.
        String msg = "Attempting to confirm/authorize an UNCONFIRMED transaction on the order was unsuccessful.";
        if (LOG.isErrorEnabled()) {
            LOG.error(msg);
        }

        if (LOG.isTraceEnabled()) {
            for (PaymentResponseDTO responseDTO : responseDTOs) {
                LOG.trace(responseDTO.getRawResponse());
            }
        }

        throw new CheckoutException(msg, context.getSeedData());
    }


    protected void populateCreditCardOnRequest(PaymentRequestDTO requestDTO, OrderPayment payment) throws WorkflowException {

        if (payment.getReferenceNumber() != null) {
            CreditCardPayment creditCardPayment = (CreditCardPayment) secureOrderPaymentService.findSecurePaymentInfo(payment.getReferenceNumber(), PaymentType.CREDIT_CARD);
            if (creditCardPayment != null) {
                requestDTO.creditCard()
                        .creditCardHolderName(creditCardPayment.getNameOnCard())
                        .creditCardNum(creditCardPayment.getPan())
                        .creditCardExpDate(
                                constructExpirationDate(creditCardPayment.getExpirationMonth(),
                                        creditCardPayment.getExpirationYear()))
                        .creditCardExpMonth(creditCardPayment.getExpirationMonth() + "")
                        .creditCardExpYear(creditCardPayment.getExpirationYear() + "")
                        .done();
            }
        }
    }

    protected void populateBillingAddressOnRequest(PaymentRequestDTO requestDTO, OrderPayment payment) {

        if (payment != null && payment.getBillingAddress() != null) {
            Address address = payment.getBillingAddress();
            String addressLine2 = address.getAddressLine2();
            if (StringUtils.isNotBlank(address.getAddressLine3())) {
                addressLine2 = addressLine2 + " " + address.getAddressLine3();
            }

            String state = null;
            if (StringUtils.isNotBlank(address.getStateProvinceRegion())) {
                state = address.getStateProvinceRegion();
            } else if (address.getState() != null) {
                state = address.getState().getAbbreviation();
            }

            String country = null;
            if (address.getIsoCountryAlpha2() != null) {
                country = address.getIsoCountryAlpha2().getAlpha2();
            } else if (address.getCountry() != null) {
                country = address.getCountry().getAbbreviation();
            }

            String phone = address.getPhonePrimary() != null ? address.getPhonePrimary().getPhoneNumber() : null;

            requestDTO.billTo()
                    .addressFirstName(address.getFirstName())
                    .addressLastName(address.getLastName())
                    .addressLine1(address.getAddressLine1())
                    .addressLine2(addressLine2)
                    .addressCityLocality(address.getCity())
                    .addressStateRegion(state)
                    .addressPostalCode(address.getPostalCode())
                    .addressCountryCode(country)
                    .addressEmail(address.getEmailAddress())
                    .addressPhone(phone)
                    .addressCompanyName(address.getCompanyName())
                    .done();
        }

    }

    protected void populateCustomerOnRequest(PaymentRequestDTO requestDTO, OrderPayment payment) {
        if (payment != null && payment.getOrder() != null && payment.getOrder().getCustomer() != null) {
            Customer customer = payment.getOrder().getCustomer();

            requestDTO.customer()
                    .firstName(customer.getFirstName())
                    .lastName(customer.getLastName())
                    .email(customer.getEmailAddress())
                    .customerId(customer.getId() + "")
                    .done();
        }

    }

    /**
     * Default expiration date construction.
     * Some Payment Gateways may require a different format. Override if necessary or set the property
     * "gateway.config.global.expDateFormat" with a format string to provide the correct format for the configured gateway.
     * @param expMonth
     * @param expYear
     * @return
     */
    protected String constructExpirationDate(Integer expMonth, Integer expYear) {
        SimpleDateFormat sdf = new SimpleDateFormat(getGatewayExpirationDateFormat());
        DateTime exp = new DateTime()
                .withYear(expYear)
                .withMonthOfYear(expMonth);

        return sdf.format(exp.toDate());
    }

    protected String getGatewayExpirationDateFormat(){
        String format = BLCSystemProperty.resolveSystemProperty("gateway.config.global.expDateFormat");
        if (StringUtils.isBlank(format)) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("The System Property 'gateway.config.global.expDateFormat' is not set. " +
                        "Defaulting to the format 'MM/YY' for the configured gateway.");
            }
            format = "MM/YY";
        }
        return format;
    }

}
