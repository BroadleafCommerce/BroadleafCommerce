/*
 * #%L
 * BroadleafCommerce Integration
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

package org.broadleafcommerce.core.payment.service;

import javax.annotation.Resource;

import org.broadleafcommerce.common.payment.service.AbstractPaymentGatewayConfigurationService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfiguration;
import org.broadleafcommerce.common.payment.service.PaymentGatewayRollbackService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayTransactionConfirmationService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayTransactionService;
import org.springframework.stereotype.Service;

/**
 * Copied from mycompany.sample.payment.service
 * We need it to be picked up by the  siteintegration setup superlasses of groovy, which already scans "org" packages.
 * @author gdiaz
 */
@Service("blNullIntegrationGatewayConfigurationService")
public class NullIntegrationGatewayConfigurationServiceImpl extends AbstractPaymentGatewayConfigurationService {

    @Resource(name = "blNullIntegrationGatewayConfiguration")
    protected NullIntegrationGatewayConfiguration configuration;

    @Resource(name = "blNullIntegrationGatewayRollbackService")
    protected PaymentGatewayRollbackService rollbackService;

    @Resource(name = "blNullIntegrationGatewayHostedTransactionConfirmationService")
    protected NullIntegrationGatewayTransactionConfirmationServiceImpl transactionConfirmationServiceImpl;

    @Resource(name = "blNullIntegrationGatewayTransactionService")
    protected NullIntegrationGatewayTransactionServiceImpl transactionService;

    public PaymentGatewayConfiguration getConfiguration() {
        return configuration;
    }

    public PaymentGatewayTransactionService getTransactionService() {
        return transactionService;
    }

    public PaymentGatewayTransactionConfirmationService getTransactionConfirmationService() {
        return transactionConfirmationServiceImpl;
    }

    public PaymentGatewayRollbackService getRollbackService() {
        return rollbackService;
    }
}
