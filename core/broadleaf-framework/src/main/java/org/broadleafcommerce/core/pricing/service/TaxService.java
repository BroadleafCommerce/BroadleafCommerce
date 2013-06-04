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

    /**
     * Some tax integrators or tax programs allow you to cancel the tax for an order. This prevents external, 
     * or third party, systems from being out of synch from a reporting or tax reconcilliation perspective. Implementors should 
     * not modify the order, or remove tax details from the order.  The order should be considered immutable.  Any compensating 
     * transactions or modifications to the order should be done outside of this method.  Implementations that do not allow 
     * for this should simply implement this method in a way that it is a pass through, or does nothing.
     * @param order
     * @throws TaxException
     */
    public void cancelTaxForOrder(Order order) throws TaxException;

}
