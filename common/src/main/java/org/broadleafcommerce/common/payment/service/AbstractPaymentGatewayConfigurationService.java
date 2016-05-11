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
