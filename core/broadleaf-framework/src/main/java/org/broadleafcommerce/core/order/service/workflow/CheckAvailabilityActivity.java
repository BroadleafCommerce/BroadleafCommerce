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
package org.broadleafcommerce.core.order.service.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.inventory.service.ContextualInventoryService;
import org.broadleafcommerce.core.inventory.service.InventoryUnavailableException;
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.call.NonDiscreteOrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

/**
 * This activity handles both adds and updates. In both cases, this will check the availability and quantities (if applicable)
 * of the passed in request. If this is an update request, this will use the {@link Sku} from {@link OrderItemRequestDTO#getOrderItemId()}.
 * If this is an add request, there is no order item yet so the {@link Sku} is looked up via the {@link OrderItemRequestDTO#getSkuId()}.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class CheckAvailabilityActivity extends BaseActivity<ProcessContext<CartOperationRequest>> {

    private static final Log LOG = LogFactory.getLog(CheckAvailabilityActivity.class);
    
    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;
    
    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;
    
    @Resource(name = "blInventoryService")
    protected ContextualInventoryService inventoryService;
    
    @Override
    public ProcessContext<CartOperationRequest> execute(ProcessContext<CartOperationRequest> context) throws Exception {
        CartOperationRequest request = context.getSeedData();
        OrderItemRequestDTO orderItemRequestDTO = request.getItemRequest();
        Sku sku;
        OrderItem orderItem = null;
        Long orderItemId = request.getItemRequest().getOrderItemId();
        if (orderItemId != null) {
            // this must be an update request as there is an order item ID available
            orderItem = orderItemService.readOrderItemById(orderItemId);
            if (orderItem instanceof DiscreteOrderItem) {
                sku = ((DiscreteOrderItem) orderItem).getSku();
            } else if (orderItem instanceof BundleOrderItem) {
                sku = ((BundleOrderItem) orderItem).getSku();
            } else {
                LOG.warn("Could not check availability; did not recognize passed-in item " + orderItem.getClass().getName());
                return context;
            }
        } else if (orderItemRequestDTO instanceof NonDiscreteOrderItemRequestDTO){
            return context;
        } else {
            // No order item, this must be a new item add request
            Long skuId = request.getItemRequest().getSkuId();
            sku = catalogService.findSkuById(skuId);
        }

        Order order = context.getSeedData().getOrder();
        Integer requestedQuantity = request.getItemRequest().getQuantity();
        checkSkuAvailability(order, sku, requestedQuantity);

        // If we have an order item, check the new quantities for its child items
        if (orderItem != null) {
            Integer previousQty = orderItem.getQuantity();
            for (OrderItem child : orderItem.getChildOrderItems()) {
                Sku childSku = ((DiscreteOrderItem) child).getSku();
                Integer childQuantity = child.getQuantity();
                childQuantity = childQuantity / previousQty;
                checkSkuAvailability(order, childSku, childQuantity * requestedQuantity);
            }
        }

        return context;
    }

    protected void checkSkuAvailability(Order order, Sku sku, Integer requestedQuantity) throws InventoryUnavailableException {
        // First check if this Sku is available
        if (!sku.isAvailable()) {
            throw new InventoryUnavailableException("The referenced Sku " + sku.getId() + " is marked as unavailable", sku.getId(), requestedQuantity, 0);
        }

        if (InventoryType.CHECK_QUANTITY.equals(sku.getInventoryType())) {
            Map<String, Object> inventoryContext = new HashMap<String, Object>();
            inventoryContext.put(ContextualInventoryService.ORDER_KEY, order);
            boolean available = inventoryService.isAvailable(sku, requestedQuantity, inventoryContext);
            if (!available) {
                throw new InventoryUnavailableException(sku.getId(),
                        requestedQuantity, inventoryService.retrieveQuantityAvailable(sku, inventoryContext));
            }
        }

        // the other case here is ALWAYS_AVAILABLE and null, which we are treating as being available
    }

}