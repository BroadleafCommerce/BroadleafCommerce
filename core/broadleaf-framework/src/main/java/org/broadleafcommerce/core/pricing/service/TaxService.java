/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
     * Some tax providers store tax details from an order on an external system for reporting and tax reconcilliation. 
     * This allows one to cancel or undo tax recording in an external system.  Typically, this will be called to offset 
     * a call to commitTaxForOrder.  This might be called, for example, in a rollback handler for a checkout workflow activity 
     * that calls commitTaxForOrder.
     * 
     * @param order
     * @throws TaxException
     */
    public void cancelTax(Order order) throws TaxException;

}
