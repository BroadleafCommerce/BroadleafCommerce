/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

/**
 * Each payment gateway module should configure an instance of this. In order for multiple gateways to exist in the system
 * at the same time, a list of these is managed via the {@link PaymentGatewayConfigurationServiceProvider}. This allows for proper
 * delegation to the right gateway to perform operations against via different order payments on an order.
 *
 * @author Elbert Bautista (elbertbautista)
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface PaymentGatewayConfigurationService {

    PaymentGatewayConfiguration getConfiguration();

    PaymentGatewayTransactionService getTransactionService();

    PaymentGatewayTransactionConfirmationService getTransactionConfirmationService();

    PaymentGatewayReportingService getReportingService();

    PaymentGatewayCreditCardService getCreditCardService();

    PaymentGatewayCustomerService getCustomerService();

    PaymentGatewaySubscriptionService getSubscriptionService();

    PaymentGatewayFraudService getFraudService();

    PaymentGatewayHostedService getHostedService();

    PaymentGatewayRollbackService getRollbackService();

    PaymentGatewayWebResponseService getWebResponseService();

    PaymentGatewayTransparentRedirectService getTransparentRedirectService();

    PaymentGatewayClientTokenService getClientTokenService();

    TRCreditCardExtensionHandler getCreditCardExtensionHandler();

    PaymentGatewayFieldExtensionHandler getFieldExtensionHandler();

    CreditCardTypesExtensionHandler getCreditCardTypesExtensionHandler();

}
