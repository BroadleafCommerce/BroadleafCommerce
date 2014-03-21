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
package org.broadleafcommerce.core.order.service.workflow;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutSeed;
import org.broadleafcommerce.core.checkout.service.workflow.DecrementInventoryActivity;
import org.broadleafcommerce.core.checkout.service.workflow.DecrementInventoryRollbackHandler;
import org.broadleafcommerce.core.inventory.service.InventoryService;
import org.broadleafcommerce.core.inventory.service.InventoryUnavailableException;
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.state.RollbackFailureException;

import java.util.Map;

/**
 * Extension handler dealing with inventory operations within different workflows. Usually all methods are implemented for
 * a particular inventory provider
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface WorkflowInventoryExtensionHandler extends ExtensionHandler {

    /**
     * Invoked during the blAddItemWorkflow and blUpdateItemWorkflow within the {@link CheckAvailabilityActivity} if it is hooked up.
     * If this returns {@link ExtensionResultStatusType#NOT_HANDLED} then the default inventory system in the framework will
     * be checked as long as the given <b>sku</b>'s {@link Sku#getInventoryType()} is {@link InventoryType#CHECK_QUANTITY}. 
     * If you decide to implement this method then you MUST return {@link ExtensionResultStatusType#HANDLED} in order to
     * prevent the framework from checking default inventory.
     * 
     * @see {@link InventoryService}
     * @see {@link CheckAvailabilityActivity}
     */
    public ExtensionResultStatusType checkAvailability(Sku sku,
                Integer requestedQuantity,
                ProcessContext<CartOperationRequest> context) throws InventoryUnavailableException;
    
    /**
     * Invoked during the blCheckoutWorkflow. If this returns {@link ExtensionResultStatusType#NOT_HANDLED} then the default
     * inventory system in the framework will be checked as long as the given <b>sku</b>'s {@link Sku#getInventoryType()} is
     * {@link InventoryType#CHECK_QUANTITY}. If you decide to implement this method then you MUST return 
     * {@link ExtensionResultStatusType#HANDLED} in order to prevent the framework from attempting to decrement its default inventory.
     * 
     * @throws {@link InventoryUnavailableException} if there is not enough inventory available for any of the Skus within
     * <b>skuQuantities</b>
     * @see {@link InventoryService}
     * @see {@link DecrementInventoryActivity}
     */
    public ExtensionResultStatusType decrementInventory(Map<Sku, Integer> skuQuantities,
                ProcessContext<CheckoutSeed> context,
                Map<String, Object> rollbackState) throws InventoryUnavailableException;

    /**
     * Invoked from the {@link DecrementInventoryRollbackHandler} to rollback the inventory operation performed by
     * {@link #decrementInventory(Map, ProcessContext, Map)}.
     * 
     * @param inventoryToIncrement - inventory that was previously <b>decremented</b> and now should be incremented
     * @param incrementedInventory - inventory that was previously <b>incremented</b> and now should be decremented
     * @param orderId
     * @return If being implemented, {@link ExtensionResultStatusType#HANDLED}
     * @throws {@link RollbackFailureException} if there was a problem rolling back the inventory operation
     * @see {@link DecrementInventoryRollbackHandler}
     */
    public ExtensionResultStatusType rollbackInventoryOperation(ProcessContext<CheckoutSeed> context,
            Map<String, Object> rollbackState) throws RollbackFailureException;
}
