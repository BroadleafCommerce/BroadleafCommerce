/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
import org.broadleafcommerce.common.web.payment.processor.CreditCardTypesExtensionHandler;
import org.broadleafcommerce.common.web.payment.processor.TRCreditCardExtensionHandler;

public class AbstractPaymentGatewayConfigurationService implements PaymentGatewayConfigurationService {

    @Override
    public PaymentGatewayConfiguration getConfiguration() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public PaymentGatewayTransactionService getTransactionService() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public PaymentGatewayTransactionConfirmationService getTransactionConfirmationService() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public PaymentGatewayReportingService getReportingService() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public PaymentGatewayCreditCardService getCreditCardService() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public PaymentGatewayCustomerService getCustomerService() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public PaymentGatewaySubscriptionService getSubscriptionService() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public PaymentGatewayFraudService getFraudService() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public PaymentGatewayHostedService getHostedService() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public PaymentGatewayRollbackService getRollbackService() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public PaymentGatewayWebResponseService getWebResponseService() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public PaymentGatewayTransparentRedirectService getTransparentRedirectService() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public PaymentGatewayClientTokenService getClientTokenService() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public TRCreditCardExtensionHandler getCreditCardExtensionHandler() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public PaymentGatewayFieldExtensionHandler getFieldExtensionHandler() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public CreditCardTypesExtensionHandler getCreditCardTypesExtensionHandler() {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
