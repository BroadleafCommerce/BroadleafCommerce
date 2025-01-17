/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
/**
 * 
 */
package org.broadleafcommerce.core.order.service.workflow;

import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.inventory.service.ContextualInventoryService;
import org.broadleafcommerce.core.inventory.service.InventoryUnavailableException;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

import java.util.Objects;

import javax.annotation.Resource;

/**
 * Common functionality between checking availability between adds and updates
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public abstract class AbstractCheckAvailabilityActivity extends BaseActivity<ProcessContext<CartOperationRequest>> {

    @Resource(name = "blInventoryService")
    protected ContextualInventoryService inventoryService;

    @Override
    public boolean shouldExecute(ProcessContext<CartOperationRequest> context) {
        Order order = context.getSeedData().getOrder();
        return order != null && !Objects.equals(order.getStatus(), OrderStatus.NAMED);
    }
    
    protected void checkSkuAvailability(Order order, Sku sku, Integer requestedQuantity) throws InventoryUnavailableException {
        inventoryService.checkSkuAvailability(order, sku, requestedQuantity);
    }
}
