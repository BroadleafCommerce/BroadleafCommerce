/*
 * #%L
 * BroadleafCommerce Integration
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
