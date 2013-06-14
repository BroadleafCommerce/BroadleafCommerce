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

}
