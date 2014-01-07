package org.broadleafcommerce.common.payment.service;

import org.broadleafcommerce.common.payment.PaymentGatewayType;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * <p>This represents the main servic bus for grabbing configurations to configured payment gateways to execute service calls
 * programmatically. The main use for this in the framework is in
 * {@link org.broadleafcommerce.core.checkout.service.workflow.ValidateAndConfirmPaymentActivity} and its rollback handler
 * {@link org.broadleafcommerce.core.checkout.service.workflow.ConfirmPaymentsRollbackHandler}. Since multiple gateways
 * can be configured for a single implementation (like Paypal Express and Braintree, or Paypal Express, a credit card
 * module and a gift card module) this allows you to select between them to perform additional operations on a payment
 * transaction.</p>
 * 
 * <p>Once you obtain the correct gateway configuration bean, you can then obtain links to each service to perform individual
 * operations like {@link PaymentGatewayTransactionService} or {@link PaymentGatewayFraudService}.</p>
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface PaymentGatewayConfigurationServiceProvider {
    
    /**
     * <p>Returns the first {@link PaymentGatewayConfigurationService} that matches the given {@link PaymentGatewayType}. Useful when
     * you need a particular {@link PaymentGatewayConfigurationService} to communicate in different ways to a payment gateway.</p>
     * 
     * @throws IllegalArgumentException if the given {@link PaymentGatewayType} is null or if there is no configuration for
     * the given {@link PaymentGatewayType}.
     */
    public PaymentGatewayConfigurationService getGatewayConfigurationService(@Nonnull PaymentGatewayType gatewayType);
    
    /*
     * All of the gateway configurations configured in the system.
     */
    public List<PaymentGatewayConfigurationService> getGatewayConfigurationServices();
    
    public void setGatewayConfigurationServices(List<PaymentGatewayConfigurationService> gatewayConfigurationServices);
    
}
