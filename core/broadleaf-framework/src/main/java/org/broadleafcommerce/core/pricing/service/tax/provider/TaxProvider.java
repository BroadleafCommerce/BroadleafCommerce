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

}
