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
package org.broadleafcommerce.core.checkout.service.workflow;

import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.inventory.service.ContextualInventoryService;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.state.ActivityStateManagerImpl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

/**
 * Decrements inventory
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class DecrementInventoryActivity extends BaseActivity<ProcessContext<CheckoutSeed>> {

    @Resource(name = "blInventoryService")
    protected ContextualInventoryService inventoryService;
    
    public DecrementInventoryActivity() {
        super();
        super.setAutomaticallyRegisterRollbackHandler(false);
    }

    @Override
    public ProcessContext<CheckoutSeed> execute(ProcessContext<CheckoutSeed> context) throws Exception {
        CheckoutSeed seed = context.getSeedData();

        Map<Sku, Integer> skuQuantityMap = new HashMap<>();
        for (OrderItem orderItem : seed.getOrder().getOrderItems()) {
            if (orderItem instanceof DiscreteOrderItem) {
                Sku sku = ((DiscreteOrderItem) orderItem).getSku();
                Integer q = skuQuantityMap.get(sku);
                q = q == null ? orderItem.getQuantity() : q + orderItem.getQuantity();
                skuQuantityMap.put(sku, q);
            }
        }

        //map to hold skus and quantity purchased
        Map<Sku, Integer> skuInventoryMap = inventoryService.buildSkuInventoryMap(skuQuantityMap);

        Map<String, Object> rollbackState = new HashMap<String, Object>();
        if (getRollbackHandler() != null && !getAutomaticallyRegisterRollbackHandler()) {
            if (getStateConfiguration() != null && !getStateConfiguration().isEmpty()) {
                rollbackState.putAll(getStateConfiguration());
            }
            // Register the map with the rollback state object early on; this allows the extension handlers to incrementally
            // add state while decrementing but still throw an exception
            ActivityStateManagerImpl.getStateManager().registerState(this, context, getRollbackRegion(), getRollbackHandler(), rollbackState);
        }
            
        if (!skuInventoryMap.isEmpty()) {
            Map<String, Object> contextualInfo = new HashMap<String, Object>();
            contextualInfo.put(ContextualInventoryService.ORDER_KEY, context.getSeedData().getOrder());
            contextualInfo.put(ContextualInventoryService.ROLLBACK_STATE_KEY, new HashMap<String, Object>());
            inventoryService.decrementInventory(skuInventoryMap, contextualInfo);
            
            if (getRollbackHandler() != null && !getAutomaticallyRegisterRollbackHandler()) {
                rollbackState.put(DecrementInventoryRollbackHandler.ROLLBACK_BLC_INVENTORY_DECREMENTED, skuInventoryMap);
                rollbackState.put(DecrementInventoryRollbackHandler.ROLLBACK_BLC_ORDER_ID, seed.getOrder().getId());
            }
            
            // add the rollback state that was used in the rollback handler
            rollbackState.put(DecrementInventoryRollbackHandler.EXTENDED_ROLLBACK_STATE, contextualInfo.get(ContextualInventoryService.ROLLBACK_STATE_KEY));
        }

        return context;
    }

}
