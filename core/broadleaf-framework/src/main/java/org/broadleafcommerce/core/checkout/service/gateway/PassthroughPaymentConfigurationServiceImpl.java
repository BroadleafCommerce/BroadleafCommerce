/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.checkout.service.gateway;

import org.broadleafcommerce.common.payment.service.AbstractPaymentGatewayConfigurationService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfiguration;
import org.broadleafcommerce.common.payment.service.PaymentGatewayRollbackService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayTransactionService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * A Default Configuration to handle Passthrough Payments, for example COD payments.
 * This default implementation just supports a rollback service and transaction service.
 *
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blPassthroughPaymentConfigurationService")
public class PassthroughPaymentConfigurationServiceImpl extends AbstractPaymentGatewayConfigurationService {

    @Resource(name = "blPassthroughPaymentConfiguration")
    protected PaymentGatewayConfiguration configuration;

    @Resource(name = "blPassthroughPaymentRollbackService")
    protected PaymentGatewayRollbackService rollbackService;

    @Resource(name = "blPassthroughPaymentTransactionService")
    protected PaymentGatewayTransactionService transactionService;

    @Override
    public PaymentGatewayConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public PaymentGatewayTransactionService getTransactionService() {
        return transactionService;
    }

}
