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
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.state.ActivityStateManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * Decrements inventory
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blDecrementInventoryActivity")
public class DecrementInventoryActivity extends BaseActivity<ProcessContext<CheckoutSeed>> {

    public static final int ORDER = 6000;
    
    @Resource(name = "blInventoryService")
    protected ContextualInventoryService inventoryService;
    
    @Autowired
    public DecrementInventoryActivity(@Qualifier("blDecrementInventoryRollbackHandler") DecrementInventoryRollbackHandler rollbackHandler) {
        super();
        super.setAutomaticallyRegisterRollbackHandler(false);
        setOrder(ORDER);
    }

    @Override
    public ProcessContext<CheckoutSeed> execute(ProcessContext<CheckoutSeed> context) throws Exception {
        CheckoutSeed seed = context.getSeedData();
        List<OrderItem> orderItems = seed.getOrder().getOrderItems();

        //map to hold skus and quantity purchased
        Map<Sku, Integer> skuInventoryMap = inventoryService.buildSkuInventoryMap(seed.getOrder());

        Map<String, Object> rollbackState = new HashMap<>();
        if (getRollbackHandler() != null && !getAutomaticallyRegisterRollbackHandler()) {
            if (getStateConfiguration() != null && !getStateConfiguration().isEmpty()) {
                rollbackState.putAll(getStateConfiguration());
            }
            // Register the map with the rollback state object early on; this allows the extension handlers to incrementally
            // add state while decrementing but still throw an exception
            ActivityStateManagerImpl.getStateManager().registerState(this, context, getRollbackRegion(), getRollbackHandler(), rollbackState);
        }
            
        if (!skuInventoryMap.isEmpty()) {
            Map<String, Object> contextualInfo = new HashMap<>();
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
