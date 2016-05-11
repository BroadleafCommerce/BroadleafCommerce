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
package org.broadleafcommerce.core.order.strategy;

import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;

/**
 * The methods in this class are invoked by the add and update item to cart workflows.
 * Broadleaf provides two implementations, the default FulfillmentGroupItemStrategyImpl 
 * and also a strategy that does nothing to FulifllmentGroupItems, which can be configured
 * by injecting the NullFulfillmentGroupItemStrategyImpl class as the "blFulfillmentGroupItemStrategy"
 * bean.
 * 
 * The null strategy would be the approach taken prior to 2.0, where the user was required
 * to manage FulfillmentGroups and FulfillmentGroupItems by themselves. However, the new default
 * implmentation takes care of this for you by ensuring that FG Items and OrderItems stay in sync.
 * 
 * Note that even the null strategy <b>WILL</b> remove FulfillmentGroupItems if their corresponding
 * OrderItem is removed to prevent orphaned records.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface FulfillmentGroupItemStrategy {

    public CartOperationRequest onItemAdded(CartOperationRequest request) throws PricingException;

    public CartOperationRequest onItemUpdated(CartOperationRequest request) throws PricingException;
    
    public CartOperationRequest onItemRemoved(CartOperationRequest request) throws PricingException;
    
    public CartOperationRequest verify(CartOperationRequest request) throws PricingException;

    public void setRemoveEmptyFulfillmentGroups(boolean removeEmptyFulfillmentGroups);
    public boolean isRemoveEmptyFulfillmentGroups();

}
