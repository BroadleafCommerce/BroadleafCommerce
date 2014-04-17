/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.core.checkout.service.gateway;

import org.broadleafcommerce.common.payment.service.PaymentGatewayConfiguration;
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfigurationService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayCreditCardService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayCustomerService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayFraudService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayHostedService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayReportingService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayRollbackService;
import org.broadleafcommerce.common.payment.service.PaymentGatewaySubscriptionService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayTransactionConfirmationService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayTransactionService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayTransparentRedirectService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayWebResponseService;
import org.broadleafcommerce.common.web.payment.expression.PaymentGatewayFieldExtensionHandler;
import org.broadleafcommerce.common.web.payment.processor.CreditCardTypesExtensionHandler;
import org.broadleafcommerce.common.web.payment.processor.TRCreditCardExtensionHandler;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * A Default Configuration to handle Passthrough Payments, for example COD payments.
 * This default implementation just supports a rollback service.
 *
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blPassthroughPaymentConfigurationService")
public class PassthroughPaymentConfigurationServiceImpl implements PaymentGatewayConfigurationService {

    @Resource(name = "blPassthroughPaymentConfiguration")
    protected PaymentGatewayConfiguration configuration;

    @Resource(name = "blPassthroughPaymentRollbackService")
    protected PaymentGatewayRollbackService rollbackService;

    @Override
    public PaymentGatewayConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public PaymentGatewayTransactionService getTransactionService() {
        return null;
    }

    @Override
    public PaymentGatewayTransactionConfirmationService getTransactionConfirmationService() {
        return null;
    }

    @Override
    public PaymentGatewayReportingService getReportingService() {
        return null;
    }

    @Override
    public PaymentGatewayCreditCardService getCreditCardService() {
        return null;
    }

    @Override
    public PaymentGatewayCustomerService getCustomerService() {
        return null;
    }

    @Override
    public PaymentGatewaySubscriptionService getSubscriptionService() {
        return null;
    }

    @Override
    public PaymentGatewayFraudService getFraudService() {
        return null;
    }

    @Override
    public PaymentGatewayHostedService getHostedService() {
        return null;
    }

    @Override
    public PaymentGatewayRollbackService getRollbackService() {
        return rollbackService;
    }

    @Override
    public PaymentGatewayWebResponseService getWebResponseService() {
        return null;
    }

    @Override
    public PaymentGatewayTransparentRedirectService getTransparentRedirectService() {
        return null;
    }

    @Override
    public TRCreditCardExtensionHandler getCreditCardExtensionHandler() {
        return null;
    }

    @Override
    public PaymentGatewayFieldExtensionHandler getFieldExtensionHandler() {
        return null;
    }

    @Override
    public CreditCardTypesExtensionHandler getCreditCardTypesExtensionHandler() {
        return null;
    }
}
