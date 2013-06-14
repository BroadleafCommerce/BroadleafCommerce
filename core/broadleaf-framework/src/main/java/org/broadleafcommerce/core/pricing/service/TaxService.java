package org.broadleafcommerce.core.pricing.service;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.pricing.service.exception.TaxException;

/**
 * Generic service to calculate taxes. Those implementing tax calculation logic should, more likely, 
 * use the default Broadleaf TaxService implementation, and implement TaxProvider.
 * 
 * @author Kelly Tisdell
 *
 */
public interface TaxService {

    /**
     * Calculates tax for the order.
     * 
     * @param order
     * @return
     * @throws TaxException
     */
    public Order calculateTaxForOrder(Order order) throws TaxException;

    /**
     * Commits tax for the order. Some implemenations may do nothing. Others may delegate 
     * to a tax provider that stores taxes in another system for reporting or reconcilliation.
     * @param order
     * @return
     * @throws TaxException
     */
    public Order commitTaxForOrder(Order order) throws TaxException;

}
