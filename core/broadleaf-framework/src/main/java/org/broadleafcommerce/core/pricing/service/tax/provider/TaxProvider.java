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
package org.broadleafcommerce.core.pricing.service.tax.provider;

import org.broadleafcommerce.common.config.domain.ModuleConfiguration;
import org.broadleafcommerce.common.config.service.ModuleProvider;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.pricing.service.exception.TaxException;

/**
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface TaxProvider extends ModuleProvider {

    /**
     * Calculates taxes on an entire order. Returns the order with taxes included.
     * @param order
     * @param config
     * @return
     */
    public Order calculateTaxForOrder(Order order, ModuleConfiguration config) throws TaxException;

    /**
     * This method provides the implementation an opportunity to finalize taxes on the order. This is 
     * often required when tax sub systems require tax documents to be created on checkout. This method 
     * will typically be called by the checkout workflow, rather than by the pricing workflow. Some implementations 
     * may wish to do nothing in this method, except perhaps recalculate taxes.
     * 
     * @param order
     * @param config
     * @return
     * @throws TaxException
     */
    public Order commitTaxForOrder(Order order, ModuleConfiguration config) throws TaxException;

    /**
     * Some tax providers store tax details from an order on an external system for reporting and tax reconcilliation. 
     * This allows one to cancel or undo tax recording in an external system.  Typically, this will be called to offset 
     * a call to commitTaxForOrder.  This might be called, for example, in a rollback handler for a checkout workflow activity 
     * that calls commitTaxForOrder.  Many implementations may wish to do nothing in this method.
     * 
     * @param order
     * @param config
     * @throws TaxException
     */
    public void cancelTax(Order order, ModuleConfiguration config) throws TaxException;
}
