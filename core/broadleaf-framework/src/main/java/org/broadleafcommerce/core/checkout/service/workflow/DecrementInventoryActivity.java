/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.checkout.service.workflow;

import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.inventory.service.InventoryService;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.workflow.WorkflowInventoryExtensionManager;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.state.ActivityStateManagerImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * Decrements inventory
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class DecrementInventoryActivity extends BaseActivity<ProcessContext<CheckoutSeed>> {

    @Resource(name = "blInventoryService")
    protected InventoryService inventoryService;
    
    @Resource(name = "blWorkflowInventoryExtensionManager")
    protected WorkflowInventoryExtensionManager extensionManager;
    
    public DecrementInventoryActivity() {
        super();
        super.setAutomaticallyRegisterRollbackHandler(false);
    }

    @Override
    public ProcessContext<CheckoutSeed> execute(ProcessContext<CheckoutSeed> context) throws Exception {
        CheckoutSeed seed = context.getSeedData();
        List<OrderItem> orderItems = seed.getOrder().getOrderItems();

        //map to hold skus and quantity purchased
        HashMap<Sku, Integer> skuInventoryMap = new HashMap<Sku, Integer>();

        for (OrderItem orderItem : orderItems) {
            if (orderItem instanceof DiscreteOrderItem) {
                Sku sku = ((DiscreteOrderItem) orderItem).getSku();
                Integer quantity = skuInventoryMap.get(sku);
                if (quantity == null) {
                    quantity = orderItem.getQuantity();
                } else {
                    quantity += orderItem.getQuantity();
                }
                if (InventoryType.CHECK_QUANTITY.equals(sku.getInventoryType())) {
                    skuInventoryMap.put(sku, quantity);
                }
            } else if (orderItem instanceof BundleOrderItem) {
                BundleOrderItem bundleItem = (BundleOrderItem) orderItem;
                if (InventoryType.CHECK_QUANTITY.equals(bundleItem.getSku().getInventoryType())) {
                    // add the bundle sku of quantities to decrement
                    skuInventoryMap.put(bundleItem.getSku(), bundleItem.getQuantity());
                }
                
                // Now add all of the discrete items within the bundl
                List<DiscreteOrderItem> discreteItems = bundleItem.getDiscreteOrderItems();
                for (DiscreteOrderItem discreteItem : discreteItems) {
                    if (InventoryType.CHECK_QUANTITY.equals(discreteItem.getSku().getInventoryType())) {
                        Integer quantity = skuInventoryMap.get(discreteItem.getSku().getId());
                        if (quantity == null) {
                            quantity = (discreteItem.getQuantity() * bundleItem.getQuantity());
                        } else {
                            quantity += (discreteItem.getQuantity() * bundleItem.getQuantity());
                        }
                        skuInventoryMap.put(discreteItem.getSku(), quantity);
                    }
                }
            }
        }

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
            ExtensionResultStatusType extensionResult = extensionManager.getProxy().decrementInventory(skuInventoryMap, context, rollbackState);
            if (ExtensionResultStatusType.NOT_HANDLED.equals(extensionResult)) {
                inventoryService.decrementInventory(skuInventoryMap);
                
                if (getRollbackHandler() != null && !getAutomaticallyRegisterRollbackHandler()) {
                    rollbackState.put(DecrementInventoryRollbackHandler.ROLLBACK_BLC_INVENTORY_DECREMENTED, skuInventoryMap);
                    rollbackState.put(DecrementInventoryRollbackHandler.ROLLBACK_BLC_ORDER_ID, seed.getOrder().getId());
                }
            }
        }

        

        return context;

    }

    public void setInventoryService(InventoryService service) {
        this.inventoryService = service;
    }

}
