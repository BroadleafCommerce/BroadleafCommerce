package org.broadleafcommerce.core.pricing.service.tax.provider;

import org.broadleafcommerce.common.config.domain.ModuleConfiguration;
import org.broadleafcommerce.common.config.service.ModuleProvider;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.pricing.service.exception.TaxException;

public interface TaxProvider extends ModuleProvider {

    /**
     * Calculates taxes on an entire order.  Returns the order with taxes included.
     * @param order
     * @param config
     * @return
     */
    public Order calculateTaxForOrder(Order order, ModuleConfiguration config) throws TaxException;

    /**
     * This method provides the implementation an opportunity to finalize taxes on the order. This is 
     * often required when tax sub systems require tax documents to be created on checkout. This method 
     * will typically be called by the checkout workflow, rather than by the pricing workflow.
     * 
     * @param order
     * @param config
     * @return
     * @throws TaxException
     */
    public Order commitTaxForOrder(Order order, ModuleConfiguration config) throws TaxException;

    /**
     * Some tax integrators or tax programs allow you to cancel the tax for an order. This prevents external, 
     * or third party, systems from being out of synch from a reporting or tax reconcilliation perspective. Implementors should 
     * not modify the order, or remove tax details from the order.  The order should be considered immutable.  Any compensating 
     * transactions or modifications to the order should be done outside of this method.  Implementations that do not allow 
     * for this should simply implement this method in a way that it is a pass through, or does nothing.
     * @param order
     * @param config
     * @throws TaxException
     */
    public void cancelTaxForOrder(Order order, ModuleConfiguration config) throws TaxException;

}
