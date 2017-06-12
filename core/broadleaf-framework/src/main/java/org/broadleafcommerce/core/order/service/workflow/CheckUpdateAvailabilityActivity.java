/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.call.NonDiscreteOrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Very similar to the {@link CheckAddAvailabilityActivity} but in the blUpdateItemWorkflow instead
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blCheckUpdateAvailabilityActivity")
public class CheckUpdateAvailabilityActivity extends AbstractCheckAvailabilityActivity {

    private static final Log LOG = LogFactory.getLog(CheckUpdateAvailabilityActivity.class);
    
    public static final int ORDER = 2000;
    
    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;
    
    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;
    
    public CheckUpdateAvailabilityActivity() {
        setOrder(ORDER);
    }
    
    @Override
    public ProcessContext<CartOperationRequest> execute(ProcessContext<CartOperationRequest> context) throws Exception {
        CartOperationRequest request = context.getSeedData();
        OrderItemRequestDTO orderItemRequestDTO = request.getItemRequest();
        if (orderItemRequestDTO instanceof NonDiscreteOrderItemRequestDTO){
            return context;
        }
        
        Sku sku;
        Long orderItemId = request.getItemRequest().getOrderItemId();
        OrderItem orderItem = orderItemService.readOrderItemById(orderItemId);
        if (orderItem instanceof DiscreteOrderItem) {
            sku = ((DiscreteOrderItem) orderItem).getSku();
        } else if (orderItem instanceof BundleOrderItem) {
            sku = ((BundleOrderItem) orderItem).getSku();
        } else {
            LOG.warn("Could not check availability; did not recognize passed-in item " + orderItem.getClass().getName());
            return context;
        }

        Order order = context.getSeedData().getOrder();
        Integer requestedQuantity = request.getItemRequest().getQuantity();
        checkSkuAvailability(order, sku, requestedQuantity);

        Integer previousQty = orderItem.getQuantity();
        for (OrderItem child : orderItem.getChildOrderItems()) {
            Sku childSku = ((DiscreteOrderItem) child).getSku();
            Integer childQuantity = child.getQuantity();
            childQuantity = childQuantity / previousQty;
            checkSkuAvailability(order, childSku, childQuantity * requestedQuantity);
        }

        return context;
    }
}
