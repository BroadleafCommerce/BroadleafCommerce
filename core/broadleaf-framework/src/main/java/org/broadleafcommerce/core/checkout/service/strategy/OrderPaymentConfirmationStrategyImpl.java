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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.config.service.SystemPropertiesService;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfigurationService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfigurationServiceProvider;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;
import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutSeed;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.payment.domain.secure.CreditCardPayment;
import org.broadleafcommerce.core.payment.service.OrderToPaymentRequestDTOService;
import org.broadleafcommerce.core.payment.service.SecureOrderPaymentService;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.WorkflowException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import javax.annotation.Resource;

/**
 * Strategy to handle confirming "UNCONFIRMED" transactions on an Order Payment during the checkout workflow.
 *
 * The default implementation is to (based on the passed in payment type):
 *  - If PaymentType == CREDIT_CARD -> AUTHORIZE or AUTHORIZE_AND_CAPTURE based on configuration of the gateway
 *  - Otherwise -> call the configured gateways {@link org.broadleafcommerce.common.payment.service.PaymentGatewayTransactionConfirmationService}
 *
 * However, if PENDING payments are enabled, then this logic will be by-passed and a new transaction
 * will be created as the payment is marked to be charged offline or asynchronously.
 *
 * @see {@link org.broadleafcommerce.core.checkout.service.workflow.ValidateAndConfirmPaymentActivity}
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blOrderPaymentConfirmationStrategy")
public class OrderPaymentConfirmationStrategyImpl implements OrderPaymentConfirmationStrategy {

    protected static final Log LOG = LogFactory.getLog(OrderPaymentConfirmationStrategyImpl.class);

    @Autowired(required = false)
    @Qualifier("blPaymentGatewayConfigurationServiceProvider")
    protected PaymentGatewayConfigurationServiceProvider paymentConfigurationServiceProvider;

    @Resource(name = "blOrderToPaymentRequestDTOService")
    protected OrderToPaymentRequestDTOService orderToPaymentRequestService;

    @Resource(name = "blSecureOrderPaymentService")
    protected SecureOrderPaymentService secureOrderPaymentService;

    @Resource(name = "blSystemPropertiesService")
    protected SystemPropertiesService systemPropertiesService;

    @Override
    public PaymentResponseDTO confirmTransaction(PaymentTransaction tx, ProcessContext<CheckoutSeed> context) throws PaymentException, WorkflowException, CheckoutException {
        return confirmTransactionInternal(tx, context, true);
    }

    @Override
    public PaymentResponseDTO confirmPendingTransaction(PaymentTransaction tx, ProcessContext<CheckoutSeed> context) throws PaymentException, WorkflowException, CheckoutException {
        return confirmTransactionInternal(tx, context, false);
    }

    protected PaymentResponseDTO confirmTransactionInternal(PaymentTransaction tx, ProcessContext<CheckoutSeed> context, boolean isCheckout) throws PaymentException, WorkflowException, CheckoutException {
        // Cannot confirm anything here if there is no provider
        if (paymentConfigurationServiceProvider == null) {
            String msg = "There are unconfirmed payment transactions on this payment but no payment gateway" +
                    " configuration or transaction confirmation service configured";
            LOG.error(msg);
            throw new CheckoutException(msg, context.getSeedData());
        }

        OrderPayment payment = tx.getOrderPayment();
        PaymentGatewayConfigurationService cfg = paymentConfigurationServiceProvider.getGatewayConfigurationService(tx.getOrderPayment().getGatewayType());
        PaymentResponseDTO responseDTO = null;

        PaymentRequestDTO confirmationRequest = orderToPaymentRequestService.translatePaymentTransaction(payment.getAmount(), tx);
        populateBillingAddressOnRequest(confirmationRequest, payment);
        populateCustomerOnRequest(confirmationRequest, payment);
        populateShippingAddressOnRequest(confirmationRequest, payment);

        if (isCheckout && enablePendingPaymentsOnCheckoutConfirmation()) {
            responseDTO = constructPendingTransaction(payment.getType(), payment.getGatewayType(), confirmationRequest);
        } else {
            if (PaymentType.CREDIT_CARD.equals(payment.getType())) {
                // Handles the PCI-Compliant Scenario where you have an UNCONFIRMED CREDIT_CARD payment on the order.
                // This can happen if you send the Credit Card directly to Broadleaf or you use a Digital Wallet solution like MasterPass.
                // The Actual Credit Card PAN is stored in blSecurePU and will need to be sent to the Payment Gateway for processing.

                populateCreditCardOnRequest(confirmationRequest, payment);

                if (cfg.getConfiguration().isPerformAuthorizeAndCapture()) {
                    responseDTO = cfg.getTransactionService().authorizeAndCapture(confirmationRequest);
                } else {
                    responseDTO = cfg.getTransactionService().authorize(confirmationRequest);
                }

            } else {
                // This handles the THIRD_PARTY_ACCOUNT scenario (like PayPal Express Checkout) where
                // the transaction just needs to be confirmed with the Gateway

                responseDTO = cfg.getTransactionConfirmationService().confirmTransaction(confirmationRequest);
            }
        }

        return responseDTO;
    }

    protected PaymentResponseDTO constructPendingTransaction(PaymentType paymentType, PaymentGatewayType gatewayType,
                                                             PaymentRequestDTO confirmationRequest) {
        PaymentResponseDTO responseDTO = new PaymentResponseDTO(paymentType, gatewayType);
        responseDTO.amount(new Money(confirmationRequest.getTransactionTotal()))
                .rawResponse(this.getClass().getName() + ": converting UNCONFIRMED transaction into a PENDING payment")
                .successful(true)
                .paymentTransactionType(PaymentTransactionType.PENDING);

        for (String key : confirmationRequest.getAdditionalFields().keySet()) {
            responseDTO.responseMap(key, confirmationRequest.getAdditionalFields().get(key).toString());
        }

        return responseDTO;
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
            orderToPaymentRequestService.populateBillTo(payment.getOrder(), requestDTO);
        }

    }

    protected void populateCustomerOnRequest(PaymentRequestDTO requestDTO, OrderPayment payment) {
        if (payment != null && payment.getOrder() != null && payment.getOrder().getCustomer() != null) {
            orderToPaymentRequestService.populateCustomerInfo(payment.getOrder(), requestDTO);
        }

    }

    protected void populateShippingAddressOnRequest(PaymentRequestDTO requestDTO, OrderPayment payment) {
        if(payment != null && payment.getOrder() != null) {
            orderToPaymentRequestService.populateShipTo(payment.getOrder(), requestDTO);
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
        String format = systemPropertiesService.resolveSystemProperty("gateway.config.global.expDateFormat");
        if (StringUtils.isBlank(format)) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("The System Property 'gateway.config.global.expDateFormat' is not set. " +
                        "Defaulting to the format 'MM/YY' for the configured gateway.");
            }
            format = "MM/YY";
        }
        return format;
    }

    /**
     * Set "gateway.config.global.enablePendingPayments" property to allow
     * confirmation of an "UNCONFIRMED" transaction into a "PENDING" state
     * instead of confirming into an AUTHORIZE or AUTHORIZE_AND_CAPTURE status.
     */
    protected boolean enablePendingPaymentsOnCheckoutConfirmation() {
        return systemPropertiesService.resolveBooleanSystemProperty("gateway.config.global.enablePendingPayments");
    }

}
