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
package org.broadleafcommerce.core.order.service.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.ProductSkuUsage;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
@Component("blCheckAddAvailabilityActivity")
public class CheckAddAvailabilityActivity extends AbstractCheckAvailabilityActivity {

    private static final Log LOG = LogFactory.getLog(CheckAddAvailabilityActivity.class);

    public static final int ORDER = 2000;
    
    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;
    
    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;

    @Value("${enable.weave.use.default.sku.inventory:false}")
    protected boolean enableUseDefaultSkuInventory = false;
    
    public CheckAddAvailabilityActivity() {
        setOrder(ORDER);
    }
    
    @Override
    public ProcessContext<CartOperationRequest> execute(ProcessContext<CartOperationRequest> context) throws Exception {
        CartOperationRequest request = context.getSeedData();
        OrderItemRequestDTO orderItemRequestDTO = request.getItemRequest();
        if (orderItemRequestDTO instanceof NonDiscreteOrderItemRequestDTO){
            return context;
        }
        
        // No order item, this must be a new item add request
        Long skuId = request.getItemRequest().getSkuId();
        Sku sku = catalogService.findSkuById(skuId);

        if(enableUseDefaultSkuInventory && ((ProductSkuUsage) sku.getProduct()).getUseDefaultSkuInInventory()){
            sku = sku.getProduct().getDefaultSku();
        }

        Order order = context.getSeedData().getOrder();
        Integer requestedQuantity = request.getItemRequest().getQuantity();

        Map<Sku, Integer> skuItems = new HashMap<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            Sku skuFromOrder = null;
            if (orderItem instanceof DiscreteOrderItem) {
                skuFromOrder = ((DiscreteOrderItem) orderItem).getSku();
            } else if (orderItem instanceof BundleOrderItem) {
                skuFromOrder = ((BundleOrderItem) orderItem).getSku();
            }

            if(skuFromOrder != null && enableUseDefaultSkuInventory && ((ProductSkuUsage) skuFromOrder.getProduct()).getUseDefaultSkuInInventory()){
                skuFromOrder = skuFromOrder.getProduct().getDefaultSku();
            }

            if (skuFromOrder != null && skuFromOrder.equals(sku)) {
                skuItems.merge(sku, orderItem.getQuantity(), (oldVal, newVal) -> oldVal + newVal);
            }
        }
        skuItems.merge(sku, requestedQuantity, (oldVal, newVal) -> oldVal + newVal);
        for (Map.Entry<Sku, Integer> entry : skuItems.entrySet()) {
            checkSkuAvailability(order, entry.getKey(), entry.getValue());
        }

        return context;
    }

}
