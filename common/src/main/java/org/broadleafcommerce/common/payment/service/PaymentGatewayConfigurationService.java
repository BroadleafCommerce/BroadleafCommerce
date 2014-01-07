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

import org.broadleafcommerce.common.web.payment.expression.PaymentGatewayFieldExtensionHandler;
import org.broadleafcommerce.common.web.payment.processor.TRCreditCardExtensionHandler;

/**
 * Each payment gateway module should configure an instance of this. In order for multiple gateways to exist in the system
 * at the same time, a list of these is managed via the {@link PaymentGatewayConfigurationServiceProvider}. This allows for proper
 * delegation to the right gateway to perform operations against via different order payments on an order.
 * 
 * @author Elbert Bautista (elbertbautista)
 */
public class PaymentGatewayConfigurationService {

    protected PaymentGatewayConfiguration configuration;
    protected PaymentGatewayTransactionService transactionService;
    protected PaymentGatewayTransactionConfirmationService transactionConfirmationService;
    protected PaymentGatewayReportingService reportingService;
    protected PaymentGatewayCreditCardService creditCardService;
    protected PaymentGatewayCustomerService customerService;
    protected PaymentGatewaySubscriptionService subscriptionService;
    protected PaymentGatewayFraudService fraudService;
    protected PaymentGatewayHostedService hostedService;
    protected PaymentGatewayRollbackService rollbackService;
    protected PaymentGatewayWebResponseService webResponseService;

    protected TRCreditCardExtensionHandler creditCardExtensionHandler;
    protected PaymentGatewayFieldExtensionHandler fieldExtensionHandler;

    public PaymentGatewayConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(PaymentGatewayConfiguration configuration) {
        this.configuration = configuration;
    }

    public PaymentGatewayTransactionService getTransactionService() {
        return transactionService;
    }

    public void setTransactionService(PaymentGatewayTransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public PaymentGatewayTransactionConfirmationService getTransactionConfirmationService() {
        return transactionConfirmationService;
    }

    public void setTransactionConfirmationService(PaymentGatewayTransactionConfirmationService transactionConfirmationService) {
        this.transactionConfirmationService = transactionConfirmationService;
    }

    public PaymentGatewayReportingService getReportingService() {
        return reportingService;
    }

    public void setReportingService(PaymentGatewayReportingService reportingService) {
        this.reportingService = reportingService;
    }

    public PaymentGatewayCreditCardService getCreditCardService() {
        return creditCardService;
    }

    public void setCreditCardService(PaymentGatewayCreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    public PaymentGatewayCustomerService getCustomerService() {
        return customerService;
    }

    public void setCustomerService(PaymentGatewayCustomerService customerService) {
        this.customerService = customerService;
    }

    public PaymentGatewaySubscriptionService getSubscriptionService() {
        return subscriptionService;
    }

    public void setSubscriptionService(PaymentGatewaySubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public PaymentGatewayFraudService getFraudService() {
        return fraudService;
    }

    public void setFraudService(PaymentGatewayFraudService fraudService) {
        this.fraudService = fraudService;
    }

    public PaymentGatewayHostedService getHostedService() {
        return hostedService;
    }

    public void setHostedService(PaymentGatewayHostedService hostedService) {
        this.hostedService = hostedService;
    }

    public PaymentGatewayRollbackService getRollbackService() {
        return rollbackService;
    }

    public void setRollbackService(PaymentGatewayRollbackService rollbackService) {
        this.rollbackService = rollbackService;
    }

    public PaymentGatewayWebResponseService getWebResponseService() {
        return webResponseService;
    }

    public void setWebResponseService(PaymentGatewayWebResponseService webResponseService) {
        this.webResponseService = webResponseService;
    }

    public TRCreditCardExtensionHandler getCreditCardExtensionHandler() {
        return creditCardExtensionHandler;
    }

    public void setCreditCardExtensionHandler(TRCreditCardExtensionHandler creditCardExtensionHandler) {
        this.creditCardExtensionHandler = creditCardExtensionHandler;
    }

    public PaymentGatewayFieldExtensionHandler getFieldExtensionHandler() {
        return fieldExtensionHandler;
    }

    public void setFieldExtensionHandler(PaymentGatewayFieldExtensionHandler fieldExtensionHandler) {
        this.fieldExtensionHandler = fieldExtensionHandler;
    }
}
